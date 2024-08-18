// Created: 03 Aug. 2024
package de.freese.player;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import de.freese.player.player.SourceDataLinePlayer;

/**
 * @author Thomas Freese
 */
public final class Analysis {
    public static void main(final String[] args) throws Exception {
        final Path path = Path.of("samples/sample.wav");

        try (InputStream inputStream = path.toUri().toURL().openStream();
             AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream)) {
            final AudioFormat audioFormat = audioInputStream.getFormat();

            final float frameRate = audioFormat.getFrameRate();
            System.out.printf("Frame Rate, Frames/Second: %f%n", frameRate);

            int frameSize = audioFormat.getFrameSize();

            if (frameSize == AudioSystem.NOT_SPECIFIED) {
                // some audio formats may have unspecified frame size
                // in that case we may read any amount of bytes.
                frameSize = 1;
            }

            System.out.printf("Frame Size in Bytes: %d%n", frameSize);

            final float sampleRate = audioFormat.getSampleRate();
            System.out.printf("Sample Rate: %f%n", sampleRate);

            final int sampleSize = audioFormat.getSampleSizeInBits();
            System.out.printf("Sample Size in Bits: %d%n", sampleSize);

            final int channels = audioFormat.getChannels();
            System.out.printf("Channels: %d%n", channels);

            final float length = audioInputStream.getFrameLength() / frameRate;
            System.out.printf("Length1 in seconds: %f%n", length);

            // 44: WAV Header in Bytes
            final float length2 = (Files.size(path) - 44) / (frameRate * frameSize);
            System.out.printf("Length2 in seconds: %f%n", length2);

            final int timeOfFrame = (int) frameRate / 1000;
            System.out.printf("Time of Frame in ms: %d%n", timeOfFrame);
            System.out.printf("BufferSize for 1 Frame: %d%n", timeOfFrame * frameSize * channels);

            // final int bufSize = (int) (frameRate * frameSize) / 10;
            // System.out.println("Buffer Size: " + bufSize);
            //
            // final int frameSample = (int) (timeOfFrame * frameRate) / 1000;
            // System.out.println("Frame Sample: " + frameSample);
            //
            // final int runTimes = (int) (length * 1000) / timeOfFrame;
            // System.out.println("Run Times: " + runTimes);
            //
            //
            // int[][] freq = new int[runTimes][frameSample];

            // fft(audioInputStream);

            final SourceDataLinePlayer sourceDataLinePlayer = new SourceDataLinePlayer(audioFormat);

            // Read Frames for 1/10 Second.
            final int bufferSize = (int) (frameRate / 10D) * frameSize * channels;
            final byte[] audioData = new byte[bufferSize];

            for (int i = 0; i < 10; i++) {
                final int bytesRead = audioInputStream.read(audioData);
                // sourceDataLinePlayer.play(audioData, bytesRead);

                if (channels == 1) {
                    // Mono
                    final List<Double> samples = new ArrayList<>(bytesRead / 2);

                    for (int j = 0; j < bytesRead / 2; j++) {
                        final double sample;

                        if (audioFormat.isBigEndian()) {
                            sample = (audioData[2 * j + 1] & 0xFF | audioData[2 * j] << 8) / (double) SourceDataLinePlayer.MAX_16_BIT;
                        }
                        else {
                            sample = (audioData[2 * j] & 0xFF | audioData[2 * j + 1] << 8) / (double) SourceDataLinePlayer.MAX_16_BIT;
                        }

                        samples.add(sample);
                    }

                    sourceDataLinePlayer.play(samples);
                }
                else {
                    // Stereo
                    final List<Double> samplesLeft = new ArrayList<>(bytesRead / 4);
                    final List<Double> samplesRight = new ArrayList<>(bytesRead / 4);

                    for (int j = 0; j < bytesRead / 4; j++) {
                        final double left;
                        final double right;

                        if (audioFormat.isBigEndian()) {
                            left = (audioData[4 * j + 1] & 0xFF | audioData[4 * j] << 8) / (double) SourceDataLinePlayer.MAX_16_BIT;
                            right = (audioData[4 * j + 3] & 0xFF | audioData[4 * j + 2] << 8) / (double) SourceDataLinePlayer.MAX_16_BIT;
                        }
                        else {
                            left = (audioData[4 * j] & 0xFF | audioData[4 * j + 1] << 8) / (double) SourceDataLinePlayer.MAX_16_BIT;
                            right = (audioData[4 * j + 2] & 0xFF | audioData[4 * j + 3] << 8) / (double) SourceDataLinePlayer.MAX_16_BIT;
                        }

                        // final double sampleMono = (left + right) / 2.0D;

                        samplesLeft.add(left);
                        samplesRight.add(right);
                    }

                    sourceDataLinePlayer.play(samplesLeft, samplesRight);

                    // final double[] x = samplesLeft.stream().mapToDouble(d -> d).toArray();
                    // final Complex[] complexes = FFT.fft(x);
                    // final Complex[] complexes = BasicFFT.fft(x);
                    //
                    // for (int c = 0; c < complexes.length; c++) {
                    //     System.out.println(complexes[c]);
                    // }

                    // final double[] outputReal = new double[x.length];
                    // final double[] outputImaginary = new double[x.length];
                    // BasicFFT.transform(x, outputReal, outputImaginary);
                    //
                    // for (int c = 0; c < x.length; c++) {
                    //     System.out.printf("%f + %fi%n", outputReal[c], outputImaginary[c]);
                    // }
                }
            }

            sourceDataLinePlayer.close();
        }
    }

    private static void fft(final AudioInputStream audioInputStream) throws IOException {
        final AudioFormat audioFormat = audioInputStream.getFormat();

        final float frameRate = audioFormat.getFrameRate();
        final float sampleRate = audioFormat.getSampleRate();
        final int channels = audioFormat.getChannels();
        final float length = audioInputStream.getFrameLength() / frameRate;

        // Calculate the number of equidistant points in time
        final int n = (int) (length * sampleRate) / channels;
        // final int n = 3;
        // System.out.printf("n: %d (number of equidistant points)%n", n);

        // Calculate the time interval at each equidistant point
        final float h = length / n;
        // final float h = timeOfFrame;
        // System.out.printf("h: %f (length of each time interval in seconds)%n", h);

        // final byte[] audioBytes = new byte[frameSize]; // 1 Frame
        final byte[] audioBytes = new byte[n * 2];
        // final byte[] audioBytes = new byte[timeOfFrame * frameSize * channels * n];
        // inputStream.skip(44); // Skip WAV Header
        final int bytesRead = audioInputStream.read(audioBytes);

        // if (bytesRead != n * 2) {
        //     throw new IllegalStateException("Could not read buffer");
        // }

        // Determine the original Endian encoding format
        final boolean isBigEndian = audioFormat.isBigEndian();

        // this array is the value of the signal at time i*h
        // final int[] x = new int[n];
        final int[] x = new int[audioBytes.length / 2];

        // convert each pair of byte values from the byte array to an Endian value
        for (int i = 0; i < audioBytes.length; i += 2) {
            int b1 = audioBytes[i];
            int b2 = audioBytes[i + 1];

            if (b1 < 0) {
                b1 += 0x100;
            }

            if (b2 < 0) {
                b2 += 0x100;
            }

            final int value;

            // Store the data based on the original Endian encoding format
            if (!isBigEndian) {
                value = (b1 << 8) + b2;
            }
            else {
                value = b1 + (b2 << 8);
            }

            x[i / 2] = value;
        }

        // do the DFT for each value of x sub j and store as f sub j
        final double[] f = new double[x.length];

        for (int j = 0; j < x.length; j++) {
            double firstSummation = 0D;
            double secondSummation = 0D;

            for (int k = 0; k < n; k++) {
                final double twoPInjk = ((2D * Math.PI) / n) * (j * k);
                firstSummation += x[k] * Math.cos(twoPInjk);
                secondSummation += x[k] * Math.sin(twoPInjk);
            }

            f[j] = Math.abs(Math.sqrt(Math.pow(firstSummation, 2D) + Math.pow(secondSummation, 2D)));

            final double amplitude = 2D * f[j] / n;
            final double frequency = (j * h) / (length * sampleRate);
            System.out.println("frequency = " + frequency + ", amp = " + amplitude);
        }
    }

    private Analysis() {
        super();
    }
}
