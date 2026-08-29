package de.freese.player.equalizer;

/**
 * @author Thomas Freese
 * @since 05.11.2024
 */
public interface Equalizer {
    void cleanHistory();

    void equalize(final int[] samplesLeft, final int[] samplesRight);

    EqualizerControls getControls();
}
