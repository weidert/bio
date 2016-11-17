package com.heliomug.bio.deprecated;
/*
package com.heliomug.biodiscovery.deprecated;

import java.util.ArrayList;
import java.util.List;

import com.heliomug.biodiscovery.Probe;
import com.heliomug.biodiscovery.ProbeSet;
import com.heliomug.biodiversity.repository.NodeStore;

public class IntervalTree {
	private static final int MAX_OFFSET = 1_000_000_000;
	
    private NodeStore<ITNode> store;
    
    public IntervalTree(NodeStore<ITNode> store, boolean isNew) {
        this.store = store;
		if (isNew) {
			ITNode root = new ITNode(store, 0, MAX_OFFSET);
			store.createNode(root);
			store.setRoot(root);
		}
    }

    public void query(ProbeSet results, int key) {
    	store.getRoot().query(results, key);
    }
    
    public void add(Probe p) {
        store.getRoot().insert(p);
    }
    
    public void flush() {
    	store.flush();
    }
    
    public String toString() {
        return store.getRoot().recursiveString(); 
    }

    public static void maing(String[] args) {
        List<Probe> li = new ArrayList<Probe>();
        NodeStore<ITNode> store = new NodeStoreMemory<ITNode>();
        IntervalTree tree = new IntervalTree(store, false);
        String chromo = "X";
        for (int i = 0 ; i < 2000 ; i++) {
            li.add(new Probe(chromo, i, i + 100, .5));
        }
        for (Probe ele : li) {
            tree.add(ele);
        }
        
        System.out.println(tree);

        ProbeSet results = new ProbeSet();
        tree.query(results, 15);
        System.out.println("query results: " + results.size());
        System.out.println(results);
        tree.flush();
    }
    
}
*/
