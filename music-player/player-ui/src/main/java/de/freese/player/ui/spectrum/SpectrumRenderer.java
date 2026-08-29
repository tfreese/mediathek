package de.freese.player.ui.spectrum;

import java.awt.Component;

import de.freese.player.fft.output.Spectrum;

/**
 * @author Thomas Freese
 * @since 07.09.2024
 */
public interface SpectrumRenderer {
    Component getComponent();

    void updateChartData(Spectrum spectrum);
}
