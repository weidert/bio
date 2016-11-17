package com.heliomug.bio.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import com.heliomug.bio.ProbeAttribute;
import com.heliomug.utils.DataSet;

public class HistogramPanel extends StandardPanel {
	private static final long serialVersionUID = 8953512310683255995L;

	private ProbeAttribute attr;
	private DataSet data;
	private int binCount;
	
	public HistogramPanel(int width, int height) {
		super();
		data = null;
		binCount = 10;
		this.setPreferredSize(new Dimension(width, height));
		this.setBackground(Color.WHITE);
		this.setBorder(STANDARD_BORDER);
	}
	
	public void clear() {
		this.attr = null;
		this.data = null;
		repaint();
	}
	
	public void display(ProbeAttribute attr, DataSet data, int binCount) {
		this.data = data;
		this.attr = attr;
		this.binCount = binCount;
		this.repaint();
	}
	
	@Override
	public void paint(Graphics gee) {
		Graphics2D g = (Graphics2D)gee;
		super.paint(g);

		if (data != null) {
			int[] hist = data.hist(binCount);
			int bins = hist.length;
	
			double max = Double.MIN_VALUE;
			for (int i = 0 ; i < bins ; i++) {
				if (hist[i] > max) max = hist[i];
			}
			
			double barWidth = ((double)(getWidth() - 2 * MARGIN)) / bins; 
			
			for (int i = 0 ; i < bins ; i++) {
				double xStart = (getWidth() - 2 * MARGIN) / bins * i + MARGIN;
				double yStart = (getHeight() - 2 * MARGIN) * (1 - hist[i] / max) + MARGIN;
				double height = getHeight() - yStart - MARGIN;
				g.setColor(Color.BLUE);
				Rectangle2D rect = new Rectangle2D.Double(xStart, yStart, barWidth, height); 
				g.fill(rect);
				g.setColor(Color.BLACK);
				g.draw(rect);
			}
			drawAxis(g, data.min(), data.max(), attr.getName(), false); 
		}
	}
}
