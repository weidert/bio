package com.heliomug.bio.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

public abstract class StandardPanel extends JPanel {
	private static final long serialVersionUID = 1549176498389506743L;

	public static final Border STANDARD_BORDER = BorderFactory.createLineBorder(Color.BLACK, 1);
	public static final Font MONOSPACE_STD = new Font("monospaced", Font.PLAIN, 12);
	public static final Font MONOSPACE_DLX = new Font("monospaced", Font.BOLD, 16);
	public static final int MARGIN = 60;
	public static final int AXIS_POS = MARGIN / 2;
	public static final int LABEL_POS = MARGIN / 6;
	public static final int TITLE_POS = MARGIN / 4;
	public static final int TICK_WIDTH = MARGIN / 8;

	public StandardPanel() {
		super();
	}

	public void drawAxis(Graphics2D g, Object min, Object max, String title, boolean isVertical) {
		if (isVertical) {
			g.drawLine(AXIS_POS - TICK_WIDTH / 2, getHeight() - MARGIN, AXIS_POS + TICK_WIDTH / 2, getHeight() - MARGIN);
			g.drawLine(AXIS_POS - TICK_WIDTH / 2, MARGIN, AXIS_POS + TICK_WIDTH / 2, MARGIN);
			g.drawLine(AXIS_POS, getHeight() - AXIS_POS, AXIS_POS, AXIS_POS);
			drawString(g, MONOSPACE_STD, min, LABEL_POS, getHeight() - MARGIN, true);
			drawString(g, MONOSPACE_STD, max, LABEL_POS, MARGIN, true);
			drawString(g, MONOSPACE_DLX, title, TITLE_POS, getHeight() /2, true);
		} else {
			g.drawLine(AXIS_POS, getHeight() - AXIS_POS, getWidth() - AXIS_POS, getHeight() - AXIS_POS);
			g.drawLine(MARGIN, getHeight() - AXIS_POS - TICK_WIDTH / 2, MARGIN, getHeight() - AXIS_POS + TICK_WIDTH / 2);
			g.drawLine(getWidth() - MARGIN, getHeight() - AXIS_POS - TICK_WIDTH / 2, getWidth() - MARGIN, getHeight() - AXIS_POS + TICK_WIDTH / 2);
			drawString(g, MONOSPACE_STD, min, MARGIN, getHeight() - LABEL_POS);
			drawString(g, MONOSPACE_STD, max, getWidth() - MARGIN, getHeight() - LABEL_POS);
			drawString(g, MONOSPACE_DLX, title, getWidth() / 2, getHeight() - TITLE_POS);
		}
	}
	
	public void drawString(Graphics2D g, Font font, Object text, int x, int y) {
		drawString(g, font, text, x, y, false);
	}
		
	public void drawString(Graphics2D g, Font font, Object text, int x, int y, boolean rotated) {
		FontMetrics metrics = g.getFontMetrics(font);
	    g.setFont(font);
    	if (text.getClass().equals(Double.class)) {
    		double d = (Double)text;
    		if ((int)d == d) {
    			text = (int)d;
    		} else {
    			text = String.format("%.5f", text);
    		}
    	}
	    if (rotated) {
    		AffineTransform orig = g.getTransform();
    		g.rotate(-Math.PI/2);
		    int xOff = metrics.stringWidth(text.toString()) / 2;
		    int yOff = metrics.getAscent() - metrics.getHeight() / 2;
		    int xPos = -y - xOff;
		    int yPos = x + yOff;
		    g.drawString(text.toString(), xPos, yPos);
    		g.setTransform(orig);	
    	} else {
		    int xOff = metrics.stringWidth(text.toString()) / 2;
		    int yOff = metrics.getAscent() - metrics.getHeight() / 2;
		    g.drawString(text.toString(), x - xOff, y + yOff);
    	}
	}
}
