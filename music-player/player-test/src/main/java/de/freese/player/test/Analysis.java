// Created: 03 Aug. 2024
package de.freese.player.test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import de.freese.player.core.player.AudioPlayerSink;
import de.freese.player.core.player.DefaultAudioPlayerSink;

/**
 * @author Thomas Freese
 */
public final class Analysis {
    public static void main(final String[] args) throws Exception {
        final Path path = Path.of("music-player/samples/sample.wav");

        try (InputStream inputStream = path.toUri().toURL().openStream();
             AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream)) {
            final AudioFormat audioFormat = audioInputStream.getFormat();

            final double frameRate = audioFormat.getFrameRate();
            System.out.printf("Frame Rate, Frames/Second: %f%n", frameRate);

            int frameSize = audioFormat.getFrameSize();

            if (frameSize == AudioSystem.NOT_SPECIFIED) {
                // some audio formats may have unspecified frame size
                // in that case we may read any amount of bytes.
                frameSize = 1;
            }

            System.out.printf("Frame Size in Bytes: %d%n", frameSize);

            final double sampleRate = audioFormat.getSampleRate();
            System.out.printf("Sample Rate: %f%n", sampleRate);

            final int sampleSize = audioFormat.getSampleSizeInBits();
            System.out.printf("Sample Size in Bits: %d%n", sampleSize);

            final int channels = audioFormat.getChannels();
            System.out.printf("Channels: %d%n", channels);

            final double length = audioInputStream.getFrameLength() / frameRate;
            System.out.printf("Length1 in seconds: %f%n", length);

            // 44: WAV Header in Bytes
            final double length2 = (Files.size(path) - 44) / (frameRate * frameSize);
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

            final AudioPlayerSink audioPlayerSink = new DefaultAudioPlayerSink(audioFormat);

            // Read Frames for 1/10 Second.
            final int bufferSize = (int) (frameRate / 10D) * frameSize * channels;
            final byte[] audioData = new byte[bufferSize];

            for (int i = 0; i < 10; i++) {
                final int bytesRead = audioInputStream.read(audioData);

                audioPlayerSink.play(audioData, bytesRead);
            }

            audioPlayerSink.close();
        }
    }

    private Analysis() {
        super();
    }
}
