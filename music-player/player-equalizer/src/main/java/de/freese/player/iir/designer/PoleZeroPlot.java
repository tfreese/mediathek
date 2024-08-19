package de.freese.player.iir.designer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.Serial;

public class PoleZeroPlot extends Canvas {
    @Serial
    private static final long serialVersionUID = 1L;

    private Color axisColor = Color.darkGray;
    private Color bgColor = Color.lightGray;
    private Color circColor = Color.red;
    private Color gridColor = Color.darkGray;
    private int order = 0;
    private float[] pImag;
    private float[] pReal;
    private Color plotColor = Color.blue;
    private float[] z;

    public Color getAxisColor() {
        return axisColor;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public Color getCircColor() {
        return circColor;
    }

    public Color getGridColor() {
        return gridColor;
    }

    public Color getPlotColor() {
        return plotColor;
    }

    public void paint(final Graphics g) {
        final int horzSpace = 20;
        final int vertSpace = 20;
        final int gridIntervals = 10;

        int x;
        int y;
        final int xc = getSize().width / 2;
        final int yc = getSize().height / 2;
        final int width = getSize().width - 2 * horzSpace;
        final int height = getSize().height - 2 * vertSpace;
        final int radius = Math.min(width / 2, height / 2);
        final int top = yc - radius;
        final int bottom = yc + radius;
        final int left = xc - radius;
        final int right = xc + radius;

        final float scale = 2F * radius / (float) gridIntervals;
        setBackground(bgColor);
        g.setColor(gridColor);

        // grid lines
        for (int i = 0; i <= gridIntervals; i++) {
            x = left + Math.round(i * scale);
            y = top + Math.round(i * scale);
            g.drawLine(x, top, x, bottom); // vertical grid line
            g.drawLine(left, y, right, y); // horizontal grid line
        }

        g.setColor(axisColor);
        g.drawLine(xc, top - vertSpace, xc, bottom + vertSpace); // vertical axis
        g.setFont(new Font("Sans serif", Font.BOLD, 10));
        final FontMetrics fm = g.getFontMetrics();
        final int h = fm.getMaxAscent();
        g.drawString("Im", xc + 4, top - vertSpace + h);
        g.drawLine(left - horzSpace, yc, right + horzSpace, yc); // horizontal axis
        final int w = fm.stringWidth("Re");
        g.drawString("Re", right + horzSpace - w, yc + h + 4);
        g.setColor(circColor);
        g.drawOval(left, top, 2 * radius, 2 * radius); // unit circle

        if (order > 0) {
            g.setColor(plotColor);

            // plot zeros
            final int zSize = 4;

            for (int i = 1; i <= order; i++) {
                x = xc + Math.round(radius * z[i]);
                // zero symbol size
                g.drawOval(x - zSize, yc - zSize, 2 * zSize, 2 * zSize);
            }

            // plot poles
            final int pSize = 3;

            for (int i = 1; i <= order; i++) {
                x = xc + Math.round(radius * pReal[i]);
                y = yc - Math.round(radius * pImag[i]);

                // pole symbol size
                g.drawLine(x - pSize, y - pSize, x + pSize, y + pSize);
                g.drawLine(x - pSize, y + pSize, x + pSize, y - pSize);
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

    public void setCircColor(final Color c) {
        if (c != null) {
            circColor = c;
        }
    }

    public void setGridColor(final Color c) {
        if (c != null) {
            gridColor = c;
        }
    }

    public void setPlotColor(final Color c) {
        if (c != null) {
            plotColor = c;
        }
    }

    public void setPolesAndZeros(final float[] pr, final float[] pi, final float[] zr) {
        order = pr.length - 1; // number of poles/zeros = filter order
        pReal = new float[order + 1];
        pImag = new float[order + 1];
        z = new float[order + 1];

        for (int i = 1; i <= order; i++) {
            pReal[i] = pr[i];
            pImag[i] = pi[i];
            z[i] = zr[i];
        }

        repaint();
    }
}
