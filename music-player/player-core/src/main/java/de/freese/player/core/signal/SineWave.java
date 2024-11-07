// Created: 03 Nov. 2024
package de.freese.player.core.signal;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import de.freese.player.core.util.PlayerUtils;

/**
 * @author Thomas Freese
 */
public final class SineWave implements Signal {
    private final double frequency;

    public SineWave(final double frequency) {
        super();

        this.frequency = frequency;
    }

    @Override
    public byte[] generate(final AudioFormat audioFormat, final double seconds) {
        final int channels = audioFormat.getChannels();

        final int frameSize = audioFormat.getFrameSize(); // Bytes per Frame
        // float frameRate = audioFormat.getFrameRate();

        // int sampleSizeInBits = audioFormat.getSampleSizeInBits();
        final double sampleRate = audioFormat.getSampleRate();

        final int samplesCount = (int) (sampleRate * seconds);
        final int arrayLength = samplesCount * frameSize;

        final ByteBuffer byteBuffer = ByteBuffer.allocate(arrayLength);

        for (int step = 0; step < samplesCount; step++) {
            final double time = step / sampleRate;

            final double sinValue = Math.sin(Math.TAU * frequency * time);

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
