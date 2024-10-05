// Created: 05 Okt. 2024
package de.freese.player.ui.swing.component.spectrum;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

    private Map<Integer, Double> bandSpectrum;

    public BarSpectrumRenderer() {
        super();

        setBackground(UIManager.getColor("Panel.background").darker());
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void paint(final Graphics g) {
        g.setColor(getBackground());
        g.fillRect(5, 5, getWidth() - 5, getHeight() - 5);

        if (bandSpectrum == null) {
            return;
        }

        // final int barWidth = (int) Math.floor((double) getWidth() / (double) bandSpectrum.size());
        final int barWidth = (int) ((double) getWidth() / (double) bandSpectrum.size());

        ((Graphics2D) g).setPaint(new GradientPaint(0F, 0F, Color.RED, 0F, getHeight() - 5F, Color.GREEN));

        for (Map.Entry<Integer, Double> entry : bandSpectrum.entrySet()) {
            // final int barHeight = (int) Math.floor(entry.getValue() * getHeight());
            final int barHeight = (int) (entry.getValue() * getHeight());

            g.fillRect(5 + ((barWidth + 1) * entry.getKey()), getHeight() - barHeight, barWidth - 1, barHeight);
        }
    }

    @Override
    public void updateChartData(final Spectrum spectrum) {
        if (spectrum == null) {
            bandSpectrum = null;
        }
        else {
            // 220 -> Spectrum is 0-22kHz -> 100Hz Range per Band
            bandSpectrum = spectrum.stream().parallel().collect(new BandSpectrumCollector(220));
        }

        SwingUtilities.invokeLater(this::repaint);
    }
}
