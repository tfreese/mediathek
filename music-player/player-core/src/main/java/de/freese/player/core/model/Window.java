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
    private final byte[] audioBytes;
    private final AudioFormat audioFormat;
    private final long framesRead;
    private final long framesTotal;
    private final int[] samplesLeft;
    private final int[] samplesRight;

    public Window(final AudioFormat audioFormat, final byte[] audioBytes, final long framesRead, final long framesTotal) {
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
            this.samplesRight = this.samplesLeft;
        }
        else {
            // Stereo
            final int[][] samples = PlayerUtils.createSamplesStereo(audioFormat, audioBytes);
            this.samplesLeft = samples[0];
            this.samplesRight = samples[1];
        }

        this.framesRead = framesRead;
        this.framesTotal = framesTotal;
    }

    public void forEach(final BiConsumer<Integer, Integer> consumer) {
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

    public int getSamplesLength() {
        return samplesLeft.length;
    }

    public int[] getSamplesRight() {
        return samplesRight;
    }

    public boolean isMono() {
        return samplesRight == null;
    }
}
