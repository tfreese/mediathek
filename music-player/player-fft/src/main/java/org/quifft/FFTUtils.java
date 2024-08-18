// Created: 09 Aug. 2024
package org.quifft;

import java.util.Arrays;

import org.quifft.output.FFTFrame;
import org.quifft.output.Frequency;

/**
 * @author Thomas Freese
 */
public final class FFTUtils {
    /**
     * dB is a measure that compares an intensity (amplitude) to some reference intensity.<br>
     * His reference intensity should be the maximum possible intensity for any sample in the entire signal.<br>
     * For 16-bit signed audio, this intensity is 32768.
     */
    private static final int MAX_AMPLITUDE_INTENSITY = 32768;

    public static double findFrequencyWithHighestAmplitude(final FFTFrame fftFrame) {
        double maxAmplitude = -100D;
        double maxFrequency = 0D;

        for (Frequency frequency : fftFrame.getFrequencies()) {
            if (frequency.getAmplitude() > maxAmplitude) {
                maxAmplitude = frequency.getAmplitude();
                maxFrequency = frequency.getFrequency();
            }
        }

        return maxFrequency;
    }

    public static double findMaxAmplitude(final FFTFrame fftFrame) {
        double maxAmp = 0D;

        for (Frequency frequency : fftFrame.getFrequencies()) {
            maxAmp = Math.max(maxAmp, frequency.getAmplitude());
        }

        return maxAmp;
    }

    public static double findMaxAmplitude(final FFTFrame[] fftFrames) {
        // double maxAmp = 0D;
        //
        // for (FFTFrame fftFrame : fftFrames) {
        //     maxAmp = Math.max(maxAmp, findMaxAmplitude(fftFrame));
        // }
        //
        // return maxAmp;

        return Arrays.stream(fftFrames)
                .parallel()
                .flatMap(frame -> Arrays.stream(frame.getFrequencies()))
                .mapToDouble(Frequency::getAmplitude)
                .max()
                .orElse(0D);
    }

    public static void normalizeFFTResult(final FFTFrame fftFrame, final double maxAmp) {
        for (Frequency frequency : fftFrame.getFrequencies()) {
            frequency.setAmplitude(frequency.getAmplitude() / maxAmp);
        }
    }

    /**
     * Normalizes each amplitude by dividing all amplitudes by the max amplitude.
     */
    public static void normalizeFFTResult(final FFTFrame[] fftFrames, final double maxAmp) {
        // for (FFTFrame fftFrame : fftFrames) {
        //     normalizeFFTResult(fftFrame, maxAmp);
        // }

        Arrays.stream(fftFrames)
                .parallel()
                .flatMap(fftFrame -> Arrays.stream(fftFrame.getFrequencies()))
                .forEach(frequency -> frequency.setAmplitude(frequency.getAmplitude() / maxAmp));
    }

    /**
     * Converts amplitudes contents of a single FFT frame to a decibel (dB) scale.
     */
    public static void scaleLogarithmically(final FFTFrame fftFrame) {
        for (Frequency frequency : fftFrame.getFrequencies()) {
            frequency.setAmplitude(10D * Math.log10(frequency.getAmplitude() / MAX_AMPLITUDE_INTENSITY));

            // establish -100 dB floor (avoid infinitely negative values)
            frequency.setAmplitude(Math.max(frequency.getAmplitude(), -100D));
        }
    }

    /**
     * Converts amplitudes contents of FFT frames to a decibel (dB) scale.
     */
    public static void scaleLogarithmically(final FFTFrame[] fftFrames) {
        // for (FFTFrame frame : fftFrames) {
        //     scaleLogarithmically(frame);
        // }

        Arrays.stream(fftFrames)
                .parallel()
                .flatMap(fftFrame -> Arrays.stream(fftFrame.getFrequencies()))
                .forEach(frequency -> {
                    frequency.setAmplitude(10D * Math.log10(frequency.getAmplitude() / MAX_AMPLITUDE_INTENSITY));

                    // establish -100 dB floor (avoid infinitely negative values)
                    frequency.setAmplitude(Math.max(frequency.getAmplitude(), -100D));
                });
    }

    private FFTUtils() {
        super();
    }
}
