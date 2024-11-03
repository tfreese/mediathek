// Created: 03 Nov. 2024
package de.freese.player.core.signal;

import javax.sound.sampled.AudioFormat;

/**
 * @author Thomas Freese
 */
public final class StereoPingPong implements Signal {
    @Override
    public byte[] generate(final AudioFormat audioFormat, final int seconds, final double frequency) {
        final int frameSize = audioFormat.getFrameSize();
        final float sampleRate = audioFormat.getSampleRate();
        final int channels = audioFormat.getChannels();

        // final int bytesPerSample = frameSize * channels;
        final int bytesPerSample = frameSize;
        final int byteCount = (int) sampleRate * bytesPerSample * seconds;
        final int sampleLength = byteCount / bytesPerSample;

        final byte[] audioBytes = new byte[byteCount];
        int bufferIndex = 0;

        double leftGain = 0.0D;
        double rightGain = sampleRate;

        for (int cnt = 0; cnt < sampleLength; cnt++) {
            // Calculate time-varying gain for each speaker.
            if (cnt % (sampleLength / 8) == 0) {
                // swap gain values
                final double temp = leftGain;
                leftGain = rightGain;
                rightGain = temp;
            }

            final double time = cnt / (double) sampleRate;

            // Generate data for left speaker.
            final double sinValueLeft = leftGain * Math.sin(Math.TAU * frequency * time);

            // Generate data for right speaker.
            final double sinValueRight = rightGain * Math.sin(Math.TAU * (frequency * 0.8D) * time);

            // final int sampleValueLeft = (int) (sampleRate * sinValueLeft);
            final int sampleValueLeft = (int) (sinValueLeft);
            // final int sampleValueRight = (int) (sampleRate * sinValueRight);
            final int sampleValueRight = (int) (sinValueRight);

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
