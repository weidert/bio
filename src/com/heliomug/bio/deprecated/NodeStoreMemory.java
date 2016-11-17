package com.heliomug.bio.deprecated;

import java.util.Map;
import java.util.TreeMap;

import com.heliomug.bio.repository.Node;
import com.heliomug.bio.repository.NodeStore;

public class NodeStoreMemory<T extends Node> implements NodeStore<T> {
	Map<Long, T> map;
	
	T root;
	
	public NodeStoreMemory() {
		map = new TreeMap<Long, T>(); 
	}
	
	@Override
	public void createNode(T n) {
		map.put(n.getId(), n);
	}

	@Override
	public T readNode(long id) {
		return map.get(id);
	}
	
	@Override
	public void markUsed(T n) {
		// don't have to do anything because the node is updated in memory;
	}

	@Override 
	public void deleteNode(long id) {
		map.remove(id);
	}
	
	@Override
	public void setRoot(T root) {
		this.root = root;
	}
	
	@Override 
	public T getRoot() {
		return this.root;
	}
	
	@Override
	public void flush() {
		// don't do anything since nothing is persistant;
	}
}
