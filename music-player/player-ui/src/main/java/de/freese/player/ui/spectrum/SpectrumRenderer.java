// Created: 07 Sept. 2024
package de.freese.player.ui.spectrum;

import java.awt.Component;

import de.freese.player.fft.output.Spectrum;

/**
 * @author Thomas Freese
 */
public interface SpectrumRenderer {
    Component getComponent();

    void updateChartData(Spectrum spectrum);
}
