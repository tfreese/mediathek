// Created: 03 Nov. 2024
package de.freese.player.core.signal;

import javax.sound.sampled.AudioFormat;

/**
 * @author Thomas Freese
 */
public final class StereoPingPong implements Signal {
    private final double frequency;

    public StereoPingPong(final double frequency) {
        super();

        this.frequency = frequency;
    }

    @Override
    public byte[] generate(final AudioFormat audioFormat, final double seconds) {
        // final int channels = audioFormat.getChannels();

        final int frameSize = audioFormat.getFrameSize(); // Bytes per Frame
        // float frameRate = audioFormat.getFrameRate();

        // int sampleSizeInBits = audioFormat.getSampleSizeInBits();
        final double sampleRate = audioFormat.getSampleRate();

        final int samplesCount = (int) (sampleRate * seconds);
        final int arrayLength = samplesCount * frameSize;

        final byte[] audioBytes = new byte[arrayLength];
        int bufferIndex = 0;

        double leftGain = 0.0D;
        double rightGain = sampleRate;

        for (int step = 0; step < samplesCount; step++) {
            final double time = step / sampleRate;

            // Calculate time-varying gain for each speaker.
            if (step % (samplesCount / 8) == 0) {
                // swap gain values
                final double temp = leftGain;
                leftGain = rightGain;
                rightGain = temp;
            }

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
