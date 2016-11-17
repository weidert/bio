package com.heliomug.bio.deprecated;
/*
package com.heliomug.biodiscovery.deprecated;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.heliomug.biodiscovery.Probe;
import com.heliomug.biodiscovery.ProbeSet;
import com.heliomug.biodiversity.repository.Node;
import com.heliomug.biodiversity.repository.NodeStore;

public class ITNode implements Node, Serializable {
	private static final long serialVersionUID = -5244252753789139912L;

	/*
	private final long NO_CHILD = -1;
	
	private static long nextId;
	
	private int min, mid, max;

    private int size;

    private long id;
    private long leftId;
    private long rightId;

    private List<Probe> byStart;
    private List<Probe> byEnd;
    
    private boolean isSorted;
    
    private transient NodeStore<ITNode> store;

    static {
    	nextId = 0;
    }
    
    public ITNode(NodeStore<ITNode> store, int min, int max) {
        this.store = store;
    	this.min = min;
        this.max = max;
        this.mid = (min + max) / 2;
        this.id = nextId++;
        this.leftId = NO_CHILD;
        this.rightId = NO_CHILD;
        this.byStart= new ArrayList<Probe>();
        this.byEnd = new ArrayList<Probe>();
        this.isSorted = true;
    }

    @SuppressWarnings("unchecked")
    @Override
	public void setStore(NodeStore<?> store) {
    	this.store = (NodeStore<ITNode>)store;
    }
    
    public long getId() {
    	return this.id;
    }
    
    public void insert(Probe p) {
    	if (p.contains(mid)) {
            byStart.add(p);
            byEnd.add(p);
            isSorted = false;
            size++;
            store.markUsed(this);
        } else if (p.isBefore(mid)) {
            ITNode left;
        	if (leftId == NO_CHILD) {
                left = new ITNode(store, min, mid - 1);
                store.createNode(left);
                leftId = left.getId(); 
                store.markUsed(this);
            } else {
            	left = store.readNode(leftId);
            }
            left.insert(p);
        } else { // probe is after split 
            ITNode right;
        	if (rightId == NO_CHILD) {
                right = new ITNode(store, mid + 1, max);
                store.createNode(right);
                rightId = right.getId();
                store.markUsed(this);
            } else {
            	right = store.readNode(rightId);
            }
            right.insert(p);
        }
    }

    private static int findStartIndex(List<Probe> li, int key) {
    	int min = 0;
    	int max = li.size() - 1;
    	int mid = (min + max) / 2;
    	while (max > min) {
    		if (li.get(mid).getEnd() >= key) {
    			max = mid;
    		} else {
    			min = mid + 1;
    		}
    	}
    	return min;
    }
    
    private static int findPastEnd(List<Probe> li, int key) {
    	int min = 0;
    	int max = li.size();
    	int mid = (min + max) / 2;
    	while (max - min > 1) {
    		if (li.get(mid).getStart() <= key) {
    			min = mid;
    		} else {
    			max = mid;
    		}
    	}
    	return max;
    }
    
    public void query(ProbeSet results, int key) {
    	if (!isSorted) {
    		byStart.sort((Probe a, Probe b) -> a.getStart() - b.getStart());
    		byEnd.sort((Probe a, Probe b) -> a.getEnd() - b.getEnd());
    		isSorted = true;
    	}
    	if (key == mid) {
    		results.addAll(byStart);
    	} else if (key < mid) {
    		List<Probe> leftResults = byStart.subList(0, findPastEnd(byStart, key));
    		if (leftResults.size() > 0) {
    			results.addAll(leftResults);
    		}
    		if (leftId != NO_CHILD) store.readNode(leftId).query(results, key);
    	} else {
    		List<Probe> rightResults = byEnd.subList(findStartIndex(byEnd, key), byEnd.size());
    		if (rightResults.size() > 0) {
        		results.addAll(rightResults);
    		}
    		if (rightId != NO_CHILD) store.readNode(rightId).query(results, key);
    	}
    }
    
    public String recursiveString() {
    	return stringFrom(0);
    }
    
    private String stringFrom(int level) {
        String pad = new String(new char[level]).replace("\0", " ") + "|";
        String leftString, rightString;
        if (leftId == NO_CHILD) {
            leftString = "";
        } else {
            leftString = store.readNode(leftId).stringFrom(level + 1);
        }
        if (rightId == NO_CHILD) {
            rightString = "";
        } else {
            rightString = store.readNode(rightId).stringFrom(level + 1);
        }
        return leftString + pad + this.toString() + "\n" + rightString;
    }
    
    public String toString() {
        return String.format("%04d:%08d-%08d-%08d (%d)\n%s", id, min, mid, max, size, byStart);
    }
}
*/
