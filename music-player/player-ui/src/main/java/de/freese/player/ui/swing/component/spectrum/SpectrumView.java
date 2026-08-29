package de.freese.player.ui.swing.component.spectrum;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import de.freese.player.fft.output.Spectrum;
import de.freese.player.ui.spectrum.SpectrumRenderer;

/**
 * @author Thomas Freese
 * @since 29.08.2024
 */
public final class SpectrumView {
    // @Serial
    // private static final long serialVersionUID = -1L;

    private final JPanel panel = new JPanel(new BorderLayout());
    private final SpectrumRenderer spectrumRenderer;

    public SpectrumView() {
        super();

        // spectrumRenderer = new JFreeChartRenderer();
        spectrumRenderer = new BarSpectrumRenderer();
        panel.add(spectrumRenderer.getComponent(), BorderLayout.CENTER);
    }

    public JComponent getComponent() {
        return panel;
    }

    public void updateChartData(final Spectrum spectrum) {
        spectrumRenderer.updateChartData(spectrum);
    }
}
