// Created: 05 Okt. 2024
package de.freese.player.ui.swing.component.spectrum;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.freese.player.fft.output.Spectrum;
import de.freese.player.ui.spectrum.BandSpectrumCollector;
import de.freese.player.ui.spectrum.SpectrumRenderer;

/**
 * @author Thomas Freese
 */
@SuppressWarnings("serial")
public final class BarSpectrumRenderer extends Component implements SpectrumRenderer {
    // @Serial
    // private static final long serialVersionUID = -1L;

    /**
     * Translate the Coordinate-Origin from the upper left to the lower left.
     */
    private static void translateCoordinates(final Graphics2D g, final int height) {
        // Swap the y-Axis to above.
        g.scale(1.0D, -1.0D);

        // Translate the 0-0 Coordinate downward.
        g.translate(0, -height);
    }

    private Map<Integer, Double> bandSpectrum = Collections.emptyMap();
    private transient BufferedImage bufferedImage;
    private transient Graphics2D bufferedImageGraphics2d;
    // private double maxAmp;

    public BarSpectrumRenderer() {
        super();

        setBackground(UIManager.getColor("Panel.background").darker());
        // setDoubleBuffered(true);
        // setLayout(null);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent event) {
                // bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                // bufferedImage = (BufferedImage) createImage(getWidth(), getHeight());
                bufferedImage = getGraphicsConfiguration().createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);

                bufferedImageGraphics2d = bufferedImage.createGraphics();
                bufferedImageGraphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                bufferedImageGraphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                translateCoordinates(bufferedImageGraphics2d, getHeight());

                paintBars(bufferedImageGraphics2d);
            }
        });
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void paint(final Graphics g) {
        if (bufferedImageGraphics2d != null) {
            g.drawImage(bufferedImage, 0, 0, this);
        }
        else {
            final Graphics2D g2d = (Graphics2D) g;

            translateCoordinates(g2d, getHeight());

            paintBars(g2d);
        }
    }

    // @Override
    // protected void paintChildren(final Graphics g) {
    //     // There are no Children.
    //     // super.paintChildren(g);
    // }
    //
    // @Override
    // protected void paintComponent(final Graphics g) {
    //     paintBars((Graphics2D) g);
    // }

    @Override
    public void updateChartData(final Spectrum spectrum) {
        if (spectrum == null) {
            bandSpectrum = Collections.emptyMap();
            // maxAmp = 0D;
        }
        else {
            // 220 -> Spectrum is 0-22kHz -> 100Hz Range per Band
            final BandSpectrumCollector bandSpectrumCollector = new BandSpectrumCollector(220);
            bandSpectrum = spectrum.stream().parallel().collect(bandSpectrumCollector);

            // Normalize
            final double maxAmp = bandSpectrumCollector.getMaxAmp();
            // maxAmp = Math.max(maxAmp, bandSpectrumCollector.getMaxAmp());

            bandSpectrum.replaceAll((key, value) -> value / maxAmp);

            // for (Map.Entry<Integer, Double> entry : bandSpectrum.entrySet()) {
            //     bandSpectrum.put(entry.getKey(), entry.getValue() / maxAmp);
            // }
        }

        if (bufferedImageGraphics2d != null) {
            paintBars(bufferedImageGraphics2d);
        }

        if (SwingUtilities.isEventDispatchThread()) {
            repaint();
        }
        else {
            SwingUtilities.invokeLater(this::repaint);
        }
    }

    private void paintBars(final Graphics2D g2d) {
        g2d.setColor(getBackground());
        g2d.fillRect(5, 0, getWidth() - 5, getHeight() - 5);

        if (bandSpectrum.isEmpty()) {
            return;
        }

        // final int barWidth = (int) Math.floor((double) getWidth() / (double) bandSpectrum.size());
        final int barWidth = (int) ((double) getWidth() / (double) bandSpectrum.size());

        g2d.setPaint(new GradientPaint(0F, 0F, Color.GREEN, 0F, getHeight() - 5F, Color.RED));

        for (Map.Entry<Integer, Double> entry : bandSpectrum.entrySet()) {
            // final int barHeight = (int) Math.floor(entry.getValue() * getHeight());
            final int barHeight = (int) (entry.getValue() * (getHeight() - 5));

            g2d.fillRect(5 + ((barWidth + 1) * entry.getKey()), 0, barWidth - 1, barHeight);
        }
    }
}
