package com.heliomug.bio.deprecated;
/*
package com.heliomug.biodiscovery.deprecated;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import com.heliomug.biodiversity.repository.Node;
import com.heliomug.biodiversity.repository.NodeStore;
import com.heliomug.utils.FileUtils;

public class NodeStoreDisk<T extends Node> implements NodeStore<T>, Serializable {
	private static final long serialVersionUID = 5970563684813544618L;

	/*
	private static final String DEFAULT_FORMAT = "%012d.node";
	private static final String TREE_INFO_FILE_NAME = "treeInfo.tree";
	
	private transient Map<Long, Entry<T>> entryMap;
	private transient PriorityQueue<Entry<T>> oldestQueue;
	
	private boolean isReadOnly;
	private int maxNodesInMemory;
	private File baseDirectory;
	private long rootId;
	
	public NodeStoreDisk(File baseDirectory, boolean isReadOnly, int maxInMem) {
		this.isReadOnly = isReadOnly;
		this.baseDirectory = baseDirectory;
		this.maxNodesInMemory = maxInMem;
		entryMap = new HashMap<Long, Entry<T>>();
		Comparator<Entry<T>> comparator = (Entry<T> a, Entry<T> b) -> {
			return (int)(a.lastUsed - b.lastUsed);
		};		
		oldestQueue = new PriorityQueue<Entry<T>>(comparator);
	}
	
	public static <T extends Node> NodeStoreDisk<T> loadFromMemory(File directory) {
		String path = directory.getPath() + File.separator + TREE_INFO_FILE_NAME;
		@SuppressWarnings("unchecked")
		NodeStoreDisk<T> store = (NodeStoreDisk<T>)FileUtils.readObject(new File(path));
		store.entryMap = new HashMap<Long, Entry<T>>();
		Comparator<Entry<T>> comparator = (Entry<T> a, Entry<T> b) -> {
			return (int)(a.lastUsed - b.lastUsed);
		};		
		store.oldestQueue = new PriorityQueue<Entry<T>>(comparator);
		store.isReadOnly = true;
		return store;
	}
	
	@Override
	public void createNode(T n) {
		Entry<T> entry = new Entry<T>(n, this);
		oldestQueue.add(entry);
		entryMap.put(entry.id, entry);
		if (entryMap.size() > maxNodesInMemory) {
			removeOldest();
		}
		markUsed(n);
	}

	@Override
	public T readNode(long id) {
		if (entryMap.containsKey(id)) {
			return entryMap.get(id).getNode();
		} else {
			T node = readFromDisk(id);
			Entry<T> entry = new Entry<T>(node, this);
			entryMap.put(entry.id, entry);
			oldestQueue.add(entry);
			if (entryMap.size() > maxNodesInMemory) {
				removeOldest();
			}
			return node;
		}
	}

	@Override
	public void markUsed(T n) {
		entryMap.get(n.getId()).updateNode();
	}

	@Override
	public void deleteNode(long id) {
		if (!isReadOnly) {
			entryMap.remove(id);
			oldestQueue.remove(id);
			String path = getPath(id);
			try {
				Files.deleteIfExists(new File(path).toPath());
			} catch (IOException e) {
				System.err.println("Could not delete node file at " + path);
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void flush() {
		if (!isReadOnly) {
			for (Long id : entryMap.keySet()) {
				writeToDisk(entryMap.get(id));
			}
			String path = baseDirectory.getPath() + File.separator + TREE_INFO_FILE_NAME;
			FileUtils.saveObject(this, path);
		}
	}
	
	@Override 
	public void setRoot(T root) {
		this.rootId = root.getId();
	}
	
	@Override
	public T getRoot() {
		return readNode(rootId);
	}
	
	private void updateQueue(Entry<T> entry) {
		// reinsert with updated last use time
		oldestQueue.remove(entry);
		oldestQueue.add(entry);
	}
	
	private void removeOldest() {
		Entry<T> toRemove = oldestQueue.poll();
		entryMap.remove(toRemove.id);
		if (!isReadOnly) writeToDisk(toRemove);
	}
	
	private String getPath(long id) {
		String fileString = String.format(DEFAULT_FORMAT, id);
		String path = baseDirectory.getPath() + File.separator + fileString;
		return path;
	}
	
	@SuppressWarnings("unchecked")
	private T readFromDisk(long id) {
		String path = getPath(id);
		Object obj = FileUtils.readObject(path);
		T node = (T) obj;
		node.setStore(this);
		return node;
	}
	
	private void writeToDisk(Entry<T> entry) {
		String path = getPath(entry.id);
		FileUtils.saveObject(entry.node, path);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(entryMap.size() + " nodes stored:");
		for (Long id : entryMap.keySet()) {
			sb.append(" " + id);
		}
		return sb.toString();
	}
	
	private static class Entry<T extends Node> implements Serializable {
		private static final long serialVersionUID = -516975203332993649L;

		long id;
		T node;
		transient long lastUsed;
		transient NodeStoreDisk<T> store;
		
		public Entry(T n, NodeStoreDisk<T> store) {
			this.id = n.getId();
			this.lastUsed = System.currentTimeMillis();
			this.node = n;
			this.store = store;
		}
		
		public T getNode() {
			lastUsed = System.currentTimeMillis();
			store.updateQueue(this);
			return node;
		}
		
		public void updateNode() {
			lastUsed = System.currentTimeMillis();
			store.updateQueue(this);
		}
	}
}
*/
