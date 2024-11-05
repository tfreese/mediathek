// Created: 05 Nov. 2024
package de.freese.player.equalizer;

/**
 * @author Thomas Freese
 */
public interface Equalizer {
    void cleanHistory();

    void equalize(final int[] samplesLeft, final int[] samplesRight);

    EqualizerControls getControls();
}
