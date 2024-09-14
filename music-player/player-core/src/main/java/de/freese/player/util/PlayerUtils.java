// Created: 17 Juli 2024
package de.freese.player.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.freese.player.model.Window;

/**
 * @author Thomas Freese
 */
public final class PlayerUtils {
    public static int[] createSamplesMono(final AudioFormat audioFormat, final byte[] audioBytes) {
        final boolean isBigEndian = audioFormat.isBigEndian();
        final int bytesPerFrame = 2;

        final int[] samples = new int[audioBytes.length / bytesPerFrame];

        for (int i = 0; i < samples.length; i++) {
            final int sample;

            if (isBigEndian) {
                sample = audioBytes[bytesPerFrame * i + 1] & 0xFF | audioBytes[bytesPerFrame * i] << 8;
            }
            else {
                sample = audioBytes[bytesPerFrame * i] & 0xFF | audioBytes[bytesPerFrame * i + 1] << 8;
            }

            samples[i] = sample;
        }

        return samples;
    }

    /**
     * Index 0: Left
     * Index 1: Right
     */
    public static int[][] createSamplesStereo(final AudioFormat audioFormat, final byte[] audioBytes) {
        final boolean isBigEndian = audioFormat.isBigEndian();
        final int bytesPerFrame = 4;

        final int[] samplesLeft = new int[audioBytes.length / bytesPerFrame];
        final int[] samplesRight = new int[audioBytes.length / bytesPerFrame];

        for (int i = 0; i < samplesLeft.length; i++) {
            final int sampleLeft;
            final int sampleRight;

            if (isBigEndian) {
                sampleLeft = audioBytes[bytesPerFrame * i + 1] & 0xFF | audioBytes[bytesPerFrame * i] << 8;
                sampleRight = audioBytes[bytesPerFrame * i + 3] & 0xFF | audioBytes[bytesPerFrame * i + 2] << 8;
            }
            else {
                sampleLeft = audioBytes[bytesPerFrame * i] & 0xFF | audioBytes[bytesPerFrame * i + 1] << 8;
                sampleRight = audioBytes[bytesPerFrame * i + 2] & 0xFF | audioBytes[bytesPerFrame * i + 3] << 8;
            }

            samplesLeft[i] = sampleLeft;
            samplesRight[i] = sampleRight;
        }

        return new int[][]{samplesLeft, samplesRight};
    }

    public static Window extractWindow(final AudioFormat audioFormat, final byte[] audioBytes, final int windowSize) {
        if (windowSize % 2 != 0) {
            throw new IllegalArgumentException("windowSize is not a power of 2: " + windowSize);
        }

        if (windowSize * 2 > audioBytes.length) {
            throw new IllegalArgumentException("windowSize bigger than byte content: " + windowSize + " > " + audioBytes.length);
        }

        final byte[] bytes = new byte[windowSize * 2];
        System.arraycopy(audioBytes, 0, bytes, 0, bytes.length);

        return new Window(audioFormat, bytes);
    }

    public static String getFileExtension(final String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    public static String getFileExtension(final File file) {
        return getFileExtension(file.getName());
    }

    public static String getFileExtension(final URI uri) {
        return getFileExtension(uri.toString());
    }

    public static String getFileExtension(final Path path) {
        return getFileExtension(path.toUri());
    }

    // private static int[] convertBytesToSamples(final byte[] bytes) {
    //     final int BYTES_PER_SAMPLE = 2;
    //     final int[] samples = new int[bytes.length / BYTES_PER_SAMPLE];
    //
    //     int b = 0;
    //
    //     for (int i = 0; i < samples.length; i++) {
    //         final ByteBuffer bb = ByteBuffer.allocate(BYTES_PER_SAMPLE);
    //         bb.order(ByteOrder.LITTLE_ENDIAN);
    //
    //         for (int j = 0; j < BYTES_PER_SAMPLE; j++) {
    //             bb.put(bytes[b++]);
    //         }
    //
    //         samples[i] = bb.getShort(0);
    //     }
    //
    //     return samples;
    // }

    public static AudioInputStream wrapTo16Bit(final InputStream inputStream) throws IOException, UnsupportedAudioFileException {
        final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
        final AudioFormat audioFormat = audioInputStream.getFormat();

        if (audioFormat.getSampleSizeInBits() == 16) {
            return audioInputStream;
        }

        final AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                audioFormat.getSampleRate(),
                16,
                audioFormat.getChannels(),
                audioFormat.getChannels() * 2,
                audioFormat.getSampleRate(),
                audioFormat.isBigEndian()); // ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder()

        return AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);
    }

    private PlayerUtils() {
        super();
    }
}
