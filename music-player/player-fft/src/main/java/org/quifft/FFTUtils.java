// Created: 09 Aug. 2024
package org.quifft;

import java.util.Arrays;

import org.quifft.output.Frequency;
import org.quifft.output.Spectrum;

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

    public static double findFrequencyWithHighestAmplitude(final Spectrum spectrum) {
        double maxAmplitude = -100D;
        double maxFrequency = 0D;

        for (Frequency frequency : spectrum.getFrequencies()) {
            if (frequency.getAmplitude() > maxAmplitude) {
                maxAmplitude = frequency.getAmplitude();
                maxFrequency = frequency.getFrequency();
            }
        }

        return maxFrequency;
    }

    public static double findMaxAmplitude(final Spectrum spectrum) {
        double maxAmp = 0D;

        for (Frequency frequency : spectrum.getFrequencies()) {
            maxAmp = Math.max(maxAmp, frequency.getAmplitude());
        }

        return maxAmp;
    }

    public static double findMaxAmplitude(final Spectrum[] spectra) {
        // double maxAmp = 0D;
        //
        // for (Spectrum spectrum : spectra) {
        //     maxAmp = Math.max(maxAmp, findMaxAmplitude(fftFrame));
        // }
        //
        // return maxAmp;

        return Arrays.stream(spectra)
                .parallel()
                .flatMap(Spectrum::asStream)
                .mapToDouble(Frequency::getAmplitude)
                .max()
                .orElse(0D);
    }

    public static void normalize(final Spectrum spectrum, final double maxAmp) {
        for (Frequency frequency : spectrum.getFrequencies()) {
            frequency.setAmplitude(frequency.getAmplitude() / maxAmp);
        }
    }

    /**
     * Normalizes each amplitude by dividing all amplitudes by the max amplitude.
     */
    public static void normalize(final Spectrum[] spectra, final double maxAmp) {
        // for (Spectrum spectrum : spectra) {
        //     normalize(spectrum, maxAmp);
        // }

        Arrays.stream(spectra)
                .parallel()
                .flatMap(Spectrum::asStream)
                .forEach(frequency -> frequency.setAmplitude(frequency.getAmplitude() / maxAmp));
    }

    /**
     * Converts amplitudes contents of a single FFT frame to a decibel (dB) scale.
     */
    public static void scaleLogarithmically(final Spectrum spectrum) {
        spectrum.forEach(frequency -> {
            frequency.setAmplitude(10D * Math.log10(frequency.getAmplitude() / MAX_AMPLITUDE_INTENSITY));

            // establish -100 dB floor (avoid infinitely negative values)
            frequency.setAmplitude(Math.max(frequency.getAmplitude(), -100D));
        });
    }

    /**
     * Converts amplitudes contents of FFT frames to a decibel (dB) scale.
     */
    public static void scaleLogarithmically(final Spectrum[] spectra) {
        // for (Spectrum spectrum : spectra) {
        //     scaleLogarithmically(spectrum);
        // }

        Arrays.stream(spectra)
                .parallel()
                .flatMap(Spectrum::asStream)
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
