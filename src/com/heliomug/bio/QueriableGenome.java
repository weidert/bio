package com.heliomug.bio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This is just an interface for what a genome repository should be able to handle
 * 
 * @author cweidert
 *
 */
public interface QueriableGenome {
	ProbeSet query(GenomeQuery query) throws InterruptedException, FileNotFoundException, ClassNotFoundException, IOException;
	File getBaseDirectory();
}
