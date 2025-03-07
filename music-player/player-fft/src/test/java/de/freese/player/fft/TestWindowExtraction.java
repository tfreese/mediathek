// Created: 08 Aug. 2024
package de.freese.player.fft;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.freese.player.fft.reader.AudioReader;
import de.freese.player.fft.sampling.SampleWindowExtractor;
import de.freese.player.fft.sampling.WindowFunction;

/**
 * @author Thomas Freese
 */
class TestWindowExtraction {
    private static final double[] HANNING_8 = {0, 0.19, 0.61, 0.95, 0.95, 0.61, 0.19, 0};

    private static int[] exampleWave;

    @BeforeAll
    static void createExampleWave() {
        exampleWave = new int[32];
        Arrays.fill(exampleWave, 32768);
    }

    private static Path getPath(final String resource) throws URISyntaxException {
        return Path.of(Thread.currentThread().getContextClassLoader().getResource(resource).toURI());
    }

    @Test
    void testAppropriateWindowsWithOverlap() {
        final int[] signal = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        final int WINDOW_SIZE = 4;
        final double OVERLAP = 0.50D;
        final int[] expected1 = {1, 2, 3, 4};
        final int[] expected2 = {3, 4, 5, 6};
        final int[] expected3 = {5, 6, 7, 8};
        final int[] expected4 = {7, 8, 9, 10};
        final int[] expected5 = {9, 10, 11, 12};

        final SampleWindowExtractor extractor = new SampleWindowExtractor(false, WINDOW_SIZE, WindowFunction.RECTANGULAR, OVERLAP, 0);

        assertArrayEquals(expected1, extractor.extractWindow(signal, 0));
        assertArrayEquals(expected2, extractor.extractWindow(signal, 1));
        assertArrayEquals(expected3, extractor.extractWindow(signal, 2));
        assertArrayEquals(expected4, extractor.extractWindow(signal, 3));
        assertArrayEquals(expected5, extractor.extractWindow(signal, 4));
    }

    @Test
    void testAppropriateWindowsWithOverlapAndNonIntegralSignalLength() {
        final int[] signal = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        final int WINDOW_SIZE = 4;
        final double OVERLAP = 0.50D;
        final int[] expected1 = {1, 2, 3, 4};
        final int[] expected2 = {3, 4, 5, 6};
        final int[] expected3 = {5, 6, 7, 8};
        final int[] expected4 = {7, 8, 9, 10};
        final int[] expected5 = {9, 10, 0, 0};

        final SampleWindowExtractor extractor = new SampleWindowExtractor(false, WINDOW_SIZE, WindowFunction.RECTANGULAR, OVERLAP, 0);

        assertArrayEquals(expected1, extractor.extractWindow(signal, 0));
        assertArrayEquals(expected2, extractor.extractWindow(signal, 1));
        assertArrayEquals(expected3, extractor.extractWindow(signal, 2));
        assertArrayEquals(expected4, extractor.extractWindow(signal, 3));
        assertArrayEquals(expected5, extractor.extractWindow(signal, 4));
    }

    @Test
    void testAverageChannelsToConvertStereoToMono() {
        final int[] stereoWave = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110};
        final int[] expectedMonoWave = {5, 25, 45, 65, 85, 105};

        final SampleWindowExtractor extractor = new SampleWindowExtractor(true, 6, WindowFunction.RECTANGULAR, 0D, 0);
        final int[] extractedWave = extractor.extractWindow(stereoWave, 0);

        assertArrayEquals(expectedMonoWave, extractedWave);
    }

    @Test
    void testIgnoreZeroPaddingWhenApplyingWindow() {
        final int[] unitWave = new int[8];
        Arrays.fill(unitWave, 100);

        final SampleWindowExtractor windowExtractor = new SampleWindowExtractor(false, 8, WindowFunction.HANNING, 0D, 24);
        final int[] extractedWindow = windowExtractor.extractWindow(unitWave, 0);

        assertEquals(32, extractedWindow.length);

        for (int i = 0; i < 8; i++) {
            assertEquals((int) Math.round(100D * HANNING_8[i]), extractedWindow[i]);
        }

        for (int i = 8; i < extractedWindow.length; i++) {
            assertEquals(0, extractedWindow[i]);
        }
    }

    @Test
    void testMultiplyingCoefficientsToSignal() {
        final int[] unitWave = new int[8];
        Arrays.fill(unitWave, 100);

        final SampleWindowExtractor windowExtractor = new SampleWindowExtractor(false, 8, WindowFunction.HANNING, 0D, 0);
        final int[] extractedWindow = windowExtractor.extractWindow(unitWave, 0);

        assertEquals(HANNING_8.length, extractedWindow.length);

        for (int i = 0; i < extractedWindow.length; i++) {
            assertEquals((int) Math.round(100D * HANNING_8[i]), extractedWindow[i]);
        }
    }

    @Test
    void testPartialLastWindowMono() {
        final int[] wave = {1, 2, 3, 4, 5, 6};
        final int WINDOW_SIZE = 4;
        final int[] expected1 = {1, 2, 3, 4};
        final int[] expected2 = {5, 6, 0, 0};

        final SampleWindowExtractor extractor = new SampleWindowExtractor(false, WINDOW_SIZE, WindowFunction.RECTANGULAR, 0D, 0);

        assertArrayEquals(expected1, extractor.extractWindow(wave, 0));
        assertArrayEquals(expected2, extractor.extractWindow(wave, 1));
    }

    @Test
    void testPartialLastWindowStereo() {
        final int[] wave = {1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6};
        final int WINDOW_SIZE = 4;
        final int[] expected1 = {1, 2, 3, 4};
        final int[] expected2 = {5, 6, 0, 0};

        final SampleWindowExtractor extractor = new SampleWindowExtractor(true, WINDOW_SIZE, WindowFunction.RECTANGULAR, 0D, 0);

        assertArrayEquals(expected1, extractor.extractWindow(wave, 0));
        assertArrayEquals(expected2, extractor.extractWindow(wave, 1));
    }

    @Test
    void testSameWindowFromWavStereoSignalAsFromMono() throws Exception {
        final Path stereoFile = getPath("500hz-tone-3secs-stereo.wav");
        final Path monoFile = getPath("500hz-tone-3secs-mono.wav");
        final AudioReader audioReaderStereo = AudioReader.of(stereoFile);
        final AudioReader audioReaderMono = AudioReader.of(monoFile);

        final SampleWindowExtractor stereoExtractor = new SampleWindowExtractor(true, 1024, WindowFunction.RECTANGULAR, 0D, 0);
        final SampleWindowExtractor monoExtractor = new SampleWindowExtractor(false, 1024, WindowFunction.RECTANGULAR, 0D, 0);

        final int[] stereoWindow = stereoExtractor.extractWindow(audioReaderStereo.getWaveform(), 1);
        final int[] monoWindow = monoExtractor.extractWindow(audioReaderMono.getWaveform(), 1);

        assertArrayEquals(stereoWindow, monoWindow);
    }

    @Test
    void testWindowsOfCorrectLengthAndValues() {
        final int WINDOW_SIZE = 16;

        final SampleWindowExtractor windowExtractor = new SampleWindowExtractor(false, WINDOW_SIZE, WindowFunction.RECTANGULAR, 0D, 0);
        final int[] window1 = windowExtractor.extractWindow(exampleWave, 0);
        final int[] window2 = windowExtractor.extractWindow(exampleWave, 1);

        final int[] expectedWindow = new int[16];
        Arrays.fill(expectedWindow, 32768);

        assertArrayEquals(expectedWindow, window1);
        assertArrayEquals(expectedWindow, window2);
    }
}
