// Created: 09 Aug. 2024
package de.freese.player.fft.math;

import java.util.Arrays;
import java.util.Comparator;

import de.freese.player.fft.output.Frequency;
import de.freese.player.fft.output.Spectrum;

/**
 * @author Thomas Freese
 */
public final class FFTMath {
    /**
     * dB is a measure that compares an intensity (amplitude) to some reference intensity.<br>
     * His reference intensity should be the maximum possible intensity for any sample in the entire signal.<br>
     * For 16-bit signed audio, this intensity is 32_768.
     */
    private static final int MAX_AMPLITUDE_INTENSITY = 32768;

    public static Frequency findMaxAmplitude(final Spectrum spectrum) {
        Frequency max = null;
        double maxAmp = -100D;

        for (Frequency frequency : spectrum) {
            if (frequency.getAmplitude() > maxAmp) {
                maxAmp = Math.max(maxAmp, frequency.getAmplitude());
                max = frequency;
            }
        }

        return max;
    }

    public static Frequency findMaxAmplitude(final Spectrum[] spectra) {
        // Frequency max = null;
        // double maxAmp = -100D;
        //
        // for (Spectrum spectrum : spectra) {
        //     final Frequency frequency = findMaxAmplitude(spectrum);
        //
        //     if (frequency != null && frequency.getAmplitude() > maxAmp) {
        //         maxAmp = Math.max(maxAmp, frequency.getAmplitude());
        //         max = frequency;
        //     }
        // }
        //
        // return max;

        return Arrays.stream(spectra)
                .parallel()
                .flatMap(Spectrum::stream)
                .max(Comparator.comparing(Frequency::getAmplitude))
                .orElse(null);
    }

    public static void normalize(final Spectrum spectrum, final double maxAmp) {
        for (Frequency frequency : spectrum) {
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
                .flatMap(Spectrum::stream)
                .forEach(frequency -> frequency.setAmplitude(frequency.getAmplitude() / maxAmp));
    }

    /**
     * Converts amplitudes contents of a single FFT frame to a decibel (dB) scale.
     */
    public static void scaleLogarithmically(final Spectrum spectrum) {
        for (Frequency frequency : spectrum) {
            frequency.setAmplitude(10D * Math.log10(frequency.getAmplitude() / MAX_AMPLITUDE_INTENSITY));

            // establish -100 dB floor (avoid infinitely negative values)
            frequency.setAmplitude(Math.max(frequency.getAmplitude(), -100D));
        }
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
                .flatMap(Spectrum::stream)
                .forEach(frequency -> {
                    frequency.setAmplitude(10D * Math.log10(frequency.getAmplitude() / MAX_AMPLITUDE_INTENSITY));

                    // establish -100 dB floor (avoid infinitely negative values)
                    frequency.setAmplitude(Math.max(frequency.getAmplitude(), -100D));
                });
    }

    private FFTMath() {
        super();
    }
}
