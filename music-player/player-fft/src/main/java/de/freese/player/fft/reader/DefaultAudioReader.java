// Created: 11 Aug. 2024
package de.freese.player.fft.reader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.NoSuchElementException;
import java.util.Objects;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * @author Thomas Freese
 */
public final class DefaultAudioReader implements AudioReader {
    private final AudioInputStream audioInputStream;
    
    private int[] lastSamples;

    public DefaultAudioReader(final AudioInputStream audioInputStream) {
        super();

        this.audioInputStream = Objects.requireNonNull(audioInputStream, "audioInputStream required");
    }

    @Override
    public AudioFormat getAudioFormat() {
        return getAudioInputStream().getFormat();
    }

    @Override
    public AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }

    @Override
    public long getDurationMs() {
        return 0;
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public int[] getWaveform() {
        try {
            final byte[] audioBytes = getAudioInputStream().readAllBytes();
            return convertBytesToSamples(audioBytes);
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public boolean hasNext() {
        try {
            return getAudioInputStream().available() > 0;
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public int[] next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // final int windowSize = fftConfig.getWindowSize() * (isStereo() ? 2 : 1);
        final int windowSize = 8192 * (isStereo() ? 2 : 1);
        // final double windowOverlap = fftConfig.getWindowOverlap();
        final double windowOverlap = 0.75D;
        final byte[] audioBytes = new byte[windowSize * 2]; // 16-bit audio = 2 bytes per sample

        try {
            if (lastSamples == null) {
                getAudioInputStream().read(audioBytes);

                lastSamples = convertBytesToSamples(audioBytes);

                return lastSamples;
            }
            else {
                final int samplesToKeep = (int) Math.round(windowSize * windowOverlap);
                final int prevSamplesCopyStartIndex = windowSize - samplesToKeep;
                final int numMoreBytesToRead = (windowSize - samplesToKeep) * 2;

                final int[] newSampleBuffer = new int[windowSize];
                System.arraycopy(lastSamples, prevSamplesCopyStartIndex, newSampleBuffer, 0, samplesToKeep);

                final byte[] newBytes = new byte[numMoreBytesToRead];
                getAudioInputStream().read(newBytes);
                final int[] newSamples = convertBytesToSamples(newBytes);
                System.arraycopy(newSamples, 0, newSampleBuffer, samplesToKeep, newSamples.length);

                lastSamples = newSampleBuffer;

                return newSampleBuffer;
            }
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * Converts a byte array consisting of 16-bit audio into a list of samples half as long
     * (each sample represented by 2 bytes).
     */
    private int[] convertBytesToSamples(final byte[] audioBytes) {
        final int[] samples = new int[audioBytes.length / 2];
        final boolean isBigEndian = getAudioFormat().isBigEndian();

        for (int i = 0; i < samples.length; i++) {
            final int sample;

            if (isBigEndian) {
                sample = audioBytes[2 * i + 1] & 0xFF | audioBytes[2 * i] << 8;
            }
            else {
                sample = audioBytes[2 * i] & 0xFF | audioBytes[2 * i + 1] << 8;
            }

            samples[i] = sample;
        }

        return samples;
    }
}
