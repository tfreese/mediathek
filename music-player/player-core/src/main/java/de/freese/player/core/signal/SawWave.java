// Created: 03 Nov. 2024
package de.freese.player.core.signal;

import javax.sound.sampled.AudioFormat;

/**
 * @author Thomas Freese
 */
public final class SawWave implements Signal {
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

        for (int cnt = 0; cnt < sampleLength; cnt++) {
            final double time = cnt / (double) sampleRate;

            final double sawValue = 2D * frequency * (time - Math.round(time));

            final int sampleValue = (int) (sampleRate * sawValue);

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
