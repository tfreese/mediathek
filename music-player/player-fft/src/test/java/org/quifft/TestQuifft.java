// Created: 08 Aug. 2024
package org.quifft;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.quifft.config.FFTConfig;
import org.quifft.output.FFTFrame;
import org.quifft.output.FFTResult;
import org.quifft.output.Frequency;
import org.quifft.sampling.WindowFunction;

/**
 * @author Thomas Freese
 */
class TestQuifft {
    private static Path mono500Hz3SecsMP3;
    private static Path mono500Hz3SecsWav;
    private static Path mono600Hz3SecsMP3;
    private static Path mono600Hz3SecsWav;
    private static Path stereo500Hz3SecsMP3;
    private static Path stereo500Hz3SecsWav;
    private static Path stereo600Hz3SecsWav;
    private static Path stereo600Hz500MsWAV;

    @BeforeAll
    public static void createQuiFFTResult() throws Exception {
        mono600Hz3SecsWav = getPath("600hz-tone-3secs-mono.wav");
        stereo600Hz3SecsWav = getPath("600hz-tone-3secs-stereo.wav");
        stereo600Hz500MsWAV = getPath("600hz-tone-500ms-stereo.wav");
        mono600Hz3SecsMP3 = getPath("600hz-tone-3secs-mono.mp3");
        mono500Hz3SecsWav = getPath("500hz-tone-3secs-mono.wav");
        stereo500Hz3SecsWav = getPath("500hz-tone-3secs-stereo.wav");
        mono500Hz3SecsMP3 = getPath("500hz-tone-3secs-mono.mp3");
        stereo500Hz3SecsMP3 = getPath("500hz-tone-3secs-stereo.mp3");
    }

    private static Path getPath(final String resource) throws URISyntaxException {
        return Path.of(Thread.currentThread().getContextClassLoader().getResource(resource).toURI());
    }

    @Test
    void testAmplitudesBetween0And1WhenNormalized() throws IOException, UnsupportedAudioFileException {
        final FFTResult result = new QuiFFT(stereo600Hz500MsWAV, new FFTConfig().decibelScale(false).normalized(true)).fullFFT();

        for (FFTFrame frame : result.getFFTFrames()) {
            for (Frequency frequency : frame.getFrequencies()) {
                assertTrue(frequency.getAmplitude() >= 0D);
                assertTrue(frequency.getAmplitude() <= 1D);
            }
        }
    }

    @Test
    void testComputeApprox4TimesAsManyFramesWith75PercentOverlap() throws IOException, UnsupportedAudioFileException {
        final FFTResult noOverlap = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0D)).fullFFT();
        final FFTResult overlap = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.75D)).fullFFT();

        assertTrue(Math.abs(noOverlap.getFFTFrames().length - (overlap.getFFTFrames().length / 4)) <= 1);
    }

    @Test
    void testComputeApproxDoubleAsManyFramesWith50PercentOverlap() throws IOException, UnsupportedAudioFileException {
        final FFTResult noOverlap = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0D)).fullFFT();
        final FFTResult overlap = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.5D)).fullFFT();

        assertTrue(Math.abs(noOverlap.getFFTFrames().length - (overlap.getFFTFrames().length / 2)) <= 1);
    }

    @Test
    void testKeepFftResultMetadataConstantWhenZeroPadding() throws IOException, UnsupportedAudioFileException {
        final double EXPECTED_WINDOW_DURATION = 46.44D;
        final FFTResult noPaddingResult = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowSize(2048)).fullFFT();
        final FFTResult withPaddingResult = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowSize(2048).numPoints(4096)).fullFFT();

        // test window duration
        assertEquals(EXPECTED_WINDOW_DURATION, noPaddingResult.getFFTFrames()[0].getFrameEndMs(), 0.01D);
        assertEquals(EXPECTED_WINDOW_DURATION, withPaddingResult.getFFTFrames()[0].getFrameEndMs(), 0.01D);

        // test frequency resolution
        assertEquals(21.5, noPaddingResult.getFrequencyResolution(), 0.1D);
        assertEquals(10.7, withPaddingResult.getFrequencyResolution(), 0.1D);
    }

    @Disabled("mp3 currently not supported")
    @Test
    void testKeepMP3MetadataEqualWhetherStereoOrMono() throws IOException, UnsupportedAudioFileException {
        final FFTResult stereoResult = new QuiFFT(stereo500Hz3SecsMP3).fullFFT();
        final FFTResult monoResult = new QuiFFT(mono500Hz3SecsMP3).fullFFT();

        assertEquals(stereoResult.getFileDurationMs(), monoResult.getFileDurationMs());
        assertEquals(stereoResult.getFFTFrames().length, monoResult.getFFTFrames().length);
        assertEquals(stereoResult.getFFTFrames()[0].getFrameEndMs(), monoResult.getFFTFrames()[0].getFrameEndMs(), 0.001D);
    }

    @Test
    void testKeepWavMetadataEqualWhetherStereoOrMono() throws IOException, UnsupportedAudioFileException {
        final FFTResult stereoResult = new QuiFFT(stereo600Hz3SecsWav).fullFFT();
        final FFTResult monoResult = new QuiFFT(mono600Hz3SecsWav).fullFFT();

        assertEquals(stereoResult.getFileDurationMs(), monoResult.getFileDurationMs());
        assertEquals(stereoResult.getFFTFrames().length, monoResult.getFFTFrames().length);
        assertEquals(stereoResult.getFFTFrames()[0].getFrameEndMs(), monoResult.getFFTFrames()[0].getFrameEndMs(), 0.001D);
    }

    @Test
    void testNotAllowLastFramesEndTimesToBeGreaterThanAudioLength() throws IOException, UnsupportedAudioFileException {
        // no overlap (only check last frame)
        final FFTResult result = new QuiFFT(stereo600Hz3SecsWav, new FFTConfig().windowOverlap(0)).fullFFT();
        assertEquals(result.getFFTFrames()[result.getFFTFrames().length - 1].getFrameEndMs(), result.getFileDurationMs(), 0.0001D);

        // 50% overlap = 2x the frames (check the last 2 frames)
        final FFTResult overlapResult = new QuiFFT(stereo600Hz3SecsWav, new FFTConfig().windowOverlap(0.5D)).fullFFT();
        assertEquals(overlapResult.getFFTFrames()[overlapResult.getFFTFrames().length - 1].getFrameEndMs(), result.getFileDurationMs(), 0.0001D);
        assertEquals(overlapResult.getFFTFrames()[overlapResult.getFFTFrames().length - 2].getFrameEndMs(), result.getFileDurationMs(), 0.0001D);
    }

    @Disabled("mp3 currently not supported")
    @Test
    void testPeakAt500HzFor500HzMonoMP3Signal() throws IOException, UnsupportedAudioFileException {
        final FFTResult result = new QuiFFT(mono500Hz3SecsMP3).fullFFT();
        assertEquals(500, FFTUtils.findFrequencyWithHighestAmplitude(result.getFFTFrames()[0]), result.getFrequencyResolution());
    }

    @Test
    void testPeakAt500HzFor500HzMonoWavSignal() throws IOException, UnsupportedAudioFileException {
        final FFTResult result = new QuiFFT(mono500Hz3SecsWav).fullFFT();
        assertEquals(500, FFTUtils.findFrequencyWithHighestAmplitude(result.getFFTFrames()[0]), result.getFrequencyResolution());
    }

    @Disabled("mp3 currently not supported")
    @Test
    void testPeakAt500HzFor500HzStereoMP3Signal() throws IOException, UnsupportedAudioFileException {
        final FFTResult result = new QuiFFT(stereo500Hz3SecsMP3).fullFFT();
        assertEquals(500, FFTUtils.findFrequencyWithHighestAmplitude(result.getFFTFrames()[0]), result.getFrequencyResolution());
    }

    @Test
    void testPeakAt500HzFor500HzStereoWavSignal() throws IOException, UnsupportedAudioFileException {
        final FFTResult result = new QuiFFT(stereo500Hz3SecsWav).fullFFT();
        assertEquals(500, FFTUtils.findFrequencyWithHighestAmplitude(result.getFFTFrames()[0]), result.getFrequencyResolution());
    }

    @Test
    void testSetAndReturnFftParametersCorrectly() throws IOException, UnsupportedAudioFileException {
        final QuiFFT quiFFT = new QuiFFT(mono600Hz3SecsWav, new FFTConfig()
                .windowSize(512)
                .windowFunction(WindowFunction.HANNING)
                .windowOverlap(0.25D)
                .numPoints(1024)
                .decibelScale(true)
                .normalized(false)
        );
        final FFTConfig fullFFTConfig = quiFFT.fullFFT().getFFTConfig();

        // test FFT params object
        assertEquals(512, fullFFTConfig.getWindowSize());
        assertEquals(WindowFunction.HANNING, fullFFTConfig.getWindowFunction());
        assertEquals(0.25D, fullFFTConfig.getWindowOverlap(), 0D);
        assertEquals(1024, (int) fullFFTConfig.getNumPoints());
        assertTrue(fullFFTConfig.isDecibelScale());
        assertFalse(fullFFTConfig.isNormalized());

        // test QuiFFT accessor methods
        assertEquals(512, quiFFT.getFFTConfig().getWindowSize());
        assertEquals(WindowFunction.HANNING, quiFFT.getFFTConfig().getWindowFunction());
        assertEquals(0.25D, quiFFT.getFFTConfig().getWindowOverlap(), 0D);
        assertEquals(1024, quiFFT.getFFTConfig().getNumPoints());
        assertTrue(quiFFT.getFFTConfig().isDecibelScale());
        assertFalse(quiFFT.getFFTConfig().isNormalized());
    }

    @Disabled("Stream of unsupported format")
    @Test
    void testSuccessfullyCompleteFftOn8BitAudio() throws Exception {
        final Path audio = getPath("600hz-tone-3secs-mono-8bit.wav");

        assertDoesNotThrow(() -> new QuiFFT(audio).fullFFT());
    }

    @Disabled("mp3 currently not supported")
    @Test
    void testSuccessfullyInitializeWithMP3File() throws IOException, UnsupportedAudioFileException {
        assertDoesNotThrow(() -> new QuiFFT(mono600Hz3SecsMP3));
    }

    @Test
    void testTakeHalfAsMuchTimeBetweenWindowsWith50PercentOverlap() throws IOException, UnsupportedAudioFileException {
        final FFTResult noOverlap = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0D)).fullFFT();
        final FFTResult overlap = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.5D)).fullFFT();

        final double noOverlapTime = noOverlap.getFFTFrames()[1].getFrameStartMs() - noOverlap.getFFTFrames()[0].getFrameStartMs();
        final double overlapTime = overlap.getFFTFrames()[1].getFrameStartMs() - overlap.getFFTFrames()[0].getFrameStartMs();

        assertEquals(overlapTime, noOverlapTime / 2D, 0.001D);
    }

    @Test
    void testThrowExceptionWhenPassedFileWithNoExtension() throws Exception {
        final Path noExtensionFile = getPath("file-with-no-extension");

        assertThrows(UnsupportedAudioFileException.class, () -> new QuiFFT(noExtensionFile));
    }

    @Test
    void testThrowExceptionWhenPassedNonAudioFile() throws Exception {
        final Path audio = getPath("text.txt");

        assertThrows(UnsupportedAudioFileException.class, () -> new QuiFFT(audio));
    }

    @Test
    void testToStringOnResultWithoutError() throws IOException, UnsupportedAudioFileException {
        assertNotNull(new QuiFFT(mono600Hz3SecsWav).fullFFT().toString());
        assertNotNull(new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0D).numPoints(8192)).fullFFT().toString());
    }
}
