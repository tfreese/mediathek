// Created: 08 Aug. 2024
package org.quifft;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URISyntaxException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.quifft.config.BadConfigException;
import org.quifft.config.FFTConfig;

/**
 * @author Thomas Freese
 */
class TestParameterValidation {
    private static Path audioFile;

    @BeforeAll
    public static void beforeAll() throws URISyntaxException {
        audioFile = Path.of(Thread.currentThread().getContextClassLoader().getResource("600hz-tone-3secs-mono.wav").toURI());
    }

    @Test
    void testNumPointsIsLessThanWindowSize() {
        assertThrows(BadConfigException.class, () -> new QuiFFT(audioFile, new FFTConfig().windowSize(8192).numPoints(4096)).fullFFT());
    }

    @Test
    void testNumPointsIsNegative() {
        assertThrows(BadConfigException.class, () -> new QuiFFT(audioFile, new FFTConfig().numPoints(-2048)).fullFFT());
    }

    @Test
    void testNumPointsIsNotAPowerOfTwo() {
        assertThrows(BadConfigException.class, () -> new QuiFFT(audioFile, new FFTConfig().windowSize(512).numPoints(1023)).fullFFT());
    }

    @Test
    void testNumPointsNotSetAndWindowSizeNotPowerOfTwo() {
        assertThrows(BadConfigException.class, () -> new QuiFFT(audioFile, new FFTConfig().windowSize(8190)).fullFFT());
    }

    @Disabled("Allowed now")
    @Test
    void testStreamAndNormalizedOutputWithoutDecibelScale() {
        assertThrows(BadConfigException.class, () -> new QuiFFT(audioFile, new FFTConfig().normalized(true).decibelScale(false)).fftStream());
    }

    @Test
    void testWindowFunctionIsNull() {
        assertThrows(BadConfigException.class, () -> new QuiFFT(audioFile, new FFTConfig().windowFunction(null)).fullFFT());
    }

    @Test
    void testWindowOverlapEqualsOne() {
        assertThrows(BadConfigException.class, () -> new QuiFFT(audioFile, new FFTConfig().windowOverlap(1D)).fullFFT());
    }

    @Test
    void testWindowOverlapIsGreaterThanOne() {
        assertThrows(BadConfigException.class, () -> new QuiFFT(audioFile, new FFTConfig().windowOverlap(2.5D)).fullFFT());
    }

    @Test
    void testWindowOverlapIsNegative() {
        assertThrows(BadConfigException.class, () -> new QuiFFT(audioFile, new FFTConfig().windowOverlap(-0.5D)).fullFFT());
    }

    @Test
    void testWindowSizeIsNegative() {
        assertThrows(BadConfigException.class, () -> new QuiFFT(audioFile, new FFTConfig().windowSize(-1)).fullFFT());
    }
}
