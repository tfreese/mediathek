// Created: 03 Nov. 2024
package de.freese.player.core.signal;

import javax.sound.sampled.AudioFormat;

/**
 * @author Thomas Freese
 */
public final class DecayPulse implements Signal {
    @Override
    public byte[] generate(final AudioFormat audioFormat, final double seconds, final double frequency) {
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
            final double time = step / sampleRate;

            // The value of scale controls the rate of decay - large scale, fast decay.
            double scale = 2D * step;

            if (scale > samplesCount) {
                scale = samplesCount;
            }

            final double gain = sampleRate * (samplesCount - scale) / samplesCount;
            // final double freq = 499.0D; // Frequency

            final double sinValue =
                    (Math.sin(Math.TAU * frequency * time)
                            + Math.sin(Math.TAU * (frequency / 1.8D) * time)
                            + Math.sin(Math.TAU * (frequency / 1.5D) * time))
                            / 3.0D;

            final int sampleValue = (int) (gain * sinValue);

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