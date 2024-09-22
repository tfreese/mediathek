// Created: 07 Sept. 2024
package de.freese.player.ui.spectrum;

import javax.swing.JComponent;

import de.freese.player.fft.output.Spectrum;

/**
 * @author Thomas Freese
 */
public interface SpectrumRenderer {
    JComponent getComponent();

    void updateChartData(Spectrum spectrum);
}
