// Created: 07 Aug. 2024
package de.freese.player.util;

import javax.sound.sampled.AudioFormat;

/**
 * @author Thomas Freese
 */
public final class SoundGenerator {
    /**
     * Duration: 1 Second
     */
    public static byte[] createSound(final AudioFormat audioFormat, final double frequency) {
        return createSound(audioFormat, 1, new double[]{frequency});
    }

    public static byte[] createSound(final AudioFormat audioFormat, final int seconds, final double[] frequencies) {
        final int frameSize = audioFormat.getFrameSize();
        final double sampleRate = audioFormat.getSampleRate();
        final int channels = audioFormat.getChannels();

        final int bytesPerSample = frameSize * channels;
        final int byteCount = (int) sampleRate * bytesPerSample * seconds;
        final byte[] audioBytes = new byte[byteCount];
        final int sampleLength = byteCount / bytesPerSample;

        int bufferIndex = 0;

        for (int cnt = 0; cnt < sampleLength; cnt++) {
            final double time = cnt / sampleRate;

            double sinValue = 0.0D;

            for (int f = 0; f < frequencies.length; f++) {
                // Math.sin(2D * Math.PI * frequency * time);
                sinValue += Math.sin(Math.TAU * frequencies[f] * time);
            }

            final int sampleValue = (int) (sampleRate * sinValue);

            if (audioFormat.isBigEndian()) {
                for (int c = 0; c < channels; c++) {
                    audioBytes[bufferIndex++] = (byte) (sampleValue >> 8);
                    audioBytes[bufferIndex++] = (byte) sampleValue;
                }
            }
            else {
                for (int c = 0; c < channels; c++) {
                    audioBytes[bufferIndex++] = (byte) sampleValue;
                    audioBytes[bufferIndex++] = (byte) (sampleValue >> 8);
                }
            }
        }

        return audioBytes;
    }

    private SoundGenerator() {
        super();
    }
}
