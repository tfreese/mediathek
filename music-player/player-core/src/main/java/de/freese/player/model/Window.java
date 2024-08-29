// Created: 06 Aug. 2024
package de.freese.player.model;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;

import javax.sound.sampled.AudioFormat;

import de.freese.player.util.PlayerUtils;

/**
 * @author Thomas Freese
 */
public final class Window {
    private final byte[] audioBytes;
    private final AudioFormat audioFormat;
    private final int[] samplesLeft;
    private final int[] samplesRight;

    public Window(final AudioFormat audioFormat, final byte[] audioBytes) {
        super();

        Objects.requireNonNull(audioBytes, "audioBytes required");

        if (audioBytes.length % 2 != 0) {
            throw new IllegalArgumentException("audioBytes length is not a power of 2: " + audioBytes.length);
        }

        this.audioFormat = Objects.requireNonNull(audioFormat, "audioFormat required");
        this.audioBytes = audioBytes;

        if (audioFormat.getChannels() == 1) {
            // Mono
            this.samplesLeft = PlayerUtils.createSamplesMono(audioFormat, audioBytes);
            this.samplesRight = null;
        }
        else {
            // Stereo
            final int[][] samples = PlayerUtils.createSamplesStereo(audioFormat, audioBytes);
            this.samplesLeft = samples[0];
            this.samplesRight = samples[1];
        }
    }

    // public Window(final int[] samples) {
    //     super();
    //
    //     this.audioBytes = null;
    //     this.samplesLeft = Objects.requireNonNull(samples, "samples required");
    //     this.samplesRight = null;
    // }
    //
    // public Window(final int[] samplesLeft, final int[] samplesRight) {
    //     super();
    //
    //     this.audioBytes = null;
    //     this.samplesLeft = Objects.requireNonNull(samplesRight, "samplesLeft required");
    //     this.samplesRight = Objects.requireNonNull(samplesRight, "samplesRight required");
    //
    //     if (samplesLeft.length != samplesRight.length) {
    //         throw new IllegalArgumentException("stereo samples must have the same length: " + samplesLeft.length + " != " + samplesRight.length);
    //     }
    // }

    public void forEachMono(final IntConsumer consumer) {
        for (int sample : getSamplesLeft()) {
            consumer.accept(sample);
        }
    }

    public void forEachStereo(final BiConsumer<Integer, Integer> consumer) {
        for (int i = 0; i < getSamplesRight().length; i++) {
            consumer.accept(getSamplesLeft()[i], getSamplesRight()[i]);
        }
    }

    public byte[] getAudioBytes() {
        return audioBytes;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
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
        return this.samplesRight == null;
    }
}
