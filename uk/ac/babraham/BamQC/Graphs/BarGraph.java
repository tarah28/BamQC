/**
 * Copyright Copyright 2014 Bart Ailey Eagle Genomics Ltd
 *
 *    This file is part of BamQC.
 *
 *    BamQC is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    BamQC is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with BamQC; if not, write to the Free Software
 *    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package uk.ac.babraham.BamQC.Graphs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public class BarGraph extends JPanel {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(BarGraph.class);

	private String xLabel;
	private String[] xCategories;
	private double[] data;
	private String graphTitle;
	private double minY;
	private double maxY;
	private double yInterval;
	private int height = -1;
	private int width = -1;

	public BarGraph(double[] data, double minY, double maxY, String xLabel, int[] xCategories, String graphTitle) {
		this(data, minY, maxY, xLabel, new String[0], graphTitle);
		this.xCategories = new String[xCategories.length];

		for (int i = 0; i < xCategories.length; i++) {
			this.xCategories[i] = "" + xCategories[i];
		}
	}

	public BarGraph(double[] data, double minY, double maxY, String xLabel, String[] xCategories, String graphTitle) {
		this.data = data;
		this.minY = minY;
		this.maxY = maxY;
		this.xLabel = xLabel;
		this.xCategories = xCategories;
		this.graphTitle = graphTitle;
		this.yInterval = findOptimalYInterval(maxY);
	}

	private double findOptimalYInterval(double max) {
		int base = 1;
		double[] divisions = new double[] { 1, 2, 2.5, 5 };

		while (true) {

			for (int d = 0; d < divisions.length; d++) {
				double tester = base * divisions[d];
				if (max / tester <= 10) {
					return tester;
				}
			}
			base *= 10;
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(800, 600);
	}

	public Dimension getMinimumSize() {
		return new Dimension(100, 200);
	}

	public int getHeight() {
		if (height < 0) {
			return super.getHeight();
		}
		return height;
	}

	public int getWidth() {
		if (width < 0) {
			return super.getWidth();
		}
		return width;
	}

	public void paint(Graphics g, int width, int height) {
		this.height = height;
		this.width = width;
		paint(g);
		this.height = -1;
		this.width = -1;
	}

	public void paint(Graphics g) {
		super.paint(g);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);

		int lastY = 0;

		double yStart;

		if (minY % yInterval == 0) {
			yStart = minY;
		}
		else {
			yStart = yInterval * (((int) minY / yInterval) + 1);
		}

		int xOffset = 0;

		for (double i = yStart; i <= maxY; i += yInterval) {
			String label = "" + i;
			label = label.replaceAll(".0$", ""); // Don't leave trailing .0s
													// where we don't need them.
			int width = g.getFontMetrics().stringWidth(label);
			if (width > xOffset) {
				xOffset = width;
			}

			g.drawString(label, 2, getY(i) + (g.getFontMetrics().getAscent() / 2));
		}

		// Give the x axis a bit of breathing space
		xOffset += 5;

		// Draw the graph title
		int titleWidth = g.getFontMetrics().stringWidth(graphTitle);
		g.drawString(graphTitle, (xOffset + ((getWidth() - (xOffset + 10)) / 2)) - (titleWidth / 2), 30);

		// Now draw the axes
		g.drawLine(xOffset, getHeight() - 40, getWidth() - 10, getHeight() - 40);
		g.drawLine(xOffset, getHeight() - 40, xOffset, 40);

		// Draw the xLabel under the xAxis
		g.drawString(xLabel, (getWidth() / 2) - (g.getFontMetrics().stringWidth(xLabel) / 2), getHeight() - 5);

		// Now draw the data points
		int baseWidth = (getWidth() - (xOffset + 10)) / data.length;
		if (baseWidth < 1) baseWidth = 1;

		// System.out.println("Base Width is "+baseWidth);
		// Let's find the longest label, and then work out how often we can draw
		// labels
		int lastXLabelEnd = 0;

		for (int i = 0; i < data.length; i++) {
			g.setColor(Color.BLACK);
			String baseNumber = "" + xCategories[i];
			int baseNumberWidth = g.getFontMetrics().stringWidth(baseNumber);
			int baseNumberPosition = (baseWidth / 2) + xOffset + (baseWidth * i) - (baseNumberWidth / 2);

			if (baseNumberPosition > lastXLabelEnd) {
				g.drawString(baseNumber, baseNumberPosition, getHeight() - 25);
				lastXLabelEnd = baseNumberPosition + baseNumberWidth + 5;
			}
		}
		// Now draw horizontal lines across from the y axis
		g.setColor(new Color(180, 180, 180));
		for (double i = yStart; i <= maxY; i += yInterval) {
			g.drawLine(xOffset, getY(i), getWidth() - 10, getY(i));
		}
		g.setColor(Color.BLACK);


		for (int d = 0; d < data.length; d++) {
			g.setColor(Color.BLUE);
			int xValue = d + 1;


			log.debug(String.format("d = %d, lastY %d", xValue, lastY));

			int thisY = getY(data[d]);
			log.debug(String.format("i = %d, data %f = lastY %d", d, data[d], thisY));

			int x1 = xOffset + (baseWidth * d);
			int x2 = xOffset + (baseWidth * (d+1));
			int y1 = getY(0);
			int y2 = thisY;

			g.fillRect(x1, y2, (x2-x1), (y1-y2));
			g.setColor(Color.BLACK);
			g.drawRect(x1, y2, (x2-x1), (y1-y2));
		}

	}

	private int getY(double y) {
		return (getHeight() - 40) - (int) (((getHeight() - 80) / (maxY - minY)) * y);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				double[] data = new double[] { 2.0d, 4.0d, 5.0d, 1.5d };
				double minY = 0.0;
				double maxY = 5.0;
				String xLabel = "xLabel";
				String[] xCategories = new String[] { "one", "two", "three", "four"};
				String graphTitle = "graphTitle";

				JFrame frame = new JFrame();
				BarGraph barGraph = new BarGraph(data, minY, maxY, xLabel, xCategories, graphTitle);

				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(500, 500);
				frame.add(barGraph);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
