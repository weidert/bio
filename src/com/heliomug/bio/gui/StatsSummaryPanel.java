package com.heliomug.bio.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.heliomug.utils.DataSet;

public class StatsSummaryPanel extends JPanel {
	private static final long serialVersionUID = 7712830295637571583L;

	private JLabel minLabel;
	private JLabel maxLabel;
	private JLabel sizeLabel;
	private JLabel sumLabel;
	private JLabel avgLabel;
	private JLabel ssdLabel;
	private JLabel psdLabel;
	
	public StatsSummaryPanel() {
		super();
		this.setLayout(new GridLayout(4, 2));
		sizeLabel = new JLabel("n: ");
		this.add(sizeLabel);
		this.add(new JLabel(" "));
		avgLabel = new JLabel("avg: ");
		this.add(avgLabel);
		sumLabel = new JLabel("sum: ");
		this.add(sumLabel);
		minLabel = new JLabel("min: ");
		this.add(minLabel);
		maxLabel = new JLabel("max: ");
		this.add(maxLabel);
		ssdLabel = new JLabel("samp sd: ");
		this.add(ssdLabel);
		psdLabel = new JLabel("pop sd: ");
		this.add(psdLabel);
	}
	
	public void clear() {
		sizeLabel.setText("n: ");
		sumLabel.setText("sum: ");
		avgLabel.setText("avg: ");
		minLabel.setText("min: ");
		maxLabel.setText("max: ");
		ssdLabel.setText("samp sd: ");
		psdLabel.setText("pop sd: ");
		repaint();
	}
	
	public void displayStats(DataSet data) {
		sizeLabel.setText("n: " + data.size());
		sumLabel.setText("sum: " + data.sum());
		avgLabel.setText("avg: " + data.mu());
		minLabel.setText("min: " + data.min());
		maxLabel.setText("max: " + data.max());
		ssdLabel.setText("samp sd: " + data.s());
		psdLabel.setText("pop sd: " + data.sigma());
		repaint();
	}
}
