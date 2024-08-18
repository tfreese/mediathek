package org.quifft.config;

import org.quifft.sampling.WindowFunction;

/**
 * A config object containing the parameters of a Fourier transform.<br>
 * These parameters are used while performing the FFT, and can be accessed
 * as an attribute of the result.
 */
public class FFTConfig {
    /**
     * If true, amplitude of frequencies will be scaled logarithmically (decibels) instead of linearly.<br>
     * The decibel scale describes the amplitude of a sound relative to some reference value.<br>
     * In the case of digital audio, this reference value is the maximum possible amplitude value that can be<br>
     * represented at a given bit depth.  Since each value will be compared to the maximum possible, most or all<br>
     * of the dB readings will be less than 0.  No sound at all is typically represented by negative infinity,<br>
     * but QuiFFT sets a floor of -100 dB to avoid infinite values.<br>
     * Therefore, if a decibel scale is used, amplitudes will be in the range [-100.0, 0.0].
     */
    private boolean decibelScale = true;
    /**
     * If true, all frequencies amplitudes will be in the range from 0.00 to 1.00,
     * where 1.00 represents the maximum frequency amplitude amongst all amplitudes in the file.<br>
     * If {@code useDecibelScale} is set to true, the value of {@code isNormalized} doesn't
     * matter because the decibel scale is normalized by definition.
     */
    private boolean normalized;
    /**
     * Number of points in the N-point FFT.<br>
     * If not defined, will default to the window size.<br>
     * If defined, must be greater than window size and be a power of 2.
     */
    private Integer numPoints;
    /**
     * Window function to be used for obtaining sequence of samples to be used for each FFT frame.<br>
     * One of: rectangular, triangular, Bartlett, Hanning, Hamming, Blackman
     */
    private WindowFunction windowFunction = WindowFunction.HANNING;
    /**
     * Percentage of overlap between adjacent sampled windows; must be between 0 and 1.<br>
     * Large window overlap percentages can dramatically increase the size of the FFT result
     * because more FFT frames are calculated.<br>
     * For example, if 75% overlap is used (windowOverlap = .75), there will be 4 times as many
     * FFT frames computed than there would be with no overlap.
     */
    private double windowOverlap = 0.50D;
    /**
     * Number of samples taken from audio waveform for use in FFT.<br>
     * If numPoints is not defined, this must be a power of 2.<br>
     * If numPoints is defined to be greater than window size, the signal will be
     * padded with (numPoints - windowSize) zeroes.
     */
    private int windowSize = 4096;

    public FFTConfig decibelScale(final boolean decibelScale) {
        this.decibelScale = decibelScale;

        return this;
    }

    public Integer getNumPoints() {
        return numPoints;
    }

    public WindowFunction getWindowFunction() {
        return windowFunction;
    }

    public double getWindowOverlap() {
        return windowOverlap;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public boolean isDecibelScale() {
        return decibelScale;
    }

    public boolean isNormalized() {
        return normalized;
    }

    public FFTConfig normalized(final boolean normalized) {
        this.normalized = normalized;

        return this;
    }

    public FFTConfig numPoints(final Integer numPoints) {
        this.numPoints = numPoints;

        return this;
    }

    /**
     * Get total length of window; returns windowSize by default, but will return numPoints of it is set.
     *
     * @return total length of sampling window (including zero-padding if applied)
     */
    public int totalWindowLength() {
        if (numPoints == null) {
            return windowSize;
        }

        return numPoints;
    }

    public FFTConfig windowFunction(final WindowFunction windowFunction) {
        this.windowFunction = windowFunction;

        return this;
    }

    public FFTConfig windowOverlap(final double windowOverlap) {
        this.windowOverlap = windowOverlap;

        return this;
    }

    public FFTConfig windowSize(final int windowSize) {
        this.windowSize = windowSize;

        return this;
    }

    /**
     * Get zero padding length (# of zeroes that should be appended to input signal before taking FFT)
     * based on numPoints and windowSize parameters.
     *
     * @return zero padding length for FFT
     */
    public int zeroPadLength() {
        if (numPoints == null) {
            return 0;
        }

        return numPoints - windowSize;
    }
}
