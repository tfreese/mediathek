// Created: 08 Aug. 2024
package org.quifft;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.quifft.reader.AudioReader;
import org.quifft.reader.AudioReaderFactory;

/**
 * @author Thomas Freese
 */
class TestAudioReader {
    @Disabled("mp3 currently not supported")
    @Test
    void testMp3() throws Exception {
        final int SAMPLE_RATE = 44100;
        final int AUDIO_DURATION_SEC = 3;
        final int expectedSampleCount = SAMPLE_RATE * AUDIO_DURATION_SEC;

        final AudioReader audioReader = AudioReaderFactory.of(Path.of(Thread.currentThread().getContextClassLoader().getResource("600hz-tone-3secs-mono.mp3").toURI()));
        final int[] extractedSamples = audioReader.getWaveform();

        assertEquals(expectedSampleCount, extractedSamples.length);
    }

    @Test
    void testWav() throws Exception {
        final int SAMPLE_RATE = 44100;
        final int AUDIO_DURATION_SEC = 3;
        final int expectedSampleCount = SAMPLE_RATE * AUDIO_DURATION_SEC;

        final AudioReader audioReader = AudioReaderFactory.of(Path.of(Thread.currentThread().getContextClassLoader().getResource("600hz-tone-3secs-mono.wav").toURI()));
        final int[] extractedSamples = audioReader.getWaveform();

        assertEquals(expectedSampleCount, extractedSamples.length);
    }
}
