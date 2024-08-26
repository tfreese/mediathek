// Created: 10 Aug. 2024
package de.freese.player.fft.reader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.freese.player.fft.config.FFTConfig;

/**
 * @author Thomas Freese
 */
public interface AudioReader extends Iterator<int[]> {
    static AudioReader of(final Path audioFile) throws UnsupportedAudioFileException, IOException {
        return new DefaultAudioReader(audioFile, new FFTConfig());
    }

    static AudioReader of(final Path audioFile, final FFTConfig fftConfig) throws UnsupportedAudioFileException, IOException {
        return new DefaultAudioReader(audioFile, fftConfig);
    }

    static AudioReader of(final AudioInputStream audioInputStream, final FFTConfig fftConfig) throws IOException {
        return new DefaultAudioReader(audioInputStream, fftConfig);
    }

    AudioFormat getAudioFormat();

    /**
     * Milliseconds.
     */
    long getDurationMs();

    FFTConfig getFFTConfig();

    /**
     * Bytes
     */
    long getLength();

    /**
     * Obtains waveform for entirety of audio file.
     */
    int[] getWaveform();

    default boolean isStereo() {
        return getAudioFormat().getChannels() == 2;
    }
}
