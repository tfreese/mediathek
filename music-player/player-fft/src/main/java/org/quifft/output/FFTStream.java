package org.quifft.output;

import java.util.Iterator;

import org.quifft.FFTUtils;
import org.quifft.config.FFTConfig;
import org.quifft.fft.FFTComputationWrapper;
import org.quifft.reader.AbstractAudioReader;
import org.quifft.reader.AudioReader;
import org.quifft.sampling.SampleWindowExtractor;
import org.quifft.sampling.WindowFunction;

/**
 * FFTStream computes an FFT on an audio file incrementally as opposed to all at once.<br>
 * It exposes an Iterator interface for computing {@link Spectrum}s one at a time.<br>
 * This can be a useful alternative to {@link SpectraResult} if your audio file is large, or you are space-constrained.
 */
public class FFTStream extends AbstractFFTObject implements Iterator<Spectrum> {

    private AudioReader audioReader;

    private int frameCount;

    private double maxAmplitude;

    private SampleWindowExtractor windowExtractor;

    @Override
    public boolean hasNext() {
        return audioReader.hasNext();
    }

    @Override
    public Spectrum next() {
        int[] nextWindow = audioReader.next();

        final double startTimeMs = frameCount * getWindowDurationMs() * (1D - getFFTConfig().getWindowOverlap());
        final float sampleRate = audioReader.getAudioFormat().getSampleRate();

        nextWindow = windowExtractor.convertSamplesToWindow(nextWindow);

        frameCount++;

        final Spectrum nextSpectrum = FFTComputationWrapper.createSpectrum(nextWindow, startTimeMs, getWindowDurationMs(), getFileDurationMs(), sampleRate, getFFTConfig());

        if (getFFTConfig().isNormalized()) {
            maxAmplitude = Math.max(maxAmplitude, FFTUtils.findMaxAmplitude(nextSpectrum));
            FFTUtils.normalize(nextSpectrum, maxAmplitude);
        }

        if (getFFTConfig().isDecibelScale()) {
            FFTUtils.scaleLogarithmically(nextSpectrum);
        }

        return nextSpectrum;
    }

    @Override
    public void setMetadata(final AudioReader audioReader, final FFTConfig config) {
        super.setMetadata(audioReader, config);

        this.audioReader = audioReader;

        if (audioReader instanceof AbstractAudioReader ar) {
            ar.setFFTConfig(config);
        }

        final int windowSize = getFFTConfig().getWindowSize();
        final WindowFunction windowFunction = getFFTConfig().getWindowFunction();
        final double overlap = getFFTConfig().getWindowOverlap();
        final int zeroPadLength = getFFTConfig().zeroPadLength();

        windowExtractor = new SampleWindowExtractor(audioReader.isStereo(), windowSize, windowFunction, overlap, zeroPadLength);
    }
}
