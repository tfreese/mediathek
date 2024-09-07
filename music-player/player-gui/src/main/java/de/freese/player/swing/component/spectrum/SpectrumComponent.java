// Created: 29 Aug. 2024
package de.freese.player.swing.component.spectrum;

import javax.swing.JComponent;

import de.freese.player.fft.output.Spectrum;

/**
 * @author Thomas Freese
 */
public final class SpectrumComponent {
    // @Serial
    // private static final long serialVersionUID = -1L;

    private final SpectrumRenderer spectrumRenderer;

    public SpectrumComponent() {
        super();

        spectrumRenderer = new JFreeChartRenderer();
    }

    public JComponent getComponent() {
        return spectrumRenderer.getComponent();
    }

    public void updateChartData(final Spectrum spectrum) {
        spectrumRenderer.updateChartData(spectrum);
    }
}
