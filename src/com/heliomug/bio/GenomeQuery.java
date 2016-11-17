package com.heliomug.bio;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class keeps track of the start and end points for a query.  
 * 
 * @author cweidert
 *
 */
public class GenomeQuery {
	private String startChromo;
	private String endChromo;
	private int startOffset;
	private int endOffset;
	
	/**
	 * @param startChromo Staring Chromosome String
	 * @param start Staring offset / index (inclusive)
	 * @param endChromo Ending Chromosome String
	 * @param end Ending offset / index (inclusive)
	 */
	public GenomeQuery(String startChromo, int start, String endChromo, int end) {
		this.startChromo = startChromo;
		this.startOffset = start;
		this.endChromo = endChromo;
		this.endOffset = end;
	}
	
	/**
	 * @param line Line of the form "^(chr.*):(\\d+)-((chr.*):)?(\\d+)$"
	 * @throws IllegalArgumentException If not a match, then throw exceptoin
	 */
	public GenomeQuery(String line) throws IllegalArgumentException {
		line = line + "\n";
		String patternString = "^(chr.*):(\\d+)-((chr.*):)?(\\d+)$";
		Pattern pattern = Pattern.compile(patternString);
		
		Matcher m = pattern.matcher(line);
		
		if (m.find()) {
			this.startChromo = m.group(1);
			this.startOffset = Integer.valueOf(m.group(2));
			this.endChromo = m.group(4) == null ? m.group(1) : m.group(4);
			this.endOffset = Integer.valueOf(m.group(5));
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public String getStartChromo() { return this.startChromo; }
	public int getStartOffset() { return this.startOffset; }
	public String getEndChromo() { return this.endChromo; }
	public int getEndOffset() { return this.endOffset; }

	public String toString() {
		return String.format("%s:%d->%s:%d", startChromo, startOffset, endChromo, endOffset);
	}
	
	public static void main(String[] args) {
		String line = "chr1:323-chr2:32438943";
		GenomeQuery q = new GenomeQuery(line);
		System.out.println(q);
	}
}
