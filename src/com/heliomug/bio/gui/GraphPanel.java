package com.heliomug.bio.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.function.Function;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.heliomug.bio.Probe;
import com.heliomug.bio.ProbeAttribute;
import com.heliomug.bio.ProbeSet;
import com.heliomug.utils.DataSet;
import com.heliomug.utils.StatusDisplayer;
import com.heliomug.utils.GlobalStatusDisplayer;

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
	private Function<Probe, Double> xFxn;
	private Function<Probe, Double> yFxn;
	private Function<Probe, Double> zFxn;
	
	private int specialProbeIndex;
	
	public GraphPanel(int xSize, int ySize) {
		super();
		results = null;
		specialProbeIndex = -1;
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Probe p = getNearProbe(e.getX(), e.getY());
				if (p != null) {
					StatusDisplayer sd = GlobalStatusDisplayer.get();
					sd.displayStatus(p.toString());
				}
			}
		});
		this.setBackground(Color.WHITE);
		this.setBorder(ProbeQueryMachineGUI.STANDARD_BORDER);

		this.setPreferredSize(new Dimension(xSize, ySize));
	}

	public void fillFromResults(
			ProbeSet results, 
			String chromo,
			Function<Probe, Double> xFxn, 
			Function<Probe, Double> yFxn, 
			Function<Probe, Double> zFxn
	){
		this.results = results.filterByChromo(chromo);
		this.xFxn = xFxn;
		this.yFxn = yFxn;
		this.zFxn = zFxn;
	}

	private double getPixX(double x) {
		return x * (getWidth() - 2 * MARGIN) + MARGIN;
	}
	
	private double getPixY(double y) {
		// y axis is upside down for plotting
		return (1 - y) * (getHeight() - 2 * MARGIN) + MARGIN;
	}
	
	public Probe getNearProbe(int xLoc, int yLoc) {
		if (results != null) {
			double[] xNorm = normZeroOne(xFxn);
			double[] yNorm = normZeroOne(yFxn);
			for (int i = xNorm.length - 1 ; i >= 0; i--) {
				double xP = getPixX(xNorm[i]);
				double yP = getPixY(yNorm[i]);
				double xDist = (xLoc - xP);
				double yDist = (yLoc - yP);
				double distSq = xDist * xDist + yDist * yDist;
				if (distSq < ELLIPSE_RADIUS * ELLIPSE_RADIUS) {
					specialProbeIndex = i;
					return results.get(i);
				}
			}
		}
		return null;
	}
	
	private double[] normZeroOne(Function<Probe, Double> fxn) {
		double[] toRet = new double[results.size()];
		for (int i = 0 ; i < results.size() ; i++) {
			toRet[i] = fxn.apply(results.get(i));
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
		xFxn = null;
		yFxn = null;
		zFxn = null;
		specialProbeIndex = -1;
		repaint();
	}
	
	@Override
	public void paint(Graphics gee) {
		super.paint(gee);
		Graphics2D g = (Graphics2D)gee;
		
		if (results != null) {
			if (specialProbeIndex < 0) {
				ProbeQueryMachineGUI.get().displayStatus("Starting Graph Drawing...");
			}
			double[] xNorm = normZeroOne(xFxn);
			double[] yNorm = normZeroOne(yFxn);
			double[] zNorm = normZeroOne(zFxn);
			for (int i = 0 ; i < results.size() ; i++) {
				if (i == specialProbeIndex) {
					g.setColor(HIGHLIGHT_COLOR);
				} else {
					g.setColor(getColor(zNorm[i]));
				}
				Shape s = getPoint(xNorm[i], yNorm[i]);
				g.fill(s);
				g.setColor(Color.BLACK);
				g.draw(s);
			}
			if (specialProbeIndex < 0) {
				ProbeQueryMachineGUI.get().displayStatus("Graph drawing complete.  Click on a point for more info");
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
		ProbeSet results = new ProbeSet();
		for (int i = 0 ; i < 10 ; i++) {
			results.add(new Probe("X", i, i + 2 + (i % 4), Math.random()));
		}
		System.out.println(results);
		
		GraphPanel panel = new GraphPanel(300, 300);
		panel.fillFromResults(results, "X", ProbeAttribute.START, ProbeAttribute.LENGTH, ProbeAttribute.VALUE);
		EventQueue.invokeLater(() -> {
			JFrame frame = new JFrame("CloudTest");
			frame.add(panel);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		});
	}

}
