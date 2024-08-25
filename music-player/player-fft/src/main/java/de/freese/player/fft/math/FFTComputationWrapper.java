package de.freese.player.fft.math;

import de.freese.player.fft.config.FFTConfig;
import de.freese.player.fft.output.Frequency;
import de.freese.player.fft.output.Spectrum;

/**
 * Uses Princeton FFT Implementation to compute {@link Spectrum}s.
 *
 * @see FFT
 */
public final class FFTComputationWrapper {
    /**
     * Computes an FFT for a windowed time domain signal.
     *
     * @param wave sampled values from audio waveform
     * @param audioSampleRate sample rate of audio file
     * @param fftConfig parameters used for this FFT
     *
     * @return a single Spectrum that is the result of an FFT being computed on wave with given parameters
     */
    public static Spectrum createSpectrum(final int[] wave,
                                          final float audioSampleRate,
                                          final FFTConfig fftConfig) {
        final Frequency[] frequencies = doFFT(wave, audioSampleRate, fftConfig);

        return new Spectrum(frequencies, 0, 0);
    }

    /**
     * Computes an FFT for a windowed time domain signal.
     *
     * @param wave sampled values from audio waveform
     * @param startTimeMs timestamp in the original audio file at which this sample window begins
     * @param windowDurationMs duration of sample window in milliseconds
     * @param fileDurationMs duration of entire audio file in milliseconds
     * @param audioSampleRate sample rate of audio file
     * @param fftConfig parameters used for this FFT
     *
     * @return a single Spectrum that is the result of an FFT being computed on wave with given parameters
     */
    public static Spectrum createSpectrum(final int[] wave,
                                          final double startTimeMs,
                                          final double windowDurationMs,
                                          final double fileDurationMs,
                                          final float audioSampleRate,
                                          final FFTConfig fftConfig) {
        final Frequency[] frequencies = doFFT(wave, audioSampleRate, fftConfig);

        // Last window(s) will probably be partial.
        final double endTimeMs = Math.min(fileDurationMs, startTimeMs + windowDurationMs);

        return new Spectrum(frequencies, startTimeMs, endTimeMs);
    }

    /**
     * Computes an FFT for a windowed time domain signal.
     *
     * @param wave sampled values from audio waveform
     * @param audioSampleRate sample rate of audio file
     * @param fftConfig parameters used for this FFT
     *
     * @return a single Spectrum that is the result of an FFT being computed on wave with given parameters
     */
    private static Frequency[] doFFT(final int[] wave,
                                     final float audioSampleRate,
                                     final FFTConfig fftConfig) {
        // final Complex[] complexWave = FFT.fftRecursive(Complex.toComplex(wave));
        final Complex[] complexWave = Complex.toComplex(wave);
        FFT.fft(complexWave);

        final double frequencyAxisIncrement = audioSampleRate / (double) wave.length;

        // Copy first half of FFT results into a list of frequencies.
        // FFT is symmetrical so any information after the halfway point is redundant.
        final Frequency[] frequencies = new Frequency[complexWave.length / 2];

        for (int i = 0; i < frequencies.length; i++) {
            final double scaledAmplitude = 2D * complexWave[i].abs() / fftConfig.totalWindowLength();
            frequencies[i] = new Frequency(i * frequencyAxisIncrement, scaledAmplitude);
        }

        return frequencies;
    }

    private FFTComputationWrapper() {
        super();
    }
}
