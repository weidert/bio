package com.heliomug.bio.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.heliomug.bio.GenomeQuery;
import com.heliomug.bio.Probe;
import com.heliomug.bio.ProbeSet;
import com.heliomug.bio.QueriableGenome;
import com.heliomug.utils.FileUtils;
import com.heliomug.utils.GlobalStatusDisplayer;

public class GenomeRepository implements QueriableGenome, Serializable {
	private static final long serialVersionUID = -4433104723700695892L;

	private static final String REPOSITORY_FILE_NAME = "genomeRepository.repo";
	private static final Probe DUMMY_PROBE = new Probe("DUMMY	0	0	0.0	0");
	private static final int MAX_PROBES_IN_CHROMO_QUEUE = 1_000_000;
	private static final int STATUS_DISPLAY_MOD = 100_000;
	public static final int MAX_OFFSET = 1_000_000_000;
	
	private transient Map<String, ChromosomeRepository> repoMap;
	
	private List<String> chromoList;
	private File baseDirectory;
	
	public GenomeRepository(File baseDirectory) {
		this.baseDirectory = baseDirectory;
		if (!baseDirectory.exists()) baseDirectory.mkdir();
		repoMap = new HashMap<>();
		chromoList = new ArrayList<>();
	}

	public static GenomeRepository createRepository(File inputFile, File baseDirectory) 
	throws FileNotFoundException, IOException, ClassNotFoundException {
		GenomeRepository repo = new GenomeRepository(baseDirectory);
		
		Runnable fileReader = () -> {
			Map<String, BlockingQueue<Probe>> probeMap = new HashMap<>();;
			try (Scanner in = new Scanner(inputFile.toPath(), "UTF-8")) {
				// skip header line
				in.nextLine();
				int count = 0;
				String oldChromo = "";

				List<Thread> fillerThreads = new ArrayList<>();
				
				while (in.hasNextLine()) {
					String line = in.nextLine();
					Probe p = new Probe(line);
					String chromo = p.getChromosome();
					if (!repo.chromoList.contains(chromo)) {
						GlobalStatusDisplayer.get().displayStatus("Read new chromosome " + chromo + "...");

						if (oldChromo != "") probeMap.get(oldChromo).put(DUMMY_PROBE);
						
						repo.chromoList.add(chromo);
						probeMap.put(chromo, new ArrayBlockingQueue<Probe>(MAX_PROBES_IN_CHROMO_QUEUE));
						try {
							repo.repoMap.put(chromo, new ChromosomeRepository(chromo, baseDirectory, true));
						} catch (ClassNotFoundException e) {
							
							e.printStackTrace();
						}
						Thread t = new Thread(repo.new ChromoRepoFiller(chromo, probeMap.get(chromo))); 
						t.start();
						fillerThreads.add(t);
					} 
					probeMap.get(chromo).put(p);
					count++;
					if (count % STATUS_DISPLAY_MOD == 0) {
						GlobalStatusDisplayer.get().displayStatus(count + " lines read...");
					}
					oldChromo = chromo; 
				}

				for (BlockingQueue<Probe> queue : probeMap.values()) {
					queue.put(DUMMY_PROBE);
				}
				
				for (Thread t : fillerThreads) {
					t.join();
				}
				
				GlobalStatusDisplayer.get().displayStatus("Finished processing file!");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.err.println("File reading interrupted!");
				e.printStackTrace();
			}
		};
	
		Thread t = new Thread(fileReader);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			System.err.println("genome repo creation interrupted");
			e.printStackTrace();
		}
		
		repo.flush();
		
		return repo;
	}
	
	public static GenomeRepository loadRepository(File baseDirectory) 
	throws FileNotFoundException, ClassNotFoundException, IOException {
		String path = baseDirectory + File.separator + REPOSITORY_FILE_NAME;
		GenomeRepository repo =  (GenomeRepository) FileUtils.readObject(path);
		repo.repoMap = new HashMap<>();
		for (String chromo : repo.chromoList) {
			ChromosomeRepository chromoRepo = ChromosomeRepository.loadExistingRepository(chromo, baseDirectory);
			repo.repoMap.put(chromo, chromoRepo);
		}
		return repo;
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}
	
	public void flush() throws FileNotFoundException, IOException {
		// chromosome repositories must have save themselves by now!
		String path = baseDirectory + File.separator + REPOSITORY_FILE_NAME;
		FileUtils.saveObject(this, path);
	}
	
	public List<String> getChromoList() {
		chromoList.sort(Probe.getChromoComparator());;
		return chromoList;
	}
	
	public ProbeSet query(GenomeQuery query) 
	throws InterruptedException, FileNotFoundException, ClassNotFoundException, IOException {
		getChromoList();
		ProbeSet results = new ProbeSet();
		String startChromo = query.getStartChromo();
		String endChromo = query.getEndChromo();
		int start = query.getStartOffset();
		int end = query.getEndOffset();
		
		int startChromoInd = chromoList.indexOf(startChromo);
		int endChromoInd = chromoList.indexOf(endChromo);
		if (startChromoInd == endChromoInd) {
			results = repoMap.get(chromoList.get(startChromoInd)).query(start, end);
		} else if (endChromoInd > startChromoInd) {
			results = repoMap.get(chromoList.get(startChromoInd)).query(start, MAX_OFFSET);
			for (int i = startChromoInd + 1 ; i < endChromoInd ; i++) {
				results.addAll(repoMap.get(chromoList.get(i)).queryAll());
			}
			results.addAll(repoMap.get(chromoList.get(endChromoInd)).query(0, end));
		}
		return results;
	}

	private class ChromoRepoFiller implements Runnable {
		private String chromosome;
		private BlockingQueue<Probe> probeQ;
		
		public ChromoRepoFiller(String chromo, BlockingQueue<Probe> probeQ) {
			this.chromosome = chromo;
			this.probeQ = probeQ;
		}
		
		@Override
		public void run() {
			try {
				boolean done = false;
				while (!done) {
					Probe p = probeQ.take();
					if (p == DUMMY_PROBE) {
						done = true;
						repoMap.get(chromosome).flush();
						repoMap.remove(chromosome);
					} else {
						repoMap.get(chromosome).add(p);
					}
				}
			} catch (InterruptedException | ClassNotFoundException | IOException e) {
				GlobalStatusDisplayer.get().displayStatus("ERROR: not able to fill chromosome " + chromosome + "!");
				e.printStackTrace();
			}
			GlobalStatusDisplayer.get().displayStatus("filler thread for chromosome " + chromosome + " finishing...");
		}
	}
	
	
	public static void maing(String[] args) 
	throws InterruptedException, FileNotFoundException, ClassNotFoundException, IOException {
		File baseDirectory = new File("/home/cweidert/prog/data/biodiscovery/repo1");
		//File inputFile = new File("/home/cweidert/prog/data/biodiscovery/probes.txt");
		//GenomeRepository repo = createRepository(inputFile, baseDirectory);
		GenomeRepository repo = loadRepository(baseDirectory);
		//ResultList results = repo.query("chr10", 98590, "chr11", 98590);
		ProbeSet results = repo.query(new GenomeQuery("chr10", 100060, "chr10", 190878));
		System.out.println(results.size() + " results");
		
		for (Probe p : results) {
			System.out.println(p);
		}
		
		System.out.println(repo.chromoList);
	}
}
