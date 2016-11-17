package com.heliomug.bio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import com.heliomug.bio.repository.GenomeRepository;
import com.heliomug.utils.StatusDisplayer;
import com.heliomug.utils.GlobalStatusDisplayer;

/**
 * A loop command line tool to make queries and get results
 * 
 * @author cweidert
 *
 */
public class CommandLineTool {
	private QueriableGenome repo;

	private StatusDisplayer sd;
	
	private CommandLineTool() {
		sd = GlobalStatusDisplayer.get();
	}
	
	private void start() {
		printWelcome();
		runLoop();
	}
	
	private void printWelcome() {
		System.out.println("Welcome to the command line interface for the genome repository.");
		System.out.println("Enter \"help\" for help or \"quit\" to quit.");
	}
	
	private void printHelp() {
		System.out.println("----------------Commands----------------");
		System.out.println("help:  displays help");
		System.out.println("open: loads exising repository");
		System.out.println("new: creates new repository from file");
		System.out.println("query: queries loaded repository");
		System.out.println("quit: quits");
		System.out.println("");
	}
	
	private void quit() {
		System.out.println("Bye!");
		System.exit(0);
	}
	
	private void runLoop() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("? ");
		while (scanner.hasNext()) {
			processLine(scanner.nextLine());
			System.out.print("\n? ");
		}
		scanner.close();
	}
	
	private void processLine(String line) {
		String[] words = line.split("\\s+");
		if (words.length < 1) {
			printWelcome();
		} else {
			String command = words[0];
			if (command.equals("help")) {
				printHelp();
			} else if (command.equals("quit")) {
				quit();
			} else if (command.equals("open") || command.equals("load")) {
				if (words.length == 2) {
					open(words[1]);
				} else {
					System.out.println("open takes exactly one argument (directory path of existing repository)");
				}
			} else if (command.equals("new") || command.equals("create")) {
				if (words.length == 3) {
					create(words[1], words[2]);
				} else {
					System.out.println("new takes exactly two arguments (file for input, directory for output)");
				}
			} else if (command.equals("query")) {
				if (words.length == 2) {
					query(words[1]);
				} else {
					System.out.print("query takes exactly one argument");
					System.out.println("(query of the form \"chr1:12000-chr3:3232423\" or \"chr1:23923-483948\")");
				}
			} else {
				System.out.println("Unknown command \"" + command + "\".  Enter \"help\" for help.");
			}
		}
	}
	
	private void open(String inputPath) {
		File inputDirectory = new File(inputPath);
		if (inputDirectory != null) {
			try {
				sd.displayStatus("Loading repository at " + inputDirectory.getAbsolutePath() + "...");
				repo = GenomeRepository.loadRepository(inputDirectory);
				sd.displayStatus("Repository at " + inputDirectory.getAbsolutePath() + " loaded.");
			} catch (FileNotFoundException e) {
				sd.displayStatus("ERROR: Could not open repository.  File not found.");
				e.printStackTrace();
			} catch (ClassNotFoundException | IOException e) {
				sd.displayStatus("ERROR: Could not open repository");
				e.printStackTrace();
			}
		}
	}
	
	private void create(String inputPath, String outputPath) {
		File inputFile = new File(inputPath);
		File outDir = new File(outputPath);
		if (inputFile != null && outDir != null) {
			try {
				sd.displayStatus("Creating repository at " + outputPath + " from " + inputPath + "...");
				GenomeRepository.createRepository(inputFile, outDir);
				repo = GenomeRepository.loadRepository(outDir);
				sd.displayStatus("Repository created at " + outDir.getAbsolutePath() + ".");
			} catch (ClassNotFoundException | IOException e) {
				sd.displayStatus("ERROR: could not create repository");
				e.printStackTrace();
			}
		}
	}
	
	private void query(String queryString) {
		if (repo != null) {
			try {
				GenomeQuery query = new GenomeQuery(queryString);
				System.out.println(repo.query(query).longString());
			} catch (ClassNotFoundException | InterruptedException | IOException e) {
				e.printStackTrace();
				System.err.println("Could not complete query!");
			} catch (IllegalArgumentException e) {
				System.err.println("Query must be of the form \"^(chr.*):([0-9]+)-((chr.*):)?([0-9]+)$\"");
				System.err.println("e.g. \"chr1:12000-chr3:3232423\" or \"chr1:23923-483948\"");
			}
		} else {
			System.err.println("Must create or load repository before making queries!");
		}
	}
	
	public static void main(String[] args) {
		CommandLineTool clt = new CommandLineTool();
		clt.start();
	}
}
