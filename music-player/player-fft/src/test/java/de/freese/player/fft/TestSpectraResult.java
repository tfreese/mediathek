// Created: 08 Aug. 2024
package de.freese.player.fft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.freese.player.fft.config.FFTConfig;
import de.freese.player.fft.math.FFTMath;
import de.freese.player.fft.output.SpectraResult;
import de.freese.player.fft.output.Spectrum;
import de.freese.player.fft.sampling.WindowFunction;

/**
 * @author Thomas Freese
 */
class TestSpectraResult {
    private static Path mono500Hz3SecsWav;
    private static Path mono600Hz3SecsWav;
    private static Path stereo500Hz3SecsWav;
    private static Path stereo600Hz3SecsWav;
    private static Path stereo600Hz500MsWAV;

    @BeforeAll
    public static void createPaths() throws Exception {
        mono600Hz3SecsWav = getPath("600hz-tone-3secs-mono.wav");
        stereo600Hz3SecsWav = getPath("600hz-tone-3secs-stereo.wav");
        stereo600Hz500MsWAV = getPath("600hz-tone-500ms-stereo.wav");
        mono500Hz3SecsWav = getPath("500hz-tone-3secs-mono.wav");
        stereo500Hz3SecsWav = getPath("500hz-tone-3secs-stereo.wav");
    }

    private static Path getPath(final String resource) throws URISyntaxException {
        return Path.of(Thread.currentThread().getContextClassLoader().getResource(resource).toURI());
    }

    @Test
    void testAmplitudesBetween0And1WhenNormalized() throws IOException, UnsupportedAudioFileException {
        final SpectraResult result = FFTFactory.createFull(stereo600Hz500MsWAV, new FFTConfig().decibelScale(false).normalized(true));

        for (Spectrum spectrum : result) {
            spectrum.forEach(frequency -> {
                assertTrue(frequency.getAmplitude() >= 0D);
                assertTrue(frequency.getAmplitude() <= 1D);
            });
        }
    }

    @Test
    void testComputeApprox4TimesAsManyFramesWith75PercentOverlap() throws IOException, UnsupportedAudioFileException {
        final SpectraResult noOverlap = FFTFactory.createFull(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0D));
        final SpectraResult overlap = FFTFactory.createFull(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.75D));

        assertTrue(Math.abs(noOverlap.length() - (overlap.length() / 4)) <= 1);
    }

    @Test
    void testComputeApproxDoubleAsManyFramesWith50PercentOverlap() throws IOException, UnsupportedAudioFileException {
        final SpectraResult noOverlap = FFTFactory.createFull(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0D));
        final SpectraResult overlap = FFTFactory.createFull(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.5D));

        assertTrue(Math.abs(noOverlap.length() - (overlap.length() / 2)) <= 1);
    }

    @Test
    void testKeepFftResultMetadataConstantWhenZeroPadding() throws IOException, UnsupportedAudioFileException {
        final double EXPECTED_WINDOW_DURATION = 46.44D;
        final SpectraResult noPaddingResult = FFTFactory.createFull(mono600Hz3SecsWav, new FFTConfig().windowSize(2048));
        final SpectraResult withPaddingResult = FFTFactory.createFull(mono600Hz3SecsWav, new FFTConfig().windowSize(2048).numPoints(4096));

        // test window duration
        assertEquals(EXPECTED_WINDOW_DURATION, noPaddingResult.getSpectrum(0).getFrameEndMs(), 0.01D);
        assertEquals(EXPECTED_WINDOW_DURATION, withPaddingResult.getSpectrum(0).getFrameEndMs(), 0.01D);

        // test frequency resolution
        assertEquals(21.5, noPaddingResult.getFrequencyResolution(), 0.1D);
        assertEquals(10.7, withPaddingResult.getFrequencyResolution(), 0.1D);
    }

    @Test
    void testKeepWavMetadataEqualWhetherStereoOrMono() throws IOException, UnsupportedAudioFileException {
        final SpectraResult stereoResult = FFTFactory.createFull(stereo600Hz3SecsWav);
        final SpectraResult monoResult = FFTFactory.createFull(mono600Hz3SecsWav);

        assertEquals(stereoResult.getFileDurationMs(), monoResult.getFileDurationMs());
        assertEquals(stereoResult.length(), monoResult.length());
        assertEquals(stereoResult.getSpectrum(0).getFrameEndMs(), monoResult.getSpectrum(0).getFrameEndMs(), 0.001D);
    }

    @Test
    void testNotAllowLastFramesEndTimesToBeGreaterThanAudioLength() throws IOException, UnsupportedAudioFileException {
        // no overlap (only check last frame)
        final SpectraResult result = FFTFactory.createFull(stereo600Hz3SecsWav, new FFTConfig().windowOverlap(0));
        assertEquals(result.getSpectrum(result.length() - 1).getFrameEndMs(), result.getFileDurationMs(), 0.0001D);

        // 50% overlap = 2x the frames (check the last 2 frames)
        final SpectraResult overlapResult = FFTFactory.createFull(stereo600Hz3SecsWav, new FFTConfig().windowOverlap(0.5D));
        assertEquals(overlapResult.getSpectrum(overlapResult.length() - 1).getFrameEndMs(), result.getFileDurationMs(), 0.0001D);
        assertEquals(overlapResult.getSpectrum(overlapResult.length() - 2).getFrameEndMs(), result.getFileDurationMs(), 0.0001D);
    }

    @Test
    void testPeakAt500HzFor500HzMonoWavSignal() throws IOException, UnsupportedAudioFileException {
        final SpectraResult result = FFTFactory.createFull(mono500Hz3SecsWav);
        assertEquals(500, FFTMath.findMaxAmplitude(result.getSpectrum(0)).getHz(), result.getFrequencyResolution());
    }

    @Test
    void testPeakAt500HzFor500HzStereoWavSignal() throws IOException, UnsupportedAudioFileException {
        final SpectraResult result = FFTFactory.createFull(stereo500Hz3SecsWav);
        assertEquals(500, FFTMath.findMaxAmplitude(result.getSpectrum(0)).getHz(), result.getFrequencyResolution());
    }

    @Test
    void testSetAndReturnFftParametersCorrectly() throws Exception {
        final FFTConfig fftConfig = new FFTConfig()
                .windowSize(512)
                .windowFunction(WindowFunction.HANNING)
                .windowOverlap(0.25D)
                .numPoints(1024)
                .decibelScale(true)
                .normalized(false);

        final SpectraResult spectraResult = FFTFactory.createFull(mono600Hz3SecsWav, fftConfig);
        final FFTConfig fullFFTConfig = spectraResult.getFFTConfig();

        // test FFT params object
        assertEquals(fftConfig.getWindowSize(), fullFFTConfig.getWindowSize());
        assertEquals(fftConfig.getWindowFunction(), fullFFTConfig.getWindowFunction());
        assertEquals(fftConfig.getWindowOverlap(), fullFFTConfig.getWindowOverlap(), 0D);
        assertEquals(fftConfig.getNumPoints(), fullFFTConfig.getNumPoints());
        assertEquals(fftConfig.isDecibelScale(), fullFFTConfig.isDecibelScale());
        assertEquals(fftConfig.isNormalized(), fullFFTConfig.isNormalized());
    }

    @Test
    void testTakeHalfAsMuchTimeBetweenWindowsWith50PercentOverlap() throws IOException, UnsupportedAudioFileException {
        final SpectraResult noOverlap = FFTFactory.createFull(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0D));
        final SpectraResult overlap = FFTFactory.createFull(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.5D));

        final double noOverlapTime = noOverlap.getSpectrum(1).getFrameStartMs() - noOverlap.getSpectrum(0).getFrameStartMs();
        final double overlapTime = overlap.getSpectrum(1).getFrameStartMs() - overlap.getSpectrum(0).getFrameStartMs();

        assertEquals(overlapTime, noOverlapTime / 2D, 0.001D);
    }

    @Test
    void testThrowExceptionWhenPassedFileWithNoExtension() throws Exception {
        final Path noExtensionFile = getPath("file-with-no-extension");

        assertThrows(UnsupportedAudioFileException.class, () -> FFTFactory.createFull(noExtensionFile));
    }

    @Test
    void testThrowExceptionWhenPassedNonAudioFile() throws Exception {
        final Path audio = getPath("text.txt");

        assertThrows(UnsupportedAudioFileException.class, () -> FFTFactory.createFull(audio));
    }

    @Test
    void testToStringOnResultWithoutError() throws IOException, UnsupportedAudioFileException {
        assertNotNull(FFTFactory.createFull(mono600Hz3SecsWav).toString());
        assertNotNull(FFTFactory.createFull(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0D).numPoints(8192)).toString());
    }
}
