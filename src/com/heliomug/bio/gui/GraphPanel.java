package com.heliomug.bio.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.heliomug.bio.ProbeAttribute;
import com.heliomug.bio.ProbeSet;
import com.heliomug.utils.DataSet;
import com.heliomug.utils.GlobalStatusDisplayer;
import com.heliomug.utils.StatusDisplay;

/**
 * This is a panel to display a graph of the data.  
 * 
 * @author cweidert
 *
 */
public class GraphPanel extends StandardPanel {
	private static final long serialVersionUID = 1452297830452829273L;

	private static final int NO_RESULT_SELECTED = -1;
	
	private static final double ELLIPSE_RADIUS = 5; 
	
	private static final Color HIGHLIGHT_COLOR = Color.BLUE;
	
	private ProbeSet results;

	private int highlightedProbeIndex;
	private List<Shape> points;
	private double[] colorVals;
	private ProbeAttribute xAttr;
	private DataSet xData;
	private DataSet yData;
	private DataSet zData;
	private ProbeAttribute yAttr;
	private ProbeAttribute zAttr;
	
	
	public GraphPanel(int xSize, int ySize) {
		super();
		results = null;
		highlightedProbeIndex = NO_RESULT_SELECTED;
		points = null;
		colorVals = null;
		xData = null;
		yData = null;
		zData = null;
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				highlightClickedProbe(e.getX(), e.getY());
			}
		});
		this.setBackground(Color.WHITE);
		this.setBorder(STANDARD_BORDER);

		this.setPreferredSize(new Dimension(xSize, ySize));
	}

	public void fillFromResults(
			ProbeSet results, 
			String chromo,
			ProbeAttribute xAttr, 
			ProbeAttribute yAttr, 
			ProbeAttribute zAttr
	){
		this.xAttr = xAttr;
		this.yAttr = yAttr;
		this.zAttr = zAttr;
		points = new ArrayList<>();
		this.xData = results.getDataSet(xAttr);
		this.yData = results.getDataSet(yAttr);
		this.zData = results.getDataSet(zAttr);
		this.results = results.filterByChromo(chromo);
		if (results != null) {
			MainProbeQuery.get().displayStatus("Processing points for graph...");
			double[] xNorm = getNormZeroOne(xData);
			double[] yNorm = getNormZeroOne(yData);
			colorVals = getNormZeroOne(results.getDataSet(zAttr));
			for (int i = 0 ; i < results.size() ; i++) {
				Shape s = getPoint(xNorm[i], yNorm[i]);
				points.add(s);
			}
			MainProbeQuery.get().displayStatus("Done processing graph points.");
		}
	}

	private double getPixX(double x) {
		return x * (getWidth() - 2 * MARGIN) + MARGIN;
	}
	
	private double getPixY(double y) {
		// y axis is upside down for plotting
		return (1 - y) * (getHeight() - 2 * MARGIN) + MARGIN;
	}
	
	public void highlightClickedProbe(int xLoc, int yLoc) {
		if (points != null) {
			for (int i = points.size() - 1 ; i >= 0; i--) {
				Shape s = points.get(i);
				if (s.contains(xLoc, yLoc)) {
					highlightedProbeIndex = i;
					StatusDisplay sd = GlobalStatusDisplayer.get();
					sd.displayStatus(results.get(i).toString());
				}
			}
		}
	}
	
	private double[] getNormZeroOne(DataSet data) {
		double[] toRet = new double[data.size()];
		double range = data.range();
		double min = data.min();
		for (int i = 0 ; i < data.size() ; i++) {
			toRet[i] = (data.get(i) - min) / range;
		}
		return toRet;
	}

	public void clear() {
		results = null;
		points = null;
		colorVals = null;
		xAttr = null;
		yAttr = null;
		zAttr = null;
		xData = null;
		yData = null;
		zData = null;
		highlightedProbeIndex = NO_RESULT_SELECTED;
		repaint();
	}
	
	@Override
	public void paint(Graphics gee) {
		super.paint(gee);
		Graphics2D g = (Graphics2D)gee;
		
		if (points != null) {
			for (int i = 0 ; i < points.size() ; i++) {
				if (i == highlightedProbeIndex) {
					g.setColor(HIGHLIGHT_COLOR);
				} else {
					g.setColor(getColor(colorVals[i]));
				}
				Shape s = points.get(i);
				g.fill(s);
				g.setColor(Color.BLACK);
				g.draw(s);
			}

			String title = yAttr.getName() + " vs " + xAttr.getName();
			super.drawString(g, MONOSPACE_DLX, title, getWidth() / 2, MARGIN / 4);
			drawAxis(g, xData.min(), xData.max(), xAttr.getName(), false); 
			drawAxis(g, yData.min(), yData.max(), yAttr.getName(), true); 
			String colorText = "Color (" + zAttr.getName() + "): " + zData.min() + " (green) -> " + zData.max() + "(red)";
			super.drawString(g, MONOSPACE_STD, colorText, getWidth() / 2, MARGIN * 5 / 8);
			
			if (this.highlightedProbeIndex == NO_RESULT_SELECTED) {
				MainProbeQuery.get().displayStatus("Graph drawing complete.  Click on a point for more info.");
			}
		}
	}

	private Color getColor(double x) {
		int red = (int)(x * 255);
		int green = (int)((1 - x) * 255);
		return new Color(red, green, 0);
	}
	
	private Shape getPoint(double x, double y) {
		double xP = getPixX(x);
		// y axis is upside down for plotting
		double yP = getPixY(y);
		double r = ELLIPSE_RADIUS;
		return new Ellipse2D.Double(xP - r, yP - r, r * 2, r * 2);
	}
	
	public static void maing(String[] args) {
		System.out.println(new Rectangle2D.Double(100, 100, 10, 10).contains(105, 105));
	}

}
