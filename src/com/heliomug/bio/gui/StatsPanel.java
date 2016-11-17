package com.heliomug.bio.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.heliomug.bio.ProbeAttribute;
import com.heliomug.bio.ProbeSet;
import com.heliomug.utils.DataSet;
import com.heliomug.utils.FileUtils;

public class StatsPanel extends JPanel implements ActionListener, StandardPanel {
	private static final long serialVersionUID = 8953512310683255995L;

	HistogramPanel histogramPanel;
	
	JComboBox<ProbeAttribute> selector;
	JSpinner binsSpinner;
	JButton button;
	StatsInfoPanel statsInfoPanel;
	
	ProbeSet results;
	
	
	public StatsPanel(int width, int height) {
		super();
		histogramPanel = new HistogramPanel(height, height);
		results = null;
		
		this.setLayout(new BorderLayout());
		this.setBorder(STANDARD_BORDER);
		
		statsInfoPanel = new StatsInfoPanel();
		this.add(statsInfoPanel, BorderLayout.NORTH);
		this.add(histogramPanel, BorderLayout.CENTER);
		JPanel optionsPanel = new JPanel();
		selector = new JComboBox<>();
		DefaultComboBoxModel<ProbeAttribute> model = new DefaultComboBoxModel<>(ProbeAttribute.values());
		selector.setModel(model);
		optionsPanel.add(new JLabel("Measure to display: "));
		optionsPanel.add(selector);
		optionsPanel.add(new JLabel("Bins:"));
		binsSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
		optionsPanel.add(binsSpinner);
		button = new JButton("Show Stats");
		button.addActionListener(this);
		button.setEnabled(false);
		button.setActionCommand("SHOW STATS");
		optionsPanel.add(button);
		this.add(optionsPanel, BorderLayout.SOUTH);
	}
	
	private DataSet getDataSet() {
		ProbeAttribute attr = ((ProbeAttribute)(selector.getSelectedItem()));
		DataSet data = new DataSet(results.extractFeature(attr));
		return data;
	}
	
	public String saveStatsSummary() throws FileNotFoundException {
		StringBuilder sb = new StringBuilder();
		sb.append("Repository: \t" + MainProbeQuery.get().getRepositoryString() + "\n");
		sb.append("Query: \t" + MainProbeQuery.get().getQueryString() + "\n");
		sb.append(getDataSet().statsSummary());
		return FileUtils.saveTextAs(sb.toString(), "Save Results As");
	}
	
	public void clear() {
		this.results = null;
		button.setEnabled(false);
		histogramPanel.clear();
		statsInfoPanel.clear();
	}
	
	public void setResults(ProbeSet results) {
		this.results = results;
		button.setEnabled(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("SHOW STATS")) {
			new Thread(() -> {
				DataSet data = getDataSet();
				int bins = (int)binsSpinner.getValue();
				histogramPanel.display(data, bins);
				statsInfoPanel.displayStats(data);
				this.repaint();
			}).start();
		}
	}

	public String saveHistogram() throws IOException {
		return FileUtils.saveComponentImage(histogramPanel, "Save Histogram Image as...");
	}
}
