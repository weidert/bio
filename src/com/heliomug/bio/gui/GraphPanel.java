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

import javax.swing.JPanel;

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
public class GraphPanel extends JPanel implements StandardPanel {
	private static final long serialVersionUID = 1452297830452829273L;

	private static final double ELLIPSE_RADIUS = 5; 
	
	private static final Color HIGHLIGHT_COLOR = Color.BLUE;
	
	private ProbeSet results;
	/*
	private Function<Probe, Double> xFxn;
	private Function<Probe, Double> yFxn;
	private Function<Probe, Double> zFxn;
	*/
	
	private int highlightedProbeIndex;
	
	private List<Shape> points;
	private double[] colorVals;
	
	public GraphPanel(int xSize, int ySize) {
		super();
		results = null;
		highlightedProbeIndex = -1;
		points = null;
		colorVals = null;
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				highlightClickedProbe(e.getX(), e.getY());
			}
		});
		this.setBackground(Color.WHITE);
		this.setBorder(MainProbeQuery.STANDARD_BORDER);

		this.setPreferredSize(new Dimension(xSize, ySize));
	}

	public void fillFromResults(
			ProbeSet results, 
			String chromo,
			ProbeAttribute xAttr, 
			ProbeAttribute yAttr, 
			ProbeAttribute zAttr
	){
		this.results = results.filterByChromo(chromo);
		points = new ArrayList<>();
		if (results != null) {
			MainProbeQuery.get().displayStatus("Processing points for graph...");
			double[] xNorm = normZeroOne(xAttr);
			double[] yNorm = normZeroOne(yAttr);
			colorVals = normZeroOne(zAttr);
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
	
	private double[] normZeroOne(ProbeAttribute attr) {
		double[] toRet = new double[results.size()];
		for (int i = 0 ; i < results.size() ; i++) {
			toRet[i] = attr.apply(results.get(i));
		}
		double max = new DataSet(toRet).max();
		double min = new DataSet(toRet).min();
		double dist = max - min;
		for (int i = 0 ; i < toRet.length ; i++) {
			toRet[i] = (toRet[i] - min) / dist;
		}
		return toRet;
	}

	public void clear() {
		results = null;
		points = null;
		colorVals = null;
		highlightedProbeIndex = -1;
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
			MainProbeQuery.get().displayStatus("Graph drawing complete.  Click on a point for more info");
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
