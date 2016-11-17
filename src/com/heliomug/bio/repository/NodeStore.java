package com.heliomug.bio.repository;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface NodeStore<T extends Node> {
    void createNode(T n);
    T readNode(long id) throws FileNotFoundException, ClassNotFoundException, IOException;
    void markUsed(T n);
    void deleteNode(long id);
    void flush() throws FileNotFoundException, IOException;
    void setRoot(T n);
    T getRoot() throws FileNotFoundException, ClassNotFoundException, IOException;
}
