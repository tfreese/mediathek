// Created: 29 Jan. 2025
package de.freese.player.core.player;

import java.util.Objects;
import java.util.function.Consumer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.FloatControl;

import de.freese.player.core.model.Window;
import de.freese.player.core.util.PlayerUtils;

/**
 * @author Thomas Freese
 */
public interface AudioPlayerSink {

    void close();

    void configureVolumeControl(Consumer<FloatControl> consumer);

    AudioFormat getAudioFormat();

    /**
     * Writes the windowed samples (between –SAMPLE_RATE and +SAMPLE_RATE) to standard audio.
     * If a sample is outside the range, it will be clipped.
     */
    default void play(final Window window) {
        Objects.requireNonNull(window, "window required");

        window.forEach(this::play);
    }

    /**
     * Writes one mono sample (between –1.0 and +1.0) to standard audio.
     * If the sample is outside the range, it will be clipped (rounded to –1.0 or +1.0).
     */
    default void play(final double sampleMono) {
        final byte[] buffer = PlayerUtils.sampleToByte(getAudioFormat(), sampleMono);

        play(buffer, buffer.length);
    }

    /**
     * Writes one stereo sample (between –1.0 and +1.0) to standard audio.
     * If a sample is outside the range, it will be clipped (rounded to –1.0 or +1.0).
     */
    default void play(final double sampleLeft, final double sampleRight) {
        final byte[] buffer = PlayerUtils.sampleToByte(getAudioFormat(), sampleLeft, sampleRight);

        play(buffer, buffer.length);
    }

    /**
     * Writes the mono samples (between –1.0 and +1.0) to standard audio.
     * If a sample is outside the range, it will be clipped.
     */
    default void play(final double[] samplesMono) {
        Objects.requireNonNull(samplesMono, "samples required");

        for (double sampleMono : samplesMono) {
            play(sampleMono);
        }
    }

    /**
     * Writes one mono sample (between –SAMPLE_RATE and +SAMPLE_RATE) to standard audio.
     * If the sample is outside the range, it will be clipped (rounded to –SAMPLE_RATE or +SAMPLE_RATE).
     */
    default void play(final int sampleMono) {
        final byte[] buffer = PlayerUtils.sampleToByte(getAudioFormat(), sampleMono);

        play(buffer, buffer.length);
    }

    /**
     * Writes one stereo sample (between –SAMPLE_RATE and +SAMPLE_RATE) to standard audio.
     * If a sample is outside the range, it will be clipped (rounded to –SAMPLE_RATE or +SAMPLE_RATE).
     */
    default void play(final int sampleLeft, final int sampleRight) {
        final byte[] buffer = PlayerUtils.sampleToByte(getAudioFormat(), sampleLeft, sampleRight);

        play(buffer, buffer.length);
    }

    void play(final byte[] audioData, final int length);

    void stop();
}
