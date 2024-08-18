// Created: 08 Aug. 2024
package org.quifft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.quifft.config.FFTConfig;
import org.quifft.output.FFTFrame;
import org.quifft.output.FFTResult;
import org.quifft.output.FFTStream;
import org.quifft.sampling.WindowFunction;

/**
 * @author Thomas Freese
 */
class TestFFTStream {
    private static Path mono500Hz3SecsMP3;
    private static Path mono500Hz3SecsWav;
    private static Path mono600Hz3SecsMP3;
    private static Path mono600Hz3SecsWav;
    private static Path stereo500Hz3SecsMP3;
    private static Path stereo500Hz3SecsWav;
    private static Path stereo600Hz500MsWAV;

    @BeforeAll
    public static void createQuiFFTResult() throws Exception {
        mono600Hz3SecsWav = getPath("600hz-tone-3secs-mono.wav");
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
    void testCallToStringOnStreamWithoutError() throws IOException, UnsupportedAudioFileException {
        assertNotNull(new QuiFFT(mono600Hz3SecsWav).fftStream().toString());
    }

    @Test
    void testComputeApprox4TimesAsManyFramesWith75PercentOverlap() throws IOException, UnsupportedAudioFileException {
        final FFTStream noOverlap = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0)).fftStream();
        final FFTStream overlap = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.75D)).fftStream();

        int noOverlapFramesCount = 0;
        int overlapFramesCount = 0;

        while (noOverlap.hasNext()) {
            noOverlap.next();
            noOverlapFramesCount++;
        }

        while (overlap.hasNext()) {
            overlap.next();
            overlapFramesCount++;
        }

        assertTrue(Math.abs(noOverlapFramesCount - (overlapFramesCount / 4)) <= 1);
    }

    @Test
    void testComputeApproxDoubleAsManyFramesWith50PercentOverlapFFTStream() throws IOException, UnsupportedAudioFileException {
        final FFTStream noOverlap = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0)).fftStream();
        final FFTStream overlap = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.5D)).fftStream();

        int noOverlapFramesCount = 0;
        int overlapFramesCount = 0;

        while (noOverlap.hasNext()) {
            noOverlap.next();
            noOverlapFramesCount++;
        }

        while (overlap.hasNext()) {
            overlap.next();
            overlapFramesCount++;
        }

        assertTrue(Math.abs(noOverlapFramesCount - (overlapFramesCount / 2)) <= 1);
    }

    @Disabled("mp3 currently not supported")
    @Test
    void testComputePeakAt500HzFor500HzMonoMP3SignalFFTStream() throws IOException, UnsupportedAudioFileException {
        final FFTStream stream = new QuiFFT(mono500Hz3SecsMP3, new FFTConfig().decibelScale(true)).fftStream();

        // call next() a few times so any issues with window overlap will be caught
        for (int i = 0; i < 5; i++) {
            stream.next();
        }

        assertEquals(500D, FFTUtils.findFrequencyWithHighestAmplitude(stream.next()), stream.getFrequencyResolution());
    }

    @Test
    void testComputePeakAt500HzFor500HzMonoWavSignalFFTStream() throws IOException, UnsupportedAudioFileException {
        final FFTStream stream = new QuiFFT(mono500Hz3SecsWav, new FFTConfig().decibelScale(true)).fftStream();

        // call next() a few times so any issues with window overlap will be caught
        for (int i = 0; i < 5; i++) {
            stream.next();
        }

        assertEquals(500D, FFTUtils.findFrequencyWithHighestAmplitude(stream.next()), stream.getFrequencyResolution());
    }

    @Disabled("mp3 currently not supported")
    @Test
    void testComputePeakAt500HzFor500HzStereoMP3SignalFFTStream() throws IOException, UnsupportedAudioFileException {
        final FFTStream stream = new QuiFFT(stereo500Hz3SecsMP3, new FFTConfig().decibelScale(true)).fftStream();

        // call next() a few times so any issues with window overlap will be caught
        for (int i = 0; i < 5; i++) {
            stream.next();
        }

        assertEquals(500D, FFTUtils.findFrequencyWithHighestAmplitude(stream.next()), stream.getFrequencyResolution());
    }

    @Test
    void testComputePeakAt500HzFor500HzStereoWavSignalFFTStream() throws IOException, UnsupportedAudioFileException {
        final FFTStream stream = new QuiFFT(stereo500Hz3SecsWav, new FFTConfig().decibelScale(true)).fftStream();

        // call next() a few times so any issues with window overlap will be caught
        for (int i = 0; i < 5; i++) {
            stream.next();
        }

        assertEquals(500D, FFTUtils.findFrequencyWithHighestAmplitude(stream.next()), stream.getFrequencyResolution());
    }

    @Test
    void testComputeSameFrameStartTimesAsFullFFT() throws IOException, UnsupportedAudioFileException {
        final FFTFrame[] fullFFTFrames = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.25D)).fullFFT().getFFTFrames();
        final FFTStream fftStream = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.25D)).fftStream();

        for (int i = 0; i < 3; i++) {
            assertEquals(fullFFTFrames[i].getFrameStartMs(), fftStream.next().getFrameStartMs(), 0.0001D);
        }
    }

    @Test
    void testComputeSameNumberOfFramesAsFullFFT() throws IOException, UnsupportedAudioFileException {
        final FFTStream stream = new QuiFFT(stereo600Hz500MsWAV, new FFTConfig().windowSize(8192)).fftStream();
        final FFTResult full = new QuiFFT(stereo600Hz500MsWAV, new FFTConfig().windowSize(8192)).fullFFT();

        int streamedFFTFramesCount = 0;

        while (stream.hasNext()) {
            stream.next();
            streamedFFTFramesCount++;
        }

        assertEquals(full.getFFTFrames().length, streamedFFTFramesCount);
    }

    @Test
    void testComputeTheSameFFTOutputAsFullFFT() throws IOException, UnsupportedAudioFileException {
        final FFTStream stream = new QuiFFT(stereo600Hz500MsWAV, new FFTConfig().windowSize(8192)).fftStream();
        final FFTResult full = new QuiFFT(stereo600Hz500MsWAV, new FFTConfig().windowSize(8192)).fullFFT();

        for (int i = 0; i < full.getFFTFrames().length; i++) {
            final FFTFrame streamFrame = stream.next();

            for (int j = 0; j < full.getFFTFrames()[i].getFrequencies().length; j++) {
                assertEquals(full.getFFTFrames()[i].getFrequencies()[j].getAmplitude(), streamFrame.getFrequencies()[j].getAmplitude(), 0.01D);
            }
        }
    }

    @Test
    void testHaveSameFrameStartAndEndTimesAsFullFft() throws IOException, UnsupportedAudioFileException {
        final FFTStream stream = new QuiFFT(stereo600Hz500MsWAV).fftStream();
        final FFTResult full = new QuiFFT(stereo600Hz500MsWAV).fullFFT();

        for (int i = 0; i < 3; i++) {
            final FFTFrame streamFrame = stream.next();
            assertEquals(full.getFFTFrames()[i].getFrameStartMs(), streamFrame.getFrameStartMs(), 0.001D);
            assertEquals(full.getFFTFrames()[i].getFrameEndMs(), streamFrame.getFrameEndMs(), 0.001D);
        }
    }

    @Test
    void testKeepFftResultMetadataConstantWhenZeroPaddingFFTStream() throws IOException, UnsupportedAudioFileException {
        final double EXPECTED_WINDOW_DURATION = 46.44D;
        final FFTStream noPaddingResult = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowSize(2048)).fftStream();
        final FFTStream withPaddingResult = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowSize(2048).numPoints(4096)).fftStream();

        // test window duration
        final FFTFrame noPaddingFrame = noPaddingResult.next();
        final FFTFrame withPaddingFrame = withPaddingResult.next();

        assertEquals(EXPECTED_WINDOW_DURATION, noPaddingFrame.getFrameEndMs(), 0.01D);
        assertEquals(EXPECTED_WINDOW_DURATION, withPaddingFrame.getFrameEndMs(), 0.01D);

        // test frequency resolution
        assertEquals(21.5D, noPaddingResult.getFrequencyResolution(), 0.1D);
        assertEquals(10.7D, withPaddingResult.getFrequencyResolution(), 0.1D);
    }

    @Test
    void testSetAndReturnFftParametersCorrectlyFFTStream() throws IOException, UnsupportedAudioFileException {
        final QuiFFT quiFFT = new QuiFFT(mono600Hz3SecsWav, new FFTConfig()
                .windowSize(512)
                .windowFunction(WindowFunction.HANNING)
                .windowOverlap(0.25D)
                .numPoints(1024)
                .decibelScale(true)
                .normalized(false)
        );
        final FFTConfig fftStreamConfig = quiFFT.fftStream().getFFTConfig();

        // test FFT params object
        assertEquals(512, fftStreamConfig.getWindowSize());
        assertEquals(WindowFunction.HANNING, fftStreamConfig.getWindowFunction());
        assertEquals(0.25D, fftStreamConfig.getWindowOverlap(), 0D);
        assertEquals(1024, fftStreamConfig.getNumPoints());
        assertTrue(fftStreamConfig.isDecibelScale());
        assertFalse(fftStreamConfig.isNormalized());

        // test QuiFFT accessor methods
        assertEquals(512, quiFFT.getFFTConfig().getWindowSize());
        assertEquals(WindowFunction.HANNING, quiFFT.getFFTConfig().getWindowFunction());
        assertEquals(0.25D, quiFFT.getFFTConfig().getWindowOverlap(), 0D);
        assertEquals(1024, quiFFT.getFFTConfig().getNumPoints());
        assertTrue(quiFFT.getFFTConfig().isDecibelScale());
        assertFalse(quiFFT.getFFTConfig().isNormalized());
    }

    @Test
    void testTakeHalfAsMuchTimeBetweenWindowsWith50PercentOverlapFFTStream() throws IOException, UnsupportedAudioFileException {
        final FFTStream noOverlap = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0D)).fftStream();
        final FFTStream overlap = new QuiFFT(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.5D)).fftStream();

        final FFTFrame noOverlap1 = noOverlap.next();
        final FFTFrame noOverlap2 = noOverlap.next();
        final FFTFrame overlap1 = overlap.next();
        final FFTFrame overlap2 = overlap.next();

        final double noOverlapTime = noOverlap2.getFrameStartMs() - noOverlap1.getFrameStartMs();
        final double overlapTime = overlap2.getFrameStartMs() - overlap1.getFrameStartMs();

        assertEquals(overlapTime, noOverlapTime / 2, 0.001D);
    }

    @Test
    void testThrowExceptionIfNextCalledWhenNoMoreSamplesRemain() {
        assertThrows(NoSuchElementException.class, () -> {
            final FFTStream fftStream = new QuiFFT(stereo600Hz500MsWAV).fftStream();

            for (int i = 0; i < 100; i++) {
                fftStream.next();
            }
        });
    }
}
