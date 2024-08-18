// Created: 04 Aug. 2024
package de.freese.player.player;

import java.util.List;
import java.util.Objects;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import de.freese.player.dsp.DspProcessor;
import de.freese.player.model.Window;

/**
 * @author Thomas Freese
 */
public final class SourceDataLinePlayer implements DspProcessor {
    /**
     * short: -32768 to 32767
     */
    public static final int MAX_16_BIT = 32_768;

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
        sourceDataLine.drain();
        sourceDataLine.stop();
        sourceDataLine.close();
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Writes the mono samples (between –1.0 and +1.0) to standard audio.
     * If a sample is outside the range, it will be clipped.
     */
    public void play(final double[] samples) {
        Objects.requireNonNull(samples, "samples required");

        for (double sample : samples) {
            play(sample);
        }
    }

    /**
     * Writes the mono samples (between –1.0 and +1.0) to standard audio.
     * If a sample is outside the range, it will be clipped.
     */
    public void play(final Iterable<Double> samples) {
        Objects.requireNonNull(samples, "samples required");

        for (double sample : samples) {
            play(sample);
        }
    }

    /**
     * Writes the stereo samples (between –1.0 and +1.0) to standard audio.
     * If a sample is outside the range, it will be clipped.
     */
    public void play(final List<Double> samplesLeft, final List<Double> samplesRight) {
        Objects.requireNonNull(samplesLeft, "samplesLeft required");
        Objects.requireNonNull(samplesRight, "samplesRight required");

        if (samplesLeft.size() != samplesRight.size()) {
            throw new IllegalArgumentException("stereo samples must have the same length: " + samplesLeft.size() + " != " + samplesRight.size());
        }

        for (int i = 0; i < samplesLeft.size(); i++) {
            play(samplesLeft.get(i), samplesRight.get(i));
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

    @Override
    public void process(final Window window) {
        play(window);
    }

    public void stop() {
        sourceDataLine.drain();
        sourceDataLine.stop();
    }

    /**
     * Writes one stereo sample (between –1.0 and +1.0) to standard audio.
     * If a sample is outside the range, it will be clipped (rounded to –1.0 or +1.0).
     */
    private void play(final double sampleLeft, final double sampleRight) {
        if (Double.isNaN(sampleLeft)) {
            throw new IllegalArgumentException("sampleLeft is NaN");
        }

        if (Double.isNaN(sampleRight)) {
            throw new IllegalArgumentException("sampleRight is NaN");
        }

        double left = sampleLeft;
        double right = sampleRight;

        // clip if outside [-1, +1]
        if (left < -1.0D) {
            left = -1.0D;
        }
        if (left > +1.0D) {
            left = +1.0D;
        }
        if (right < -1.0D) {
            right = -1.0D;
        }
        if (right > +1.0D) {
            right = +1.0D;
        }

        short sLeft = (short) (MAX_16_BIT * left);
        if (Double.compare(left, 1.0D) == 0) {
            sLeft = Short.MAX_VALUE;   // special case since 32768 not a short
        }

        short sRight = (short) (MAX_16_BIT * right);
        if (Double.compare(right, 1.0D) == 0) {
            sRight = Short.MAX_VALUE;   // special case since 32768 not a short
        }

        final byte[] buffer = new byte[4];

        if (audioFormat.isBigEndian()) {
            buffer[0] = (byte) (sLeft >> 8);
            buffer[1] = (byte) sLeft;
            buffer[2] = (byte) (sRight >> 8);
            buffer[3] = (byte) sRight;
        }
        else {
            buffer[0] = (byte) sLeft;
            buffer[1] = (byte) (sLeft >> 8);
            buffer[2] = (byte) sRight;
            buffer[3] = (byte) (sRight >> 8);
        }

        play(buffer, buffer.length);
    }

    /**
     * Writes one mono sample (between –1.0 and +1.0) to standard audio.
     * If the sample is outside the range, it will be clipped (rounded to –1.0 or +1.0).
     */
    private void play(final double sample) {
        if (Double.isNaN(sample)) {
            throw new IllegalArgumentException("sample is NaN");
        }

        double mono = sample;

        // clip if outside [-1, +1]
        if (mono < -1.0D) {
            mono = -1.0D;
        }
        if (mono > +1.0D) {
            mono = +1.0D;
        }

        short s = (short) (MAX_16_BIT * mono);
        if (Double.compare(mono, 1.0D) == 0) {
            s = Short.MAX_VALUE;   // special case since 32768 not a short
        }

        final byte[] buffer = new byte[2];

        if (audioFormat.isBigEndian()) {
            buffer[0] = (byte) (s >> 8);
            buffer[1] = (byte) s;
        }
        else {
            buffer[0] = (byte) s;
            buffer[1] = (byte) (s >> 8);
        }

        play(buffer, buffer.length);
    }

    /**
     * Writes one mono sample (between –SAMPLE_RATE and +SAMPLE_RATE) to standard audio.
     * If the sample is outside the range, it will be clipped (rounded to –SAMPLE_RATE or +SAMPLE_RATE).
     */
    private void play(final int sample) {
        final float sampleRate = audioFormat.getSampleRate();
        int mono = sample;

        // clip if outside [-SAMPLE_RATE, +SAMPLE_RATE]
        if (mono < -sampleRate) {
            mono = -(int) sampleRate;
        }

        if (mono > +sampleRate) {
            mono = +(int) sampleRate;
        }

        final byte[] buffer = new byte[2];

        if (audioFormat.isBigEndian()) {
            buffer[0] = (byte) (mono >> 8);
            buffer[1] = (byte) mono;
        }
        else {
            buffer[0] = (byte) mono;
            buffer[1] = (byte) (mono >> 8);
        }

        play(buffer, buffer.length);
    }

    /**
     * Writes one stereo sample (between –SAMPLE_RATE and +SAMPLE_RATE) to standard audio.
     * If a sample is outside the range, it will be clipped (rounded to –SAMPLE_RATE or +SAMPLE_RATE).
     */
    private void play(final int sampleLeft, final int sampleRight) {
        final float sampleRate = audioFormat.getSampleRate();
        int left = sampleLeft;
        int right = sampleRight;

        // clip if outside [-SAMPLE_RATE, +SAMPLE_RATE]
        if (left < -sampleRate) {
            left = -(int) sampleRate;
        }
        if (left > +sampleRate) {
            left = +(int) sampleRate;
        }

        if (right < -sampleRate) {
            right = -(int) sampleRate;
        }
        if (right > +sampleRate) {
            right = +(int) sampleRate;
        }

        final byte[] buffer = new byte[4];

        if (audioFormat.isBigEndian()) {
            buffer[0] = (byte) (left >> 8);
            buffer[1] = (byte) left;
            buffer[2] = (byte) (right >> 8);
            buffer[3] = (byte) right;
        }
        else {
            buffer[0] = (byte) left;
            buffer[1] = (byte) (left >> 8);
            buffer[2] = (byte) right;
            buffer[3] = (byte) (right >> 8);
        }

        play(buffer, buffer.length);
    }
}
