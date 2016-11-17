package com.heliomug.bio.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public interface StandardPanel {
	public static final Border STANDARD_BORDER = BorderFactory.createLineBorder(Color.BLACK, 1);
	public static final int MARGIN = 20;
	public static final Font MONOSPACE_STD = new Font("monospaced", Font.PLAIN, 12);
}
