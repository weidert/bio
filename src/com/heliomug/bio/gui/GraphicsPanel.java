package com.heliomug.bio.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.heliomug.bio.ProbeAttribute;
import com.heliomug.bio.ProbeSet;
import com.heliomug.utils.FileUtils;

/**
 * This is a panel to handle the graph of the data and all its controls.  
 * 
 * @author cweidert
 *
 */
public class GraphicsPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -396497902138438977L;

	private GraphPanel illustrationPanel;
	
	private JComboBox<String> chromoSelector;
	private JComboBox<ProbeAttribute> xSelector;
	private JComboBox<ProbeAttribute> ySelector;
	private JComboBox<ProbeAttribute> colorSelector;
	private JButton executeButton; 
	//private JButton cancelButton;

	private ProbeSet results;
	
	public GraphicsPanel(int width, int height) {
		super();
		
		results = null;
		this.illustrationPanel = new GraphPanel(width, height);
		
		this.setBorder(MainProbeQuery.STANDARD_BORDER);
		this.setLayout(new BorderLayout());
		this.add(illustrationPanel, BorderLayout.CENTER);
		
		JPanel subpanel = new JPanel();
		subpanel.setLayout(new BorderLayout());
		JPanel subsubpanel;
		subsubpanel = new JPanel();
		subsubpanel.setLayout(new FlowLayout());
		xSelector = attributeSelector(); 
		subsubpanel.add(selectorPanel(xSelector, "X axis: "));
		ySelector = attributeSelector(); 
		subsubpanel.add(selectorPanel(ySelector, "Y axis: "));
		colorSelector = attributeSelector(); 
		subsubpanel.add(selectorPanel(colorSelector, "Color: "));
		subpanel.add(subsubpanel, BorderLayout.NORTH);
		subsubpanel = new JPanel();
		JPanel subsubsub = new JPanel();
		subsubsub.add(new JLabel("Chromosome: "));
		chromoSelector = new JComboBox<String>();
		subsubsub.add(chromoSelector);
		subsubpanel.add(subsubsub);
		executeButton = new JButton("Show Visualization");
		executeButton.setActionCommand("SHOW GRAPHICS");
		executeButton.setMnemonic(KeyEvent.VK_V);
		executeButton.addActionListener(this);
		executeButton.setEnabled(false);
		subsubpanel.add(executeButton);
		/*
		cancelButton = new JButton("Cancel Visualization");
		cancelButton.setActionCommand("CANCEL GRAPHICS");
		cancelButton.setMnemonic(KeyEvent.VK_L);
		cancelButton.addActionListener(this);
		cancelButton.setEnabled(false);
		subsubpanel.add(cancelButton);
		*/
		subpanel.add(subsubpanel, BorderLayout.SOUTH);
		this.add(subpanel, BorderLayout.SOUTH);
	}
	
	private JPanel selectorPanel(JComboBox<ProbeAttribute> box, String label) {
		JPanel panel = new JPanel();
		panel.add(new JLabel(label));
		panel.add(box);
		return panel;
	}
	
	private JComboBox<ProbeAttribute> attributeSelector() {
		JComboBox<ProbeAttribute> selector = new JComboBox<>();
		DefaultComboBoxModel<ProbeAttribute> model = new DefaultComboBoxModel<>(ProbeAttribute.values());
		selector.setModel(model);
		return selector;
	}

	
	public String saveGraph() throws IOException {
		return FileUtils.saveComponentImage(illustrationPanel, "Save Graph Image As...");
	}
	
	public void setChromosomes(List<String> chromos) {
		String[] resultChromoArray = chromos.toArray(new String[chromos.size()]);
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(resultChromoArray);
		chromoSelector.setModel(model);
	}
	
	public void setResults(ProbeSet results) {
		this.results = results;
		this.executeButton.setEnabled(true);
	}
	
	public void clear() {
		this.results = null;
		executeButton.setEnabled(false);
		illustrationPanel.clear();
		repaint();
	}
	
	private void showGraphics() {
		if (results != null) {
			executeButton.setEnabled(false);
			//cancelButton.setEnabled(true);
			illustrationPanel.fillFromResults(
				results, 
				(String)chromoSelector.getSelectedItem(),
				(ProbeAttribute)xSelector.getSelectedItem(),
				(ProbeAttribute)ySelector.getSelectedItem(),
				(ProbeAttribute)colorSelector.getSelectedItem()
			);
			repaint();
			executeButton.setEnabled(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("SHOW GRAPHICS")) {
			new Thread(() -> {
				showGraphics();
			}).start();
		} 
	}
}
