package com.heliomug.bio.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import com.heliomug.bio.Probe;
import com.heliomug.bio.ProbeSet;

public class BTNode implements Node, Serializable {
	private static final long serialVersionUID = 4531115335716306667L;

	public static final long NO_NODE = -1;
	private static final int CAPACITY = 1024;
	
	private static long nextId;
	
	private Entry[] entries;
	private long[] childIds;
	private long parentId;
	private long id;
	private int size;
	private transient NodeStore<BTNode> store;
	
    static {
    	nextId = 0;
    }
    
    public BTNode(NodeStore<BTNode> store, long parentId) {
        this.store = store;
    	this.entries = new Entry[CAPACITY + 1];
        this.id = BTNode.nextId++;
        this.parentId = parentId;
        this.childIds = new long[CAPACITY + 2];
        for (int i = 0 ; i < childIds.length ; i++) {
        	childIds[i] = NO_NODE;
        }
        store.createNode(this);
    }

    @SuppressWarnings("unchecked")
	@Override
    public void setStore(NodeStore<?> store) {
    	this.store = (NodeStore<BTNode>)store;
    }
    
    @Override
    public long getId() {
    	return this.id;
    }
    
    public void add(int key, Probe probe) throws FileNotFoundException, ClassNotFoundException, IOException {
        add(new Entry(key, probe));
    }

    private void add(Entry entry) throws FileNotFoundException, ClassNotFoundException, IOException {
    	int index = getInsertionIndexBack(entry.key); 
    	long childId = childIds[index]; 
    	if (childId == NO_NODE) {
            insertIntoThisNode(entry, index);
            maybeSplit();
            store.markUsed(this);
    	} else {
    		store.readNode(childId).add(entry);
    	}
    }

    private void maybeSplit() throws FileNotFoundException, ClassNotFoundException, IOException {
        if (size > CAPACITY) {
        	split();
        }
    }
    
    private void split() throws FileNotFoundException, ClassNotFoundException, IOException {
    	if (parentId == NO_NODE) {
            BTNode newRoot = new BTNode(store, NO_NODE);
            newRoot.childIds[0] = this.id;
            parentId = newRoot.getId();
            store.createNode(newRoot);
            store.markUsed(newRoot);
            store.setRoot(newRoot);
            store.markUsed(this);
    	}
        
        int mid = entries.length / 2;

        // make a new right node and give it half the entries
        BTNode right = new BTNode(store, this.parentId);
        for (int i = mid + 1 ; i < entries.length ; i++) {
            right.entries[i - mid - 1] = entries[i];
            right.childIds[i - mid - 1] = childIds[i];
            right.size++;
            entries[i] = null;
            childIds[i] = NO_NODE;
            size--;
        }
        right.childIds[right.entries.length - mid - 1] = childIds[right.entries.length];
        childIds[right.entries.length] = NO_NODE;
        store.createNode(right);
        store.markUsed(right);

        
        store.readNode(parentId).insertFromBelow(entries[mid], right.id); 
        entries[mid] = null;
        size--;
        store.markUsed(this);
    }
    
    public void queryAll(ProbeSet results)
    throws InterruptedException, FileNotFoundException, ClassNotFoundException, IOException {
    	Thread.sleep(1);
    	for (Entry entry : entries) {
    		if (entry != null) results.add(entry.probe);
    	}
    	for (long childId : childIds) {
    		if (childId != NO_NODE) {
    			store.readNode(childId).queryAll(results);
    		}
    	}
    }
    
    public void query(ProbeSet results, int keyStart, int keyFinish) 
    throws InterruptedException, FileNotFoundException, ClassNotFoundException, IOException {
    	Thread.sleep(1);
    	int first = getInsertionIndexFront(keyStart);
    	int last = getInsertionIndexBack(keyFinish) - 1;
    	
    	BTNode child;
    	if (childIds[first] != NO_NODE) {
    		child = store.readNode(childIds[first]);
    		child.query(results, keyStart, keyFinish);
    	}
    	for (int i = first ; i <= last ; i++) {
    		results.add(entries[i].probe);
        	if (childIds[i] != NO_NODE) {
        		child = store.readNode(childIds[i + 1]);
        		child.query(results, keyStart, keyFinish);
        	}
    	}
    }
    
    private int getInsertionIndexFront(int key) {
        int min = 0;
        int max = size;
        while (max != min) {
            int mid = (min + max) / 2;
            if (entries[mid] == null || key <= entries[mid].key) {
                max = mid;
            } else {
                min = mid + 1;
            }
        }
        return min;
    }

    private int getInsertionIndexBack(int key) {
        int min = 0;
        int max = size;
        while (max != min) {
            int mid = (min + max) / 2;
            if (entries[mid] == null || key < entries[mid].key) {
                max = mid;
            } else {
                min = mid + 1;
            }
        }
        return min;
    }

    private void insertIntoThisNode(Entry entry, int index) {
        // inefficient!  should use a tree here!  maybe laterrr!
    	for (int i = size; i > index ; i--) {
            entries[i] = entries[i - 1];
            childIds[i + 1] = childIds[i];
        }
        entries[index] = entry;
        size++;
    }

    private void insertFromBelow(Entry entry, long rightChildId) 
    throws FileNotFoundException, ClassNotFoundException, IOException {
    	int index = getInsertionIndexBack(entry.key); 
    	insertIntoThisNode(entry, index);
    	childIds[index + 1] = rightChildId;
    	maybeSplit();
    	store.markUsed(this);
    }
    
    public String stringFrom() throws FileNotFoundException, ClassNotFoundException, IOException {
    	return stringFrom(0);
    }
    
    private String stringFrom(int level) throws FileNotFoundException, ClassNotFoundException, IOException {
    	String padding = new String(new char[level]).replace("\0", "   ");
    	StringBuilder sb = new StringBuilder();
    	//sb.append(padding + "node #" + this.id + ": " + this.toString() + "\n");
    	//sb.append(padding + Arrays.toString(childIds) + "\n");
    	if (childIds[0] != NO_NODE) {
    		sb.append(store.readNode(childIds[0]).stringFrom(level + 1));
    	}
    	for (int i = 0 ; i < entries.length ; i++) {
        	Entry entry = entries[i]; 
    		if (entry != null) sb.append(padding + entry.probe.getStart() + "\n");
    		long id = childIds[i + 1]; 
    		if (id != NO_NODE) {
        		sb.append(store.readNode(id).stringFrom(level + 1));
        	}
    	}
    	return sb.toString();
    }
    
    public String toString() {
        if (entries[0] == null) return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append("[" + entries[0].key);
        for (int i = 1 ; i < entries.length ; i++) {
            if (entries[i] == null) break;
            sb.append(", " + entries[i].key);
        }
        sb.append("]");
        return sb.toString();
    }
    
    private class Entry implements Serializable {
		private static final long serialVersionUID = -5131547739926246573L;

		public int key;
    	public Probe probe;
    	
    	public Entry(int key, Probe probe) {
    		this.key = key;
    		this.probe = probe;
    	}
    	
    	public String toString() { return probe.toString(); }
    }
}

