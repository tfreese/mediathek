// Created: 06 Aug. 2024
package de.freese.player.core.model;

import java.util.Objects;
import java.util.function.BiConsumer;

import javax.sound.sampled.AudioFormat;

import de.freese.player.core.util.PlayerUtils;

/**
 * @author Thomas Freese
 */
public final class Window {
    public static Window of(final AudioFormat audioFormat, final byte[] audioBytes, final long framesRead, final long framesTotal) {
        Objects.requireNonNull(audioFormat, "audioFormat required");
        Objects.requireNonNull(audioBytes, "audioBytes required");

        if (audioBytes.length % 2 != 0) {
            throw new IllegalArgumentException("audioBytes length is not a power of 2: " + audioBytes.length);
        }

        int[] samplesLeft = null;
        int[] samplesRight = null;

        if (audioFormat.getChannels() == 1) {
            // Mono
            samplesLeft = PlayerUtils.createSamplesMono(audioFormat, audioBytes);
            samplesRight = samplesLeft;
        }
        else {
            // Stereo
            final int[][] samples = PlayerUtils.createSamplesStereo(audioFormat, audioBytes);
            samplesLeft = samples[0];
            samplesRight = samples[1];
        }

        return new Window(audioFormat, samplesLeft, samplesRight, framesRead, framesTotal);
    }

    private final AudioFormat audioFormat;
    private final long framesRead;
    private final long framesTotal;
    private final int[] samplesLeft;
    private final int[] samplesRight;

    private Window(final AudioFormat audioFormat, final int[] samplesLeft, final int[] samplesRight, final long framesRead, final long framesTotal) {
        super();

        this.audioFormat = audioFormat;
        this.samplesLeft = samplesLeft;
        this.samplesRight = samplesRight;
        this.framesRead = framesRead;
        this.framesTotal = framesTotal;
    }

    public void forEach(final BiConsumer<Integer, Integer> consumer) {
        for (int i = 0; i < getSamplesLeft().length; i++) {
            consumer.accept(getSamplesLeft()[i], getSamplesRight()[i]);
        }
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public long getFramesRead() {
        return framesRead;
    }

    public long getFramesTotal() {
        return framesTotal;
    }

    public int[] getMergedSamples() {
        if (isMono()) {
            return getSamplesLeft();
        }

        final int[] samples = new int[getSamplesLeft().length];

        for (int i = 0; i < samples.length; i++) {
            final int mergedSample = (int) Math.round((getSamplesLeft()[i] + getSamplesRight()[i]) / 2D);
            samples[i] = mergedSample;
        }

        return samples;
    }

    public int[] getSamplesLeft() {
        return samplesLeft;
    }

    public int[] getSamplesRight() {
        return samplesRight;
    }

    public boolean isMono() {
        return getAudioFormat().getChannels() == 1;
    }
}
