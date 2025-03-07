// Created: 08 Aug. 2024
package de.freese.player.fft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.freese.player.fft.config.FFTConfig;
import de.freese.player.fft.math.FFTMath;
import de.freese.player.fft.output.SpectraResult;
import de.freese.player.fft.output.Spectrum;
import de.freese.player.fft.output.SpectrumStream;
import de.freese.player.fft.sampling.WindowFunction;

/**
 * @author Thomas Freese
 */
class TestSpectrumStream {
    private static Path mono500Hz3SecsWav;
    private static Path mono600Hz3SecsWav;
    private static Path stereo500Hz3SecsWav;
    private static Path stereo600Hz500MsWAV;

    @BeforeAll
    static void createPaths() throws Exception {
        mono600Hz3SecsWav = getPath("600hz-tone-3secs-mono.wav");
        stereo600Hz500MsWAV = getPath("600hz-tone-500ms-stereo.wav");
        mono500Hz3SecsWav = getPath("500hz-tone-3secs-mono.wav");
        stereo500Hz3SecsWav = getPath("500hz-tone-3secs-stereo.wav");
    }

    private static Path getPath(final String resource) throws URISyntaxException {
        return Path.of(Thread.currentThread().getContextClassLoader().getResource(resource).toURI());
    }

    @Test
    void testCallToStringOnStreamWithoutError() throws IOException, UnsupportedAudioFileException {
        assertNotNull(FFTFactory.createStream(mono600Hz3SecsWav).toString());
    }

    @Test
    void testComputeApprox4TimesAsManyFramesWith75PercentOverlap() throws IOException, UnsupportedAudioFileException {
        final SpectrumStream noOverlap = FFTFactory.createStream(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0));
        final SpectrumStream overlap = FFTFactory.createStream(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.75D));

        final Iterator<Spectrum> iteratorNoOverlap = noOverlap.iterator();
        final Iterator<Spectrum> iteratorOverlap = overlap.iterator();

        int noOverlapFramesCount = 0;
        int overlapFramesCount = 0;

        while (iteratorNoOverlap.hasNext()) {
            iteratorNoOverlap.next();
            noOverlapFramesCount++;
        }

        while (iteratorOverlap.hasNext()) {
            iteratorOverlap.next();
            overlapFramesCount++;
        }

        assertTrue(Math.abs(noOverlapFramesCount - (overlapFramesCount / 4)) <= 1);
    }

    @Test
    void testComputeApproxDoubleAsManyFramesWith50PercentOverlapFFTStream() throws IOException, UnsupportedAudioFileException {
        final SpectrumStream noOverlap = FFTFactory.createStream(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0));
        final SpectrumStream overlap = FFTFactory.createStream(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.5D));

        final Iterator<Spectrum> iteratorNoOverlap = noOverlap.iterator();
        final Iterator<Spectrum> iteratorOverlap = overlap.iterator();

        int noOverlapFramesCount = 0;
        int overlapFramesCount = 0;

        while (iteratorNoOverlap.hasNext()) {
            iteratorNoOverlap.next();
            noOverlapFramesCount++;
        }

        while (iteratorOverlap.hasNext()) {
            iteratorOverlap.next();
            overlapFramesCount++;
        }

        assertTrue(Math.abs(noOverlapFramesCount - (overlapFramesCount / 2)) <= 1);
    }

    @Test
    void testComputePeakAt500HzFor500HzMonoWavSignalFFTStream() throws IOException, UnsupportedAudioFileException {
        final SpectrumStream stream = FFTFactory.createStream(mono500Hz3SecsWav, new FFTConfig().decibelScale(true));

        final Iterator<Spectrum> iterator = stream.iterator();

        // call next() a few times so any issues with window overlap will be caught
        for (int i = 0; i < 5; i++) {
            iterator.next();
        }

        assertEquals(500D, FFTMath.findMaxAmplitude(iterator.next()).getHz(), stream.getFrequencyResolution());
    }

    @Test
    void testComputePeakAt500HzFor500HzStereoWavSignalFFTStream() throws IOException, UnsupportedAudioFileException {
        final SpectrumStream stream = FFTFactory.createStream(stereo500Hz3SecsWav, new FFTConfig().decibelScale(true));

        final Iterator<Spectrum> iterator = stream.iterator();

        // call next() a few times so any issues with window overlap will be caught
        for (int i = 0; i < 5; i++) {
            iterator.next();
        }

        assertEquals(500D, FFTMath.findMaxAmplitude(iterator.next()).getHz(), stream.getFrequencyResolution());
    }

    @Test
    void testComputeSameFrameStartTimesAsFullFFT() throws IOException, UnsupportedAudioFileException {
        final SpectraResult result = FFTFactory.createFull(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.25D));
        final SpectrumStream stream = FFTFactory.createStream(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.25D));

        final Iterator<Spectrum> iterator = stream.iterator();

        for (int i = 0; i < 3; i++) {
            assertEquals(result.getSpectrum(i).getFrameStartMs(), iterator.next().getFrameStartMs(), 0.0001D);
        }
    }

    @Test
    void testComputeSameNumberOfFramesAsFullFFT() throws IOException, UnsupportedAudioFileException {
        final SpectraResult result = FFTFactory.createFull(stereo600Hz500MsWAV, new FFTConfig().windowSize(8192));
        final SpectrumStream stream = FFTFactory.createStream(stereo600Hz500MsWAV, new FFTConfig().windowSize(8192));

        final Iterator<Spectrum> iterator = stream.iterator();
        int streamedFFTFramesCount = 0;

        while (iterator.hasNext()) {
            iterator.next();
            streamedFFTFramesCount++;
        }

        assertEquals(result.length(), streamedFFTFramesCount);
    }

    @Test
    void testComputeTheSameFFTOutputAsFullFFT() throws IOException, UnsupportedAudioFileException {
        final SpectraResult result = FFTFactory.createFull(stereo600Hz500MsWAV, new FFTConfig().windowSize(8192));
        final SpectrumStream stream = FFTFactory.createStream(stereo600Hz500MsWAV, new FFTConfig().windowSize(8192));

        final Iterator<Spectrum> iterator = stream.iterator();

        for (int i = 0; i < result.length(); i++) {
            final Spectrum spectrum = iterator.next();

            for (int j = 0; j < result.getSpectrum(i).length(); j++) {
                assertEquals(result.getSpectrum(i).getFrequency(j).getAmplitude(), spectrum.getFrequency(j).getAmplitude(), 0.01D);
            }
        }
    }

    @Test
    void testHaveSameFrameStartAndEndTimesAsFullFft() throws IOException, UnsupportedAudioFileException {
        final SpectraResult result = FFTFactory.createFull(stereo600Hz500MsWAV);
        final SpectrumStream stream = FFTFactory.createStream(stereo600Hz500MsWAV);

        final Iterator<Spectrum> iterator = stream.iterator();

        for (int i = 0; i < 3; i++) {
            final Spectrum spectrum = iterator.next();
            assertEquals(result.getSpectrum(i).getFrameStartMs(), spectrum.getFrameStartMs(), 0.001D);
            assertEquals(result.getSpectrum(i).getFrameEndMs(), spectrum.getFrameEndMs(), 0.001D);
        }
    }

    @Test
    void testKeepFftResultMetadataConstantWhenZeroPaddingFFTStream() throws IOException, UnsupportedAudioFileException {
        final double EXPECTED_WINDOW_DURATION = 46.44D;
        final SpectrumStream noPaddingResult = FFTFactory.createStream(mono600Hz3SecsWav, new FFTConfig().windowSize(2048));
        final SpectrumStream withPaddingResult = FFTFactory.createStream(mono600Hz3SecsWav, new FFTConfig().windowSize(2048).numPoints(4096));

        final Iterator<Spectrum> iteratorNoPadding = noPaddingResult.iterator();
        final Iterator<Spectrum> iteratorWithPadding = withPaddingResult.iterator();

        // test window duration
        final Spectrum noPaddingSpectrum = iteratorNoPadding.next();
        final Spectrum withPaddingSpectrum = iteratorWithPadding.next();

        assertEquals(EXPECTED_WINDOW_DURATION, noPaddingSpectrum.getFrameEndMs(), 0.01D);
        assertEquals(EXPECTED_WINDOW_DURATION, withPaddingSpectrum.getFrameEndMs(), 0.01D);

        // test frequency resolution
        assertEquals(21.5D, noPaddingResult.getFrequencyResolution(), 0.1D);
        assertEquals(10.7D, withPaddingResult.getFrequencyResolution(), 0.1D);
    }

    @Test
    void testSetAndReturnFftParametersCorrectlyFFTStream() throws Exception {
        final FFTConfig fftConfig = new FFTConfig()
                .windowSize(512)
                .windowFunction(WindowFunction.HANNING)
                .windowOverlap(0.25D)
                .numPoints(1024)
                .decibelScale(true)
                .normalized(false);

        final SpectraResult spectraResult = FFTFactory.createFull(mono600Hz3SecsWav, fftConfig);
        final FFTConfig fftStreamConfig = spectraResult.getFFTConfig();

        // test FFT params object
        assertEquals(fftConfig.getWindowSize(), fftStreamConfig.getWindowSize());
        assertEquals(fftConfig.getWindowFunction(), fftStreamConfig.getWindowFunction());
        assertEquals(fftConfig.getWindowOverlap(), fftStreamConfig.getWindowOverlap(), 0D);
        assertEquals(fftConfig.getNumPoints(), fftStreamConfig.getNumPoints());
        assertEquals(fftConfig.isDecibelScale(), fftStreamConfig.isDecibelScale());
        assertEquals(fftConfig.isNormalized(), fftStreamConfig.isNormalized());
    }

    @Test
    void testTakeHalfAsMuchTimeBetweenWindowsWith50PercentOverlapFFTStream() throws Exception {
        final SpectrumStream noOverlap = FFTFactory.createStream(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0D));
        final SpectrumStream overlap = FFTFactory.createStream(mono600Hz3SecsWav, new FFTConfig().windowOverlap(0.5D));

        final Iterator<Spectrum> iteratorNoOverlap = noOverlap.iterator();
        final Iterator<Spectrum> iteratorOverlap = overlap.iterator();

        final Spectrum noOverlap1 = iteratorNoOverlap.next();
        final Spectrum noOverlap2 = iteratorNoOverlap.next();
        final Spectrum overlap1 = iteratorOverlap.next();
        final Spectrum overlap2 = iteratorOverlap.next();

        final double noOverlapTime = noOverlap2.getFrameStartMs() - noOverlap1.getFrameStartMs();
        final double overlapTime = overlap2.getFrameStartMs() - overlap1.getFrameStartMs();

        assertEquals(overlapTime, noOverlapTime / 2, 0.001D);
    }

    @Test
    void testThrowExceptionIfNextCalledWhenNoMoreSamplesRemain() {
        assertThrows(NoSuchElementException.class, () -> {
            final SpectrumStream stream = FFTFactory.createStream(stereo600Hz500MsWAV);
            final Iterator<Spectrum> iterator = stream.iterator();

            for (int i = 0; i < 100; i++) {
                iterator.next();
            }
        });
    }
}
