// Created: 17 Juli 2024
package de.freese.player.core.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.freese.player.core.model.Window;

/**
 * @author Thomas Freese
 * //@see com.sun.media.sound.Toolkit
 */
public final class PlayerUtils {
    /**
     * short: -32768 to 32767
     */
    public static final int MAX_16_BIT = Short.MAX_VALUE + 1;

    /**
     * Gets the time in milliseconds for the given number of bytes.<br>
     * //@see com.sun.media.sound.Toolkit#bytes2millis
     */
    public static long bytesToMillis(final AudioFormat format, final long bytes) {
        return (long) (bytes / (double) format.getFrameRate() * 1000.0D / format.getFrameSize());
    }

    public static int createSampleMono(final boolean isBigEndian, final byte[] audioBytes, final int index) {
        // final int bytesPerFrame = 2;

        if (isBigEndian) {
            return audioBytes[2 * index + 1] & 0xFF | audioBytes[2 * index] << 8;
        }

        return audioBytes[2 * index] & 0xFF | audioBytes[2 * index + 1] << 8;
    }

    public static int[] createSampleStereo(final boolean isBigEndian, final byte[] audioBytes, final int index) {
        // final int bytesPerFrame = 4;
        final int sampleLeft;
        final int sampleRight;

        if (isBigEndian) {
            sampleLeft = audioBytes[4 * index + 1] & 0xFF | audioBytes[4 * index] << 8;
            sampleRight = audioBytes[4 * index + 3] & 0xFF | audioBytes[4 * index + 2] << 8;
        }
        else {
            sampleLeft = audioBytes[4 * index] & 0xFF | audioBytes[4 * index + 1] << 8;
            sampleRight = audioBytes[4 * index + 2] & 0xFF | audioBytes[4 * index + 3] << 8;
        }

        return new int[]{sampleLeft, sampleRight};
    }

    public static int[] createSamplesMono(final AudioFormat audioFormat, final byte[] audioBytes) {
        final boolean isBigEndian = audioFormat.isBigEndian();

        final int[] samples = new int[audioBytes.length / 2];

        for (int i = 0; i < samples.length; i++) {
            samples[i] = createSampleMono(isBigEndian, audioBytes, i);
        }

        return samples;
    }

    /**
     * Index 0: Left
     * Index 1: Right
     */
    public static int[][] createSamplesStereo(final AudioFormat audioFormat, final byte[] audioBytes) {
        final boolean isBigEndian = audioFormat.isBigEndian();

        final int[] samplesLeft = new int[audioBytes.length / 4];
        final int[] samplesRight = new int[audioBytes.length / 4];

        for (int i = 0; i < samplesLeft.length; i++) {
            final int[] leftRight = createSampleStereo(isBigEndian, audioBytes, i);

            samplesLeft[i] = leftRight[0];
            samplesRight[i] = leftRight[1];
        }

        return new int[][]{samplesLeft, samplesRight};
    }

    /**
     * <pre>{@code
     * Using the natural log, ln, log base e:
     * linear-to-db(x) = ln(x) / (ln(10) / 20)
     * db-to-linear(x) = e^(x * (ln(10) / 20))
     *
     * Using the common log, log, log base 10:
     * linear-to-db(x) = log(x) * 20
     * db-to-linear(x) = 10^(x / 20)
     * }</pre>
     *
     * @see FloatControl.Type#MASTER_GAIN
     * //@see com.sun.media.sound.Toolkit#dBToLinear
     */
    public static double dBToLinear(final double dB) {
        return Math.pow(10.0D, dB / 20.0D);
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

        return Window.of(audioFormat, bytes, 0, 0);
    }

    /**
     * Gets the time in milliseconds for the given number of frames.
     */
    public static long framesToMillies(final AudioFormat format, final long frames) {
        return (long) (((double) frames) / (double) format.getFrameRate() * 1000.0D);
    }

    public static Duration getDuration(final Path file) throws Exception {
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            return getDuration(file, audioInputStream.getFormat());
        }
    }

    public static Duration getDuration(final Path file, final AudioFormat audioFormat) throws IOException {
        final long audioFileLength = Files.size(file);
        final double durationInSeconds = audioFileLength / ((double) audioFormat.getFrameSize() * audioFormat.getFrameRate());

        return Duration.ofSeconds((long) durationInSeconds);
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

    public static double getMilliesPerSample(final AudioFormat audioFormat) {
        return 1D / audioFormat.getSampleRate();
    }

    public static double getMilliesPerSample(final Path file) throws Exception {
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            final Duration duration = getDuration(file, audioInputStream.getFormat());

            return (double) duration.toMillis() / (double) audioInputStream.getFrameLength();
        }
    }

    /**
     * <pre>{@code
     * Using the natural log, ln, log base e:
     * linear-to-db(x) = ln(x) / (ln(10) / 20)
     * db-to-linear(x) = e^(x * (ln(10) / 20))
     *
     * Using the common log, log, log base 10:
     * linear-to-db(x) = log(x) * 20
     * db-to-linear(x) = 10^(x / 20)
     * }</pre>
     *
     * @see FloatControl.Type#MASTER_GAIN
     * //@see com.sun.media.sound.Toolkit#linearToDB
     */
    public static double linearToDB(final double linear) {
        return Math.log(((Double.compare(linear, 0.0D) == 0) ? 0.0001D : linear) / Math.log(10.0D) * 20.0D);
        // if (Double.compare(linear, 0.0D) == 0) {
        //     return 0D;
        // }
        //
        // return Math.log10(linear) * 20D;
    }

    /**
     * Gets the number of frames needed to play the specified number of milliseconds.
     */
    public static long milliesToFrames(final AudioFormat format, final long millis) {
        return (long) (millis * (double) format.getFrameRate() / 1000.0D);
    }

    /**
     * Gets the number of bytes needed to play the specified number of milliseconds.<br>
     * //@see com.sun.media.sound.Toolkit#millis2bytes
     */
    public static long millisToBytes(final AudioFormat format, final long millis) {
        final long result = (long) (millis * (double) format.getFrameRate() / 1000.0D * format.getFrameSize());

        return align(result, format.getFrameSize());
    }

    /**
     * Scales a Value (Wikipedia).<br>
     *
     * @param value double
     * @param minOld double
     * @param maxOld double
     * @param minNew double
     * @param maxNew double
     */
    public static double rescale(final double value, final double minOld, final double maxOld, final double minNew, final double maxNew) {
        return minNew + (((value - minOld) * (maxNew - minNew)) / (maxOld - minOld));
    }

    /**
     * If a sample is outside the range, it will be clipped (rounded to –1.0 or +1.0).
     */
    public static byte[] sampleToByte(final AudioFormat audioFormat, final double sampleLeft, final double sampleRight) {
        if (Double.isNaN(sampleLeft)) {
            throw new IllegalArgumentException("sampleLeft is NaN");
        }

        if (Double.isNaN(sampleRight)) {
            throw new IllegalArgumentException("sampleRight is NaN");
        }

        // clip if outside [-1, +1]
        // double left = sampleLeft;
        final double left = Math.clamp(sampleLeft, -1.0D, +1.0D);
        // if (left < -1.0D) {
        //     left = -1.0D;
        // }
        // else if (left > +1.0D) {
        //     left = +1.0D;
        // }

        // double right = sampleRight;
        final double right = Math.clamp(sampleLeft, -1.0D, +1.0D);
        // if (right < -1.0D) {
        //     right = -1.0D;
        // }
        // else if (right > +1.0D) {
        //     right = +1.0D;
        // }

        short sLeft = (short) (MAX_16_BIT * left);
        if (Double.compare(left, 1.0D) == 0) {
            sLeft = Short.MAX_VALUE;   // special case since 32768 not a short
        }

        short sRight = (short) (MAX_16_BIT * right);
        if (Double.compare(right, 1.0D) == 0) {
            sRight = Short.MAX_VALUE;   // special case since 32768 not a short
        }

        final byte[] buffer = new byte[4];

        if (audioFormat.isBigEndian()) {
            buffer[0] = (byte) (sLeft >> 8);
            buffer[1] = (byte) sLeft;
            buffer[2] = (byte) (sRight >> 8);
            buffer[3] = (byte) sRight;
        }
        else {
            buffer[0] = (byte) sLeft;
            buffer[1] = (byte) (sLeft >> 8);
            buffer[2] = (byte) sRight;
            buffer[3] = (byte) (sRight >> 8);
        }

        return buffer;
    }

    /**
     * If a sample is outside the range, it will be clipped (rounded to –SAMPLE_RATE or +SAMPLE_RATE).
     */
    public static byte[] sampleToByte(final AudioFormat audioFormat, final int sampleLeft, final int sampleRight) {
        final int maxRange = (int) Math.min(audioFormat.getSampleRate(), Short.MAX_VALUE);

        // int left = sampleLeft;
        final int left = Math.clamp(sampleLeft, -maxRange, +maxRange);
        // if (left < -maxRange) {
        //     left = -maxRange;
        // }
        // else if (left > +maxRange) {
        //     left = +maxRange;
        // }

        // int right = sampleRight;
        final int right = Math.clamp(sampleRight, -maxRange, +maxRange);
        // if (right < -maxRange) {
        //     right = -maxRange;
        // }
        // else if (right > +maxRange) {
        //     right = +maxRange;
        // }

        final byte[] buffer = new byte[4];

        if (audioFormat.isBigEndian()) {
            buffer[0] = (byte) (left >> 8);
            buffer[1] = (byte) left;
            buffer[2] = (byte) (right >> 8);
            buffer[3] = (byte) right;
        }
        else {
            buffer[0] = (byte) left;
            buffer[1] = (byte) (left >> 8);
            buffer[2] = (byte) right;
            buffer[3] = (byte) (right >> 8);
        }

        return buffer;
    }

    /**
     * If the sample is outside the range, it will be clipped (rounded to –1.0 or +1.0).
     */
    public static byte[] sampleToByte(final AudioFormat audioFormat, final double sampleMono) {
        if (Double.isNaN(sampleMono)) {
            throw new IllegalArgumentException("sample is NaN");
        }

        // clip if outside [-1, +1]
        final double mono = Math.clamp(sampleMono, -1.0D, +1.0D);
        // if (mono < -1.0D) {
        //     mono = -1.0D;
        // }
        // if (mono > +1.0D) {
        //     mono = +1.0D;
        // }

        short s = (short) (MAX_16_BIT * mono);
        if (Double.compare(mono, 1.0D) == 0) {
            s = Short.MAX_VALUE;   // special case since 32768 not a short
        }

        final byte[] buffer = new byte[2];

        if (audioFormat.isBigEndian()) {
            buffer[0] = (byte) (s >> 8);
            buffer[1] = (byte) s;
        }
        else {
            buffer[0] = (byte) s;
            buffer[1] = (byte) (s >> 8);
        }

        return buffer;
    }

    /**
     * If the sample is outside the range, it will be clipped (rounded to –SAMPLE_RATE or +SAMPLE_RATE).
     */
    public static void sampleToByte(final AudioFormat audioFormat, final int sampleMono, final Consumer<Byte> byteConsumer) {
        final int sampleRate = (int) audioFormat.getSampleRate();

        final int mono = Math.clamp(sampleMono, -sampleRate, +sampleRate);

        if (audioFormat.isBigEndian()) {
            byteConsumer.accept((byte) (mono >> 8));
            byteConsumer.accept((byte) mono);
        }
        else {
            byteConsumer.accept((byte) mono);
            byteConsumer.accept((byte) (mono >> 8));
        }
    }

    /**
     * If the sample is outside the range, it will be clipped (rounded to –SAMPLE_RATE or +SAMPLE_RATE).
     */
    public static byte[] sampleToByte(final AudioFormat audioFormat, final int sampleMono) {
        final int sampleRate = (int) audioFormat.getSampleRate();

        // clip if outside [-SAMPLE_RATE, +SAMPLE_RATE]
        final int mono = Math.clamp(sampleMono, -sampleRate, +sampleRate);
        // if (mono < -sampleRate) {
        //     mono = -(int) sampleRate;
        // }
        //
        // if (mono > +sampleRate) {
        //     mono = +(int) sampleRate;
        // }

        final byte[] buffer = new byte[2];

        if (audioFormat.isBigEndian()) {
            buffer[0] = (byte) (mono >> 8);
            buffer[1] = (byte) mono;
        }
        else {
            buffer[0] = (byte) mono;
            buffer[1] = (byte) (mono >> 8);
        }

        return buffer;
    }

    public static String toFileName(final URI uri) {
        String fileName = uri.getPath();

        // Escape special Characters.
        fileName = fileName.replace(" ", "\\ ");
        fileName = fileName.replace("(", "\\(");
        fileName = fileName.replace(")", "\\)");
        fileName = fileName.replace("'", "\\'");
        fileName = fileName.replace("&", "\\&");
        fileName = fileName.replace("$", "\\$");
        fileName = fileName.replace(";", "\\;");

        return fileName;
    }

    public static String toString(final Duration duration) {
        final long days = duration.toDaysPart();
        final int hours = duration.toHoursPart();
        final int minutes = duration.toMinutesPart();
        final int seconds = duration.toSecondsPart();

        final String value;

        if (days != 0) {
            value = "%d Days %02d:%02d:%02d".formatted(days, hours, minutes, seconds);
        }
        else if (hours != 0) {
            value = "%d:%02d:%02d".formatted(hours, minutes, seconds);
        }
        else if (minutes != 0) {
            value = "%d:%02d".formatted(minutes, seconds);
        }
        else {
            value = "0:%02d".formatted(seconds);
        }

        return value;
    }

    public static String toStringForTable(final URI uri) {
        final List<String> splits = new ArrayList<>(List.of(uri.getPath().split("/")));

        while (splits.size() > 3) {
            splits.removeFirst();
        }

        return String.join("/", splits);
    }

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
                audioFormat.isBigEndian()); // ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder(), Platform.isBigEndian()

        return AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);
    }

    /**
     * Returns bytes aligned to a multiple of block size
     * the return value will be in the range of (bytes-blockSize+1) ... bytes<br>
     * //@see com.sun.media.sound.Toolkit#align
     */
    static long align(final long bytes, final int blockSize) {
        if (blockSize <= 1) {
            return bytes;
        }

        return bytes - (bytes % blockSize);
    }

    /**
     * //@see com.sun.media.sound.Toolkit#align
     */
    static int align(final int bytes, final int blockSize) {
        if (blockSize <= 1) {
            return bytes;
        }

        return bytes - (bytes % blockSize);
    }

    private PlayerUtils() {
        super();
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
}
