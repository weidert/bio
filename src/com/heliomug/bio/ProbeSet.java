package com.heliomug.bio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class keeps a list of probes, typically as the result of a query
 * 
 * 
 * 
 * @author cweidert
 *
 */
public class ProbeSet implements Iterable<Probe>{
	private Set<Probe> probeSet;
	private List<Probe> probeList;
	private boolean listUpToDate;
	
	public ProbeSet() {
		probeSet = new HashSet<Probe>();
		listUpToDate = false;
	}
	
	public void add(Probe p) {
		probeSet.add(p);
		listUpToDate = false;
	}
	
	public void addAll(ProbeSet other) {
		addAll(other.probeSet);
	}
	
	public void addAll(Collection<Probe> c) {
		probeSet.addAll(c);
		listUpToDate = false;
	}
	
	/**
	 * This method filters out all but the elements between start & end inclusive.
	 * Notably, it makes no considerations for the chromosome.   
	 * 
	 * @param start 
	 * @param end
	 */
	public void filter(int start, int end) {
		Set<Probe> filtered = new HashSet<Probe>();
		for (Probe p : probeSet) {
			if (p.overlaps(start, end)) {
				filtered.add(p);
			}
		}
		probeSet = filtered;
		listUpToDate = false;
	}
	
	public ProbeSet filterByChromo(String chromo) {
		ProbeSet set = new ProbeSet();
		for (Probe p : probeSet) {
			if (p.getChromosome().equals(chromo)) set.add(p);
		}
		return set;
	}
	
	public int size() {
		return probeSet.size();
	}
	
	public Probe get(int i) {
		List<Probe> li = getList();
		if (i < 0 || i >= size()) {
			return null;
		} else {
			return li.get(i);
		}
	}
	
	/**
	 * @return The probes in sorted list form.  
	 */
	public List<Probe> getList() {
		if (!listUpToDate) {
			probeList = new ArrayList<Probe>(probeSet);
			probeList.sort((Probe a, Probe b) -> {
				Comparator<String> chromoComp = Probe.getChromoComparator();
				int chromoDiff = chromoComp.compare(a.getChromosome(), b.getChromosome());
				if (chromoDiff != 0) {
					return chromoDiff;
				} else {
					int diff = a.getStart() - b.getStart();
					return diff == 0 ? a.getEnd() - b.getEnd() : diff;
				}
			}); 
			listUpToDate = true;
		}
		return probeList;
	}
	
	public List<String> getChromoList() {
		Set<String> set = new HashSet<>();
		for (Probe p : probeSet) {
			set.add(p.getChromosome());
		}
		List<String> li = new ArrayList<>(set);
		li.sort(Probe.getChromoComparator());
		return li;
	}
	
	public double[] extractFeature(ProbeAttribute attr) {
		List<Probe> li = getList();
		double[] toRet = new double[size()];
		for (int i = 0 ; i < size() ; i++) {
			toRet[i] = attr.apply(li.get(i));
		}
		return toRet;
	}
	
	/**
	 * @param resultLimit This puts a bound on the number of results to include.  
	 * @return String representation, tab separated, of the results.  
	 */
	public String longString(int resultLimit) {
		StringBuilder sb = new StringBuilder();
		int num = (int)Math.min(resultLimit, this.size());
		int digits = (int)Math.ceil(Math.log10(num)) + 1;

		sb.append("#\tChromosome\tStart\tEnd\tValue\n");
		String fmt = "%"+digits+"d\t%s\t%d\t%d\t%f\n";
		
		int count = 0;
		for (Probe p : getList()) {
			count++;
			sb.append(String.format(fmt, count, p.getChromosome(), p.getStart(), p.getEnd(), p.getValue()));
			if (count >= resultLimit) {
				sb.append("[truncated at " + resultLimit + " results]\n");
				break;
			}
		}
		return sb.toString();
	}
	
	public String longString() {
		return longString(Integer.MAX_VALUE);
	}
	
	public String toString() {
		return getList().toString();
	}

	@Override
	public Iterator<Probe> iterator() {
		return getList().iterator();
	}
	

}
