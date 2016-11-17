package com.heliomug.bio.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import com.heliomug.bio.Probe;
import com.heliomug.bio.ProbeSet;
import com.heliomug.utils.GlobalStatusDisplayer;

public class ChromosomeRepository {
	private static final int MAX_PROBE_LENGTH = 100;
	
	private BTree byStart;
	
	private boolean isNew;
	private File baseDirectory;
	private String chromosome;
	
	public ChromosomeRepository(String chromo, File baseDirectory, boolean isNew) 
	throws FileNotFoundException, ClassNotFoundException, IOException {
		this.isNew = isNew;
		this.baseDirectory = baseDirectory;
		this.chromosome = chromo;
		
		if (this.isNew) {
			File dir;
			dir = makeSubDirectory(chromosome);
			NodeStore<BTNode> startStore = new NodeStoreHybrid<BTNode>(dir, this.isNew);
			byStart = new BTree(startStore, isNew);
		} else {
			File dir;
			dir = makeSubDirectory(chromosome);
			NodeStore<BTNode> startStore = NodeStoreHybrid.<BTNode>loadFromDisk(dir);
			byStart = new BTree(startStore, isNew);
		}
	}
	
	public String getChromosome() {
		return this.chromosome;
	}
	
	public static ChromosomeRepository loadExistingRepository(String chromo, File baseDirectory) 
	throws FileNotFoundException, ClassNotFoundException, IOException {
		return new ChromosomeRepository(chromo, baseDirectory, false);
	}
	
	public static ChromosomeRepository constructProbeRepositoryFromFile(String chromo, File baseDirectory, File inFile) 
	throws FileNotFoundException, ClassNotFoundException, IOException {
		ChromosomeRepository repo = new ChromosomeRepository(chromo, baseDirectory, true);
		repo.addProbesFromFile(inFile);
		return repo;
	}

	public void addProbesFromFile(File file) throws ClassNotFoundException {
		try (Scanner in = new Scanner(file.toPath(), "UTF-8")) {
			in.nextLine();
			int count = 0;
			while (in.hasNextLine()) {
				Probe p = new Probe(in.nextLine());
				add(p);
				count++;
				if (count % 10_000 == 0) {
					GlobalStatusDisplayer.get().displayStatus(count);
				}
			}
			GlobalStatusDisplayer.get().displayStatus(count + " lines read...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void add(Probe p) throws FileNotFoundException, ClassNotFoundException, IOException {
		byStart.add(p);
	}
	
	public File makeSubDirectory(String subdir) {
		String path = baseDirectory.toPath().toString();
		String[] subDirs = subdir.split(File.separator);
		File newDir = null;
		for (String subDir : subDirs) {
			path += File.separator + subDir;
			newDir = new File(path);
			newDir.mkdir();
		}
		return newDir;
	}
	
	public ProbeSet queryAll() 
	throws InterruptedException, FileNotFoundException, ClassNotFoundException, IOException {
		GlobalStatusDisplayer.get().displayStatus("querying all in " + chromosome + "...");
		ProbeSet results = new ProbeSet();
		byStart.queryAll(results);
		return results;
	}
			
	public ProbeSet query(int startKey, int endKey) 
	throws InterruptedException, FileNotFoundException, ClassNotFoundException, IOException {
		GlobalStatusDisplayer.get().displayStatus("querying " + startKey + " -> " + endKey + " in " + chromosome + "...");
		ProbeSet results = new ProbeSet();
		byStart.query(results, startKey - MAX_PROBE_LENGTH, endKey);
		results.filter(startKey, endKey);
		return results;
	}
	
	public void flush() throws FileNotFoundException, IOException {
		if (isNew) {
			GlobalStatusDisplayer.get().displayStatus("Saving repository for chromosome " + chromosome + " to disk...");
			byStart.flush();
			//byEnd.flush();
			//byInterval.flush();
			GlobalStatusDisplayer.get().displayStatus("Saving for chromosome " + chromosome + " completed...");
		}
	}
}
