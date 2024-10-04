// Created: 04 Aug. 2024
package de.freese.player.core.player;

import java.util.Objects;
import java.util.function.Consumer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import de.freese.player.core.model.Window;
import de.freese.player.core.util.PlayerUtils;

/**
 * @author Thomas Freese
 */
public final class SourceDataLinePlayer {
    private final AudioFormat audioFormat;
    private final SourceDataLine sourceDataLine;

    public SourceDataLinePlayer(final AudioFormat audioFormat) throws LineUnavailableException {
        super();

        this.audioFormat = Objects.requireNonNull(audioFormat, "audioFormat required");
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();
    }

    public void close() {
        // Continues data line I/O until its buffer is drained.
        // sourceDataLine.drain();

        sourceDataLine.stop();
        sourceDataLine.close();
    }

    public void configureVolumeControl(final Consumer<FloatControl> consumer) {
        if (sourceDataLine.isOpen() && sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            consumer.accept((FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN));
        }
    }

    /**
     * Writes the mono samples (between –1.0 and +1.0) to standard audio.
     * If a sample is outside the range, it will be clipped.
     */
    public void play(final double[] samplesMono) {
        Objects.requireNonNull(samplesMono, "samples required");

        for (double sampleMono : samplesMono) {
            play(sampleMono);
        }
    }

    /**
     * Writes the windowed samples (between –SAMPLE_RATE and +SAMPLE_RATE) to standard audio.
     * If a sample is outside the range, it will be clipped.
     */
    public void play(final Window window) {
        Objects.requireNonNull(window, "window required");

        if (window.isMono()) {
            window.forEachMono(this::play);
        }
        else {
            window.forEachStereo(this::play);
        }
    }

    public void play(final byte[] audioData, final int length) {
        sourceDataLine.write(audioData, 0, length);
    }

    /**
     * Writes one mono sample (between –SAMPLE_RATE and +SAMPLE_RATE) to standard audio.
     * If the sample is outside the range, it will be clipped (rounded to –SAMPLE_RATE or +SAMPLE_RATE).
     */
    public void play(final int sampleMono) {
        final byte[] buffer = PlayerUtils.sampleToByte(audioFormat, sampleMono);

        play(buffer, buffer.length);
    }

    /**
     * Writes one mono sample (between –1.0 and +1.0) to standard audio.
     * If the sample is outside the range, it will be clipped (rounded to –1.0 or +1.0).
     */
    public void play(final double sampleMono) {
        final byte[] buffer = PlayerUtils.sampleToByte(audioFormat, sampleMono);

        play(buffer, buffer.length);
    }

    /**
     * Writes one stereo sample (between –SAMPLE_RATE and +SAMPLE_RATE) to standard audio.
     * If a sample is outside the range, it will be clipped (rounded to –SAMPLE_RATE or +SAMPLE_RATE).
     */
    public void play(final int sampleLeft, final int sampleRight) {
        final byte[] buffer = PlayerUtils.sampleToByte(audioFormat, sampleLeft, sampleRight);

        play(buffer, buffer.length);
    }

    /**
     * Writes one stereo sample (between –1.0 and +1.0) to standard audio.
     * If a sample is outside the range, it will be clipped (rounded to –1.0 or +1.0).
     */
    public void play(final double sampleLeft, final double sampleRight) {
        final byte[] buffer = PlayerUtils.sampleToByte(audioFormat, sampleLeft, sampleRight);

        play(buffer, buffer.length);
    }

    public void stop() {
        // Continues data line I/O until its buffer is drained.
        // sourceDataLine.drain();

        sourceDataLine.stop();
    }
}
