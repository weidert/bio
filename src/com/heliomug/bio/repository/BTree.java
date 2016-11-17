package com.heliomug.bio.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Function;

import com.heliomug.bio.Probe;
import com.heliomug.bio.ProbeSet;

public class BTree {
    private Function<Probe, Integer> keyFunction;

    private transient NodeStore<BTNode> store;
    
    
    public BTree(NodeStore<BTNode> store, boolean isNew) {
    	this.store = store;
       	keyFunction = (Probe a) -> a.getStart(); 
        if (isNew) {
        	BTNode root = new BTNode(this.store, BTNode.NO_NODE);
            this.store.createNode(root);
            this.store.setRoot(root);
        }
    }

    public void add(Probe p) throws FileNotFoundException, ClassNotFoundException, IOException {
    	store.getRoot().add(keyFunction.apply(p), p);
    }

    public void queryAll(ProbeSet results)
    throws InterruptedException, FileNotFoundException, ClassNotFoundException, IOException {
    	store.getRoot().queryAll(results);
    }
    
    public void query(ProbeSet results, int keyStart, int keyFinish) 
    throws InterruptedException, FileNotFoundException, ClassNotFoundException, IOException {
    	store.getRoot().query(results, keyStart, keyFinish);
    }
    
    public void flush() throws FileNotFoundException, IOException {
    	store.flush();
    }
    
    public String toString() {
        try {
			return store.getRoot().stringFrom();
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't find file to print BTree");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Couldn't find class to print BTree");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Couldn't find file to print BTree");
			e.printStackTrace();
		}
        return "[NO STRING]";
    }
}
