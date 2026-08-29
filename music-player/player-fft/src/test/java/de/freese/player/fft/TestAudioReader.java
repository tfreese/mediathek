package de.freese.player.fft;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import de.freese.player.fft.reader.AudioReader;

/**
 * @author Thomas Freese
 * @since 08.08.2024
 */
class TestAudioReader {
    @Test
    void testWav() throws Exception {
        final int SAMPLE_RATE = 44100;
        final int AUDIO_DURATION_SEC = 3;
        final int expectedSampleCount = SAMPLE_RATE * AUDIO_DURATION_SEC;

        final AudioReader audioReader = AudioReader.of(Path.of(Thread.currentThread().getContextClassLoader().getResource("600hz-tone-3secs-mono.wav").toURI()));
        final int[] extractedSamples = audioReader.getWaveform();

        assertEquals(expectedSampleCount, extractedSamples.length);
    }
}
