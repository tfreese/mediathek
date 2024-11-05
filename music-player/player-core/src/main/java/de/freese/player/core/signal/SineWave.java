// Created: 03 Nov. 2024
package de.freese.player.core.signal;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import de.freese.player.core.util.PlayerUtils;

/**
 * @author Thomas Freese
 */
public final class SineWave implements Signal {
    @Override
    public byte[] generate(final AudioFormat audioFormat, final double seconds, final double frequency) {
        final int channels = audioFormat.getChannels();

        final int frameSize = audioFormat.getFrameSize(); // Bytes per Frame
        // float frameRate = audioFormat.getFrameRate();

        // int sampleSizeInBits = audioFormat.getSampleSizeInBits();
        final double sampleRate = audioFormat.getSampleRate();

        final int samplesCount = (int) (sampleRate * seconds);
        final int arrayLength = samplesCount * frameSize;

        // final byte[] audioBytes = new byte[arrayLength];
        // int bufferIndex = 0;
        final ByteBuffer byteBuffer = ByteBuffer.allocate(arrayLength);

        for (int step = 0; step < samplesCount; step++) {
            final double time = step / sampleRate;

            final double sinValue = Math.sin(Math.TAU * frequency * time);

            // for (int f = 0; f < frequencies.length; f++) {
            //     // Math.sin(2D * Math.PI * frequency * time);
            //     sinValue += Math.sin(Math.TAU * frequencies[f] * time);
            // }

            final int sampleValue = (int) (sampleRate * sinValue);

            if (channels == 1) {
                // Mono
                PlayerUtils.sampleToByte(audioFormat, sampleValue, byteBuffer::put);
            }
            else {
                // Stereo
                PlayerUtils.sampleToByte(audioFormat, sampleValue, byteBuffer::put);
                PlayerUtils.sampleToByte(audioFormat, sampleValue, byteBuffer::put);
            }

            // if (audioFormat.isBigEndian()) {
            //     for (int c = 0; c < channels; c++) {
            //         audioBytes[bufferIndex++] = (byte) (sampleValue >> 8);
            //         audioBytes[bufferIndex++] = (byte) sampleValue;
            //     }
            // }
            // else {
            //     for (int c = 0; c < channels; c++) {
            //         audioBytes[bufferIndex++] = (byte) sampleValue;
            //         audioBytes[bufferIndex++] = (byte) (sampleValue >> 8);
            //     }
            // }
        }

        byteBuffer.flip();

        if (byteBuffer.hasArray()) {
            return byteBuffer.array();
        }

        // DirectBuffers don't have an Array.
        final byte[] audioBytes = new byte[arrayLength];
        byteBuffer.get(audioBytes);

        return audioBytes;
    }
}
