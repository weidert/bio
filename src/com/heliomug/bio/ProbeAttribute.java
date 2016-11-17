package com.heliomug.bio;

import java.util.function.Function;

/**
 * This class is basically a list of functions to extract data from the probes
 * 
 * @author cweidert
 *
 */
public enum ProbeAttribute implements Function<Probe, Double>{
	VALUE("Value", p -> p.getValue()),
	START("Start", p -> (double)p.getStart()),
	END("End", p -> (double)p.getEnd()), 
	LENGTH("Length", p -> ((double)p.getLength())),
	NONE("None", p -> 0.0);

	private String name;
	private Function<Probe, Double> fxn;
	
	private ProbeAttribute(String name, Function<Probe, Double> fxn) {
		this.name = name;
		this.fxn = fxn;
	}
	
	public String getName() { 
		return this.name;
	}
	
	public String toString() {
		return this.name();
	}
	
	public Function<Probe, Double> getFunction () {
		return this.fxn;
	}
	
	@Override
	public Double apply(Probe p) {
		return fxn.apply(p);
	}

}
