// Created: 03 Nov. 2024
package de.freese.player.core.signal;

import java.util.Random;

import javax.sound.sampled.AudioFormat;

/**
 * @author Thomas Freese
 */
public final class WhiteNoise implements Signal {
    private final Random random = new Random();

    @Override
    public byte[] generate(final AudioFormat audioFormat, final double seconds) {
        final int channels = audioFormat.getChannels();

        final int frameSize = audioFormat.getFrameSize(); // Bytes per Frame
        // float frameRate = audioFormat.getFrameRate();

        // int sampleSizeInBits = audioFormat.getSampleSizeInBits();
        final double sampleRate = audioFormat.getSampleRate();

        final int samplesCount = (int) (sampleRate * seconds);
        final int arrayLength = samplesCount * frameSize;

        final byte[] audioBytes = new byte[arrayLength];
        int bufferIndex = 0;

        for (int step = 0; step < samplesCount; step++) {
            final int sampleValue = random.nextInt(-(int) sampleRate + 1, (int) sampleRate);

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
}
