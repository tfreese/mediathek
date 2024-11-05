// Created: 03 Nov. 2024
package de.freese.player.core.signal;

import javax.sound.sampled.AudioFormat;

/**
 * @author Thomas Freese
 */
public final class EchoPulse implements Signal {
    private static double playEchoPulseHelper(final int cnt, final int sampleLength, final double sampleRate, final double frequency) {
        // The value of scale controls the rate of decay - large scale, fast decay.
        double scale = 2D * cnt;

        if (scale > sampleLength) {
            scale = sampleLength;
        }

        final double gain = sampleRate * (sampleLength - scale) / sampleLength;
        final double time = cnt / sampleRate;

        final double sinValue =
                (Math.sin(Math.TAU * frequency * time)
                        + Math.sin(Math.TAU * (frequency / 1.8D) * time)
                        + Math.sin(Math.TAU * (frequency / 1.5D) * time))
                        / 3.0D;

        return gain * sinValue;
    }

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

        int cnt2 = -8_000;
        int cnt3 = -16_000;
        int cnt4 = -24_000;

        for (int cnt1 = 0; cnt1 < samplesCount; cnt1++, cnt2++, cnt3++, cnt4++) {
            double sinValue = playEchoPulseHelper(cnt1, samplesCount, sampleRate, frequency);

            if (cnt2 > 0) {
                sinValue += 0.7D * playEchoPulseHelper(cnt2, samplesCount, sampleRate, frequency);
            }

            if (cnt3 > 0) {
                sinValue += 0.49D * playEchoPulseHelper(cnt3, samplesCount, sampleRate, frequency);
            }

            if (cnt4 > 0) {
                sinValue += 0.34D * playEchoPulseHelper(cnt4, samplesCount, sampleRate, frequency);
            }

            final int sampleValue = (int) sinValue;

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
