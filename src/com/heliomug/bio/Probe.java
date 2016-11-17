package com.heliomug.bio;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class Probe implements Serializable {
	private static final long serialVersionUID = -4124976271370800258L;

	private int start;
    private int end;
    private double value;
    private String chromosome;

    public Probe() {
        start = end = 0;
        value = 0.0;
        chromosome = "";
    }

    public Probe(String chromo, int start, int end, double val) {
        this.start = start;
        this.end = end;
        this.value = val;
        this.chromosome = chromo;
    }

    public Probe(String line) {
    	String[] words = line.split("\\t");
    	String chromo = words[0];
    	int start = Integer.valueOf(words[1]);
    	int end = Integer.valueOf(words[2]);
    	double value = Double.valueOf(words[3]);
    	this.chromosome = chromo;
    	this.start = start;
    	this.end = end;
    	this.value = value;
    }

    @Override
    public int hashCode() {
    	return Objects.hash(start, end, value, chromosome);
    }
    
    @Override
    public boolean equals(Object otherObj) {
    	if (this == otherObj) return true;
    	
    	if (otherObj == null) return false;
    	
    	if (getClass() != otherObj.getClass()) return false;
    	
    	Probe other = (Probe)otherObj;
    	
    	return (start == other.start) && (end == other.end) && (value == other.value) && (chromosome.equals(other.chromosome));
    	
    }
    
    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public int getLength() {
        return this.end - this.start;
    }

    public double getValue() {
        return this.value;
    }

    public String getChromosome() {
        return this.chromosome;
    }


    public boolean contains(int location) {
        return this.start <= location && this.end >= location;
    }

    public boolean isBefore(int location) {
        return this.end < location;
    } 

    public boolean isAfter(int location) {
        return this.start > location;
    }
    
    public boolean overlaps(int start, int end) {
    	return this.start <= end && this.end >= start;
    }
    
    public String toString() {
        return String.format("Chromosome: %s \tStart: %d \tEnd: %d \tValue: %.5f", chromosome, start, end, value);
    }
    
	public static Comparator<String> getChromoComparator() {
		return (String a, String b) -> {
			// if they're numbers after the initial "chr"
			boolean aIsNumerical = a.length() > 3 && a.substring(3).matches("^\\d+$");
			boolean bIsNumerical = a.length() > 3 && a.substring(3).matches("^\\d+$");
			// if they're both numerical, compare based on the numbers
			// otherwise, e.g. chr10 < chr2 because it's lexicographic
			if (aIsNumerical && bIsNumerical) {
				int aNum = Integer.valueOf(a.substring(3));
				int bNum = Integer.valueOf(b.substring(3));
				return aNum - bNum;
			} else if (aIsNumerical && !bIsNumerical) {
				// numbers come before letters always
				return -1;
			} else if (!aIsNumerical && bIsNumerical) {
				// numbers come before letters always
				return 1;
			} else {
				// if both strings, use lexi order
				return a.compareTo(b);
			}
		};
	}

    public static void maing(String[] args) {
    	Probe p = new Probe("chr1	15253	15278	-0.05000932514667511	0");
    	System.out.println(p);
    }
}
