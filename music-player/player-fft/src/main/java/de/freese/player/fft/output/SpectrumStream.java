package de.freese.player.fft.output;

import java.util.Iterator;

import de.freese.player.fft.config.FFTConfig;
import de.freese.player.fft.math.FFTComputationWrapper;
import de.freese.player.fft.math.FFTMath;
import de.freese.player.fft.reader.AbstractAudioReader;
import de.freese.player.fft.reader.AudioReader;
import de.freese.player.fft.sampling.SampleWindowExtractor;
import de.freese.player.fft.sampling.WindowFunction;

/**
 * SpectrumStream computes an FFT on an audio file incrementally as opposed to all at once.<br>
 * It exposes an Iterator interface for computing {@link Spectrum}s one at a time.<br>
 * This can be a useful alternative to {@link SpectraResult} if your audio file is large, or you are space-constrained.
 */
public final class SpectrumStream extends AbstractFFTObject {

    private final AudioReader audioReader;
    private final SampleWindowExtractor windowExtractor;

    private int frameCount;
    private double maxAmplitude;

    public SpectrumStream(final AudioReader audioReader, final FFTConfig config) {
        super(audioReader, config);

        this.audioReader = audioReader;

        if (audioReader instanceof AbstractAudioReader ar) {
            ar.setFFTConfig(config);
        }

        final int windowSize = config.getWindowSize();
        final WindowFunction windowFunction = config.getWindowFunction();
        final double overlap = config.getWindowOverlap();
        final int zeroPadLength = config.zeroPadLength();

        windowExtractor = new SampleWindowExtractor(audioReader.isStereo(), windowSize, windowFunction, overlap, zeroPadLength);
    }

    @Override
    public Iterator<Spectrum> iterator() {
        return new Iterator<>() {
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
                    maxAmplitude = Math.max(maxAmplitude, FFTMath.findMaxAmplitude(nextSpectrum).getAmplitude());
                    FFTMath.normalize(nextSpectrum, maxAmplitude);
                }

                if (getFFTConfig().isDecibelScale()) {
                    FFTMath.scaleLogarithmically(nextSpectrum);
                }

                return nextSpectrum;
            }
        };
    }
}
