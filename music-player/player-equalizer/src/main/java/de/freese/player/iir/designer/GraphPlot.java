package de.freese.player.iir.designer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serial;

public class GraphPlot extends Canvas {
    public static final int SIGNAL = 1;
    public static final int SPECTRUM = 2;

    @Serial
    private static final long serialVersionUID = -2284879212465893870L;

    private Color axisColor = Color.BLACK;
    private Color bgColor = Color.BLUE;
    private Color gridColor = Color.BLACK;
    private int horzIntervals = 10;
    private int horzSpace = 20;
    private boolean logScale = false;
    private int nPoints = 0;
    private Color plotColor = Color.yellow;
    private int plotStyle = SIGNAL;
    private float[] plotValues;
    private boolean tracePlot = true;
    private int vertIntervals = 8;
    private int vertSpace = 20;
    private float ymax = 0.0F;

    public Color getAxisColor() {
        return axisColor;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public Color getGridColor() {
        return gridColor;
    }

    public int getHorzIntervals() {
        return horzIntervals;
    }

    public int getHorzSpace() {
        return horzSpace;
    }

    public Color getPlotColor() {
        return plotColor;
    }

    public int getPlotStyle() {
        return plotStyle;
    }

    public int getVertIntervals() {
        return vertIntervals;
    }

    public int getVertSpace() {
        return vertSpace;
    }

    public float getYmax() {
        return ymax;
    }

    public boolean isLogScale() {
        return logScale;
    }

    public boolean isTracePlot() {
        return tracePlot;
    }

    public void paint(final Graphics g) {
        int x;
        int y;
        final int top = vertSpace;
        final int bottom = getSize().height - vertSpace;
        final int left = horzSpace;
        final int right = getSize().width - horzSpace;
        final int width = right - left;
        final int fullHeight = bottom - top;
        final int centre = (top + bottom) / 2;
        int xAxisPos = centre;
        int yHeight = fullHeight / 2;

        if (plotStyle == SPECTRUM) {
            xAxisPos = bottom;
            yHeight = fullHeight;
        }

        this.setBackground(bgColor);

        if (logScale) {
            xAxisPos = top;
            g.setColor(gridColor);

            // vertical grid lines
            for (int i = 0; i <= vertIntervals; i++) {
                x = left + i * width / vertIntervals;
                g.drawLine(x, top, x, bottom);
            }

            // horizontal grid lines
            for (int i = 0; i <= horzIntervals; i++) {
                y = top + i * fullHeight / horzIntervals;
                g.drawLine(left, y, right, y);
            }
        }

        g.setColor(axisColor);
        g.drawLine(left, top, left, bottom);        // vertical axis
        g.drawLine(left, xAxisPos, right, xAxisPos);  // horizontal axis

        if (nPoints != 0) {
            g.setColor(plotColor);
            // horizontal scale factor:
            final float xScale = width / (float) (nPoints - 1);
            // vertical scale factor:
            final float yScale = yHeight / ymax;
            final int[] xCoords = new int[nPoints];
            final int[] yCoords = new int[nPoints];

            for (int i = 0; i < nPoints; i++) {
                xCoords[i] = left + Math.round(i * xScale);
                yCoords[i] = xAxisPos - Math.round(plotValues[i] * yScale);
            }

            if (tracePlot) {
                g.drawPolyline(xCoords, yCoords, nPoints);
            }
            else { // bar plot
                for (int i = 0; i < nPoints; i++) {
                    g.drawLine(xCoords[i], xAxisPos, xCoords[i], yCoords[i]);
                }
            }
        }
    }

    public void setAxisColor(final Color c) {
        if (c != null) {
            axisColor = c;
        }
    }

    public void setBgColor(final Color c) {
        if (c != null) {
            bgColor = c;
        }
    }

    public void setGridColor(final Color c) {
        if (c != null) {
            gridColor = c;
        }
    }

    public void setHorzIntervals(final int i) {
        horzIntervals = i;
    }

    public void setHorzSpace(final int h) {
        horzSpace = h;
    }

    public void setLogScale(final boolean b) {
        logScale = b;
    }

    public void setPlotColor(final Color c) {
        if (c != null) {
            plotColor = c;
        }
    }

    public void setPlotStyle(final int pst) {
        plotStyle = pst;
    }

    public void setPlotValues(final float[] values) {
        nPoints = values.length;
        plotValues = new float[nPoints];
        plotValues = values;
        repaint();
    }

    public void setTracePlot(final boolean b) {
        tracePlot = b;
    }

    public void setVertIntervals(final int i) {
        vertIntervals = i;
    }

    public void setVertSpace(final int v) {
        vertSpace = v;
    }

    public void setYmax(final float m) {
        ymax = m;
    }
}
