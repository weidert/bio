package com.heliomug.bio.repository;

public interface Node {
	long getId();
	void setStore(NodeStore<?> store);
}
