// Created: 25 Aug. 2024
package de.freese.player.fft;

import java.io.IOException;
import java.nio.file.Path;

import javax.sound.sampled.UnsupportedAudioFileException;

import de.freese.player.fft.config.ConfigValidator;
import de.freese.player.fft.config.FFTConfig;
import de.freese.player.fft.math.FFTComputationWrapper;
import de.freese.player.fft.math.FFTMath;
import de.freese.player.fft.output.Frequency;
import de.freese.player.fft.output.SpectraResult;
import de.freese.player.fft.output.Spectrum;
import de.freese.player.fft.output.SpectrumStream;
import de.freese.player.fft.reader.AudioReader;
import de.freese.player.fft.reader.AudioReaderFactory;
import de.freese.player.fft.sampling.SampleWindowExtractor;

/**
 * @author Thomas Freese
 */
public final class FFTFactory {
    /**
     * Performs an FFT for the entire audio file.
     *
     * @return an FFT result containing metadata of this FFT and an array of all {@link Spectrum}s computed
     */
    public static SpectraResult createFull(final Path path) throws UnsupportedAudioFileException, IOException {
        return createFull(AudioReaderFactory.of(path), new FFTConfig());
    }

    /**
     * Performs an FFT for the entire audio file.
     *
     * @return an FFT result containing metadata of this FFT and an array of all {@link Spectrum}s computed
     */
    public static SpectraResult createFull(final Path path, final FFTConfig fftConfig) throws UnsupportedAudioFileException, IOException {
        return createFull(AudioReaderFactory.of(path), fftConfig);
    }

    /**
     * Performs an FFT for the entire audio file.
     *
     * @return an FFT result containing metadata of this FFT and an array of all {@link Spectrum}s computed
     */
    public static SpectraResult createFull(final AudioReader audioReader, final FFTConfig fftConfig) {
        ConfigValidator.validate(fftConfig, false);

        final boolean isStereo = audioReader.isStereo();
        final float sampleRate = audioReader.getAudioFormat().getSampleRate();
        final int[] wave = audioReader.getWaveform();

        final int lengthOfWave = wave.length / (isStereo ? 2 : 1);
        final double frameOverlapMultiplier = 1D / (1D - fftConfig.getWindowOverlap());
        final int numFrames = (int) Math.ceil(((double) lengthOfWave / fftConfig.getWindowSize()) * frameOverlapMultiplier);
        final Spectrum[] spectra = new Spectrum[numFrames];

        final SpectraResult result = new SpectraResult(audioReader, fftConfig, spectra);

        final SampleWindowExtractor windowExtractor = new SampleWindowExtractor(isStereo, fftConfig.getWindowSize(),
                fftConfig.getWindowFunction(), fftConfig.getWindowOverlap(), fftConfig.zeroPadLength());
        double currentAudioTimeMs = 0D;

        for (int i = 0; i < spectra.length; i++) {
            // sampleWindow is input to FFT -- may be zero-padded if numPoints > windowSize
            final int[] sampleWindow = windowExtractor.extractWindow(wave, i);

            // compute next current FFT frame
            spectra[i] = FFTComputationWrapper.createSpectrum(sampleWindow, currentAudioTimeMs, result.getWindowDurationMs(),
                    result.getFileDurationMs(), sampleRate, fftConfig);

            // adjust current audio time
            currentAudioTimeMs += result.getWindowDurationMs() * (1 - fftConfig.getWindowOverlap());
        }

        if (fftConfig.isNormalized()) {
            final Frequency maxAmplitude = FFTMath.findMaxAmplitude(spectra);
            FFTMath.normalize(spectra, maxAmplitude.getAmplitude());
        }

        if (fftConfig.isDecibelScale()) {
            FFTMath.scaleLogarithmically(spectra);
        }

        return result;
    }

    /**
     * Creates a Stream which can be used as an iterator to compute {@link Spectrum}s one by one.
     */
    public static SpectrumStream createStream(final Path path) throws UnsupportedAudioFileException, IOException {
        return createStream(AudioReaderFactory.of(path), new FFTConfig());
    }

    /**
     * Creates a Stream which can be used as an iterator to compute {@link Spectrum}s one by one.
     */
    public static SpectrumStream createStream(final Path path, final FFTConfig fftConfig) throws UnsupportedAudioFileException, IOException {
        return createStream(AudioReaderFactory.of(path), fftConfig);
    }

    /**
     * Creates a Stream which can be used as an iterator to compute {@link Spectrum}s one by one.
     */
    public static SpectrumStream createStream(final AudioReader audioReader, final FFTConfig fftConfig) {
        ConfigValidator.validate(fftConfig, true);

        return new SpectrumStream(audioReader, fftConfig);
    }

    private FFTFactory() {
        super();
    }
}
