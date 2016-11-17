package com.heliomug.bio.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.heliomug.utils.FileUtils;

public class NodeStoreHybrid<T extends Node> implements NodeStore<T>, Serializable {
	private static final long serialVersionUID = -3713484245628952274L;

	private static final String DEFAULT_FORMAT = "%012d.node";
	private static final String TREE_INFO_FILE_NAME = "treeInfo.tree";
	
	private transient Map<Long, T> entryMap;
	
	private boolean isNew;
	private File baseDirectory;
	private long rootId;
	
	public NodeStoreHybrid(File baseDirectory, boolean isNew) {
		this.isNew = isNew;
		this.baseDirectory = baseDirectory;
		entryMap = new HashMap<Long, T>();
	}
	
	public static <T extends Node> NodeStoreHybrid<T> loadFromDisk(File directory) 
	throws FileNotFoundException, ClassNotFoundException, IOException {
		String path = directory.getPath() + File.separator + TREE_INFO_FILE_NAME;
		@SuppressWarnings("unchecked")
		NodeStoreHybrid<T> store = (NodeStoreHybrid<T>)FileUtils.readObject(new File(path));
		store.entryMap = new HashMap<Long, T>();
		store.isNew = false;
		return store;
	}
	
	@Override
	public void createNode(T node) {
		entryMap.put(node.getId(), node);
	}

	@Override
	public T readNode(long id) throws FileNotFoundException, ClassNotFoundException, IOException {
		if (entryMap.containsKey(id)) {
			return entryMap.get(id);
		} else {
			T node = readFromDisk(id);
			entryMap.put(node.getId(), node);
			return node;
		}
	}

	@Override
	public void markUsed(T n) {
		// everything in memory
	}

	@Override
	public void deleteNode(long id) {
		if (!isNew) {
			entryMap.remove(id);
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
	public void flush() throws FileNotFoundException, IOException {
		if (isNew) {
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
	public T getRoot() throws FileNotFoundException, ClassNotFoundException, IOException {
		return readNode(rootId);
	}
	
	private String getPath(long id) {
		String fileString = String.format(DEFAULT_FORMAT, id);
		String path = baseDirectory.getPath() + File.separator + fileString;
		return path;
	}
	
	@SuppressWarnings("unchecked")
	private T readFromDisk(long id) throws FileNotFoundException, ClassNotFoundException, IOException {
		String path = getPath(id);
		Object obj = FileUtils.readObject(path);
		T node = (T) obj;
		node.setStore(this);
		return node;
	}
	
	private void writeToDisk(T node) throws FileNotFoundException, IOException {
		String path = getPath(node.getId());
		FileUtils.saveObject(node, path);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(entryMap.size() + " nodes stored:");
		for (Long id : entryMap.keySet()) {
			sb.append(" " + id);
		}
		return sb.toString();
	}	
}
