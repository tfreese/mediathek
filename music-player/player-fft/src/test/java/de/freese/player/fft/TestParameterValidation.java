// Created: 08 Aug. 2024
package de.freese.player.fft;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URISyntaxException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.freese.player.fft.config.BadConfigException;
import de.freese.player.fft.config.FFTConfig;

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
        assertThrows(BadConfigException.class, () -> FFTFactory.createFull(audioFile, new FFTConfig().windowSize(8192).numPoints(4096)));
    }

    @Test
    void testNumPointsIsNegative() {
        assertThrows(BadConfigException.class, () -> FFTFactory.createFull(audioFile, new FFTConfig().numPoints(-2048)));
    }

    @Test
    void testNumPointsIsNotAPowerOfTwo() {
        assertThrows(BadConfigException.class, () -> FFTFactory.createFull(audioFile, new FFTConfig().windowSize(512).numPoints(1023)));
    }

    @Test
    void testNumPointsNotSetAndWindowSizeNotPowerOfTwo() {
        assertThrows(BadConfigException.class, () -> FFTFactory.createFull(audioFile, new FFTConfig().windowSize(8190)));
    }

    @Disabled("Allowed now")
    @Test
    void testStreamAndNormalizedOutputWithoutDecibelScale() {
        assertThrows(BadConfigException.class, () -> FFTFactory.createStream(audioFile, new FFTConfig().normalized(true).decibelScale(false)));
    }

    @Test
    void testWindowFunctionIsNull() {
        assertThrows(BadConfigException.class, () -> FFTFactory.createFull(audioFile, new FFTConfig().windowFunction(null)));
    }

    @Test
    void testWindowOverlapEqualsOne() {
        assertThrows(BadConfigException.class, () -> FFTFactory.createFull(audioFile, new FFTConfig().windowOverlap(1D)));
    }

    @Test
    void testWindowOverlapIsGreaterThanOne() {
        assertThrows(BadConfigException.class, () -> FFTFactory.createFull(audioFile, new FFTConfig().windowOverlap(2.5D)));
    }

    @Test
    void testWindowOverlapIsNegative() {
        assertThrows(BadConfigException.class, () -> FFTFactory.createFull(audioFile, new FFTConfig().windowOverlap(-0.5D)));
    }

    @Test
    void testWindowSizeIsNegative() {
        assertThrows(BadConfigException.class, () -> FFTFactory.createFull(audioFile, new FFTConfig().windowSize(-1)));
    }
}
