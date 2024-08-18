package org.quifft;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.quifft.config.ConfigValidator;
import org.quifft.config.FFTConfig;
import org.quifft.fft.FFTComputationWrapper;
import org.quifft.output.FFTFrame;
import org.quifft.output.FFTResult;
import org.quifft.output.FFTStream;
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
     * @return an FFT result containing metadata of this FFT and an array of all {@link FFTFrame}s computed
     */
    public FFTResult fullFFT() {
        ConfigValidator.validate(fftConfig, false);

        final FFTResult fftResult = new FFTResult();
        fftResult.setMetadata(audioReader, fftConfig);

        final boolean isStereo = audioReader.isStereo();
        final float sampleRate = audioReader.getAudioFormat().getSampleRate();
        final int[] wave = audioReader.getWaveform();

        final int lengthOfWave = wave.length / (isStereo ? 2 : 1);
        final double frameOverlapMultiplier = 1D / (1D - fftConfig.getWindowOverlap());
        final int numFrames = (int) Math.ceil(((double) lengthOfWave / fftConfig.getWindowSize()) * frameOverlapMultiplier);
        final FFTFrame[] fftFrames = new FFTFrame[numFrames];

        final SampleWindowExtractor windowExtractor = new SampleWindowExtractor(isStereo, fftConfig.getWindowSize(),
                fftConfig.getWindowFunction(), fftConfig.getWindowOverlap(), fftConfig.zeroPadLength());
        double currentAudioTimeMs = 0D;

        for (int i = 0; i < fftFrames.length; i++) {
            // sampleWindow is input to FFT -- may be zero-padded if numPoints > windowSize
            final int[] sampleWindow = windowExtractor.extractWindow(wave, i);

            // compute next current FFT frame
            fftFrames[i] = FFTComputationWrapper.doFFT(sampleWindow, currentAudioTimeMs, fftResult.getWindowDurationMs(),
                    fftResult.getFileDurationMs(), sampleRate, fftConfig);

            // adjust current audio time
            currentAudioTimeMs += fftResult.getWindowDurationMs() * (1 - fftConfig.getWindowOverlap());
        }

        if (fftConfig.isNormalized()) {
            final double maxAmplitude = FFTUtils.findMaxAmplitude(fftFrames);
            FFTUtils.normalizeFFTResult(fftFrames, maxAmplitude);
        }

        if (fftConfig.isDecibelScale()) {
            FFTUtils.scaleLogarithmically(fftFrames);
        }

        fftResult.setFftFrames(fftFrames);

        return fftResult;
    }

    public FFTConfig getFFTConfig() {
        return fftConfig;
    }
}
