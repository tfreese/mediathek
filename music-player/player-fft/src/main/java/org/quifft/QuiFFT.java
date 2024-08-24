package org.quifft;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.quifft.config.ConfigValidator;
import org.quifft.config.FFTConfig;
import org.quifft.fft.FFTComputationWrapper;
import org.quifft.output.FFTStream;
import org.quifft.output.SpectraResult;
import org.quifft.output.Spectrum;
import org.quifft.reader.AudioReader;
import org.quifft.reader.AudioReaderFactory;
import org.quifft.sampling.SampleWindowExtractor;

/**
 * Class used by the client to compute an FFT for an audio file
 */
public final class QuiFFT {
    private final AudioReader audioReader;
    private final FFTConfig fftConfig;

    public QuiFFT(final Path inputFile) throws IOException, UnsupportedAudioFileException {
        this(inputFile, new FFTConfig());
    }

    public QuiFFT(final Path inputFile, final FFTConfig fftConfig) throws IOException, UnsupportedAudioFileException {
        this(AudioReaderFactory.of(inputFile), fftConfig);
    }

    public QuiFFT(final AudioReader audioReader, final FFTConfig fftConfig) {
        super();

        this.audioReader = Objects.requireNonNull(audioReader, "audioReader required");
        this.fftConfig = Objects.requireNonNull(fftConfig, "fftConfig required");
    }

    /**
     * Creates an FFTStream which can be used as an iterator to compute FFT frames one by one
     */
    public FFTStream fftStream() {
        ConfigValidator.validate(fftConfig, true);

        final FFTStream fftStream = new FFTStream();
        fftStream.setMetadata(audioReader, fftConfig);

        return fftStream;
    }

    /**
     * Performs an FFT for the entirety of the audio file
     *
     * @return an FFT result containing metadata of this FFT and an array of all {@link Spectrum}s computed
     */
    public SpectraResult fullFFT() {
        ConfigValidator.validate(fftConfig, false);

        final SpectraResult result = new SpectraResult();
        result.setMetadata(audioReader, fftConfig);

        final boolean isStereo = audioReader.isStereo();
        final float sampleRate = audioReader.getAudioFormat().getSampleRate();
        final int[] wave = audioReader.getWaveform();

        final int lengthOfWave = wave.length / (isStereo ? 2 : 1);
        final double frameOverlapMultiplier = 1D / (1D - fftConfig.getWindowOverlap());
        final int numFrames = (int) Math.ceil(((double) lengthOfWave / fftConfig.getWindowSize()) * frameOverlapMultiplier);
        final Spectrum[] spectra = new Spectrum[numFrames];

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
            final double maxAmplitude = FFTUtils.findMaxAmplitude(spectra);
            FFTUtils.normalize(spectra, maxAmplitude);
        }

        if (fftConfig.isDecibelScale()) {
            FFTUtils.scaleLogarithmically(spectra);
        }

        result.setSpectra(spectra);

        return result;
    }

    public FFTConfig getFFTConfig() {
        return fftConfig;
    }
}
