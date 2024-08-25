// Created: 10 Aug. 2024
package de.freese.player.fft.reader;

import java.util.Iterator;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * @author Thomas Freese
 */
public interface AudioReader extends Iterator<int[]> {
    AudioFormat getAudioFormat();

    AudioInputStream getAudioInputStream();

    /**
     * Milliseconds.
     */
    long getDurationMs();

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
