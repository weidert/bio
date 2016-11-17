package com.heliomug.bio.gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.heliomug.bio.ProbeSet;

public class TextPanel extends JPanel implements StandardPanel {
	private static final long serialVersionUID = 2099539043705227472L;

	private static final int TEXT_RESULT_LIMIT = 1000000;

	private ProbeSet results;
	
	private JTextArea textArea; 
	
	public TextPanel() {
		super();
		textArea = new JTextArea(28, 80);
		textArea.setEditable(false);
		textArea.setFont(MONOSPACE_STD);
		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
