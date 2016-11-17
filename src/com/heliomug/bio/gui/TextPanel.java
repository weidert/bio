package com.heliomug.bio.gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.heliomug.bio.ProbeSet;

public class TextPanel extends JPanel implements StandardPanel {
	private static final long serialVersionUID = 2099539043705227472L;

	private static final int TEXT_RESULT_LIMIT = 1000000;
	private static final int TAB_WIDTH = 12;

	private ProbeSet results;
	
	private JTextArea textArea; 
	
	public TextPanel() {
		super();
		textArea = new JTextArea(28, 80);
		textArea.setEditable(false);
		textArea.setTabSize(TAB_WIDTH);
		textArea.setFont(MONOSPACE_STD);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(scrollPane);
	}
	
	public void clear() {
		results = null;
		textArea.setText("");
	}
	
	public void setResults(ProbeSet results) {
		this.results = results;
		new Thread(() -> {
			textArea.setText(this.results.longString(TEXT_RESULT_LIMIT).replaceAll(" \t", ", "));
		}).start();
	}

}
