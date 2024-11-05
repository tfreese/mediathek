// Created: 03 Nov. 2024
package de.freese.player.core.signal;

import javax.sound.sampled.AudioFormat;

/**
 * @author Thomas Freese
 */
public final class StereoPanning implements Signal {
    @Override
    public byte[] generate(final AudioFormat audioFormat, final double seconds, final double frequency) {
        // final int channels = audioFormat.getChannels();

        final int frameSize = audioFormat.getFrameSize(); // Bytes per Frame
        // float frameRate = audioFormat.getFrameRate();

        // int sampleSizeInBits = audioFormat.getSampleSizeInBits();
        final double sampleRate = audioFormat.getSampleRate();

        final int samplesCount = (int) (sampleRate * seconds);
        final int arrayLength = samplesCount * frameSize;

        final byte[] audioBytes = new byte[arrayLength];
        int bufferIndex = 0;

        for (int step = 0; step < samplesCount; step++) {
            // Calculate time-varying gain for each speaker.
            final double rightGain = sampleRate * step / samplesCount;
            final double leftGain = sampleRate - rightGain;

            final double time = step / sampleRate;

            // Generate data for left speaker.
            final double sinValueLeft = leftGain * Math.sin(Math.TAU * frequency * time);

            // Generate data for right speaker.
            final double sinValueRight = rightGain * Math.sin(Math.TAU * (frequency * 0.8D) * time);

            // final int sampleValueLeft = (int) (sampleRate * sinValueLeft);
            final int sampleValueLeft = (int) sinValueLeft;
            // final int sampleValueRight = (int) (sampleRate * sinValueRight);
            final int sampleValueRight = (int) sinValueRight;

            if (audioFormat.isBigEndian()) {
                audioBytes[bufferIndex++] = (byte) (sampleValueLeft >> 8);
                audioBytes[bufferIndex++] = (byte) sampleValueLeft;

                audioBytes[bufferIndex++] = (byte) (sampleValueRight >> 8);
                audioBytes[bufferIndex++] = (byte) sampleValueRight;
            }
            else {
                audioBytes[bufferIndex++] = (byte) sampleValueLeft;
                audioBytes[bufferIndex++] = (byte) (sampleValueLeft >> 8);

                audioBytes[bufferIndex++] = (byte) sampleValueRight;
                audioBytes[bufferIndex++] = (byte) (sampleValueRight >> 8);
            }
        }

        return audioBytes;
    }
}
