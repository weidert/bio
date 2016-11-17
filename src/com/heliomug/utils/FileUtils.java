package com.heliomug.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.CancellationException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

public class FileUtils {
	public static File selectDirectory() {
		return selectDirectory("Select Directory");
	}
	
	public static File selectDirectory(String title) {
		JFileChooser fc = new JFileChooser();
    	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	fc.setDialogTitle(title);
		int response = fc.showOpenDialog(null);

		if (response == JFileChooser.APPROVE_OPTION) {
			File directory = fc.getSelectedFile();
			return directory;
		} 
		
		return null;
    }

    public static File selectFile(String title) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(title);
		int response = fc.showOpenDialog(null);

		if (response == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file;
		} 
		
		return null;
    }
    
    public static File selectFile() {
    	return selectFile("Select File");
    }
    
    public static Object readObject(String path) throws FileNotFoundException, IOException, ClassNotFoundException {
    	return readObject(new File(path));
    }
    
	public static Object readObject(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			Object obj = ois.readObject();
			return obj;
		} 
	}
    
    public static boolean saveObject(Object obj, String path) throws FileNotFoundException, IOException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
			oos.writeObject(obj);
			oos.close();
			return true;
		}
    }
    
    public static String saveTextAs(String text, String title) throws FileNotFoundException, CancellationException {
		File file = FileUtils.selectFile(title);
		if (file == null) throw new CancellationException();
		try (PrintWriter pw = new PrintWriter(file)) {
			pw.println(text);
		}
		return file.getAbsolutePath();
    }
    
    public static String saveTextAs(String text) throws FileNotFoundException {
    	return saveTextAs(text, "Save Text File As...");
    }
    
    public static String saveComponentImage(JComponent comp) throws IOException {
    	return saveComponentImage(comp, "Save Image File As...");
    }
    
	public static String saveComponentImage(JComponent comp, String title) throws CancellationException, IOException {
		File file = FileUtils.selectFile(title);
		if (file == null) throw new CancellationException();
		if (!file.getPath().endsWith(".png")) {
			file = new File(file.getPath() + ".png");
		}
		BufferedImage bi = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_ARGB);
		comp.paint(bi.getGraphics());
		ImageIO.write(bi, "png", file);
		return file.getAbsolutePath();
	}
    
    public static void maing(String[] args) {
    	System.out.println(selectDirectory().getAbsolutePath());
    }
}
