package com.heliomug.bio.gui;

import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.heliomug.bio.repository.GenomeRepository;

public class QueryWidget extends JPanel {
	private static final long serialVersionUID = -2186486177374055705L;

	private JComboBox<String> chromoSelector;
	private JSpinner offsetSpinner;
	
	public QueryWidget() {
		chromoSelector = new JComboBox<String>();
		offsetSpinner = new JSpinner(new SpinnerNumberModel(0, 0, GenomeRepository.MAX_OFFSET, 1));
		this.add(chromoSelector);
		this.add(offsetSpinner);
	}
	
	public void setChromos(List<String> chromos) {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(chromos.toArray(new String[chromos.size()]));
		chromoSelector.setModel(model);
	}
	
	public int getOffset() {
		return (int)offsetSpinner.getValue();
	}
	
	public String getChromo() {
		return (String)chromoSelector.getSelectedItem();
	}
	
	public boolean isAfter(QueryWidget other) {
		return true;
	}
}
