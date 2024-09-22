// Created: 08 Aug. 2024
package de.freese.player.equalizer.iir;

/**
 * N-Order IIR Filter Assumes inputs are normalized to [-1, 1]<br>
 *
 * Based on the difference equation from
 * <a href="https://en.wikipedia.org/wiki/Infinite_impulse_response">Wikipedia link</a>
 */
public final class IIRFilter {

    private final double[] coeffsA;
    private final double[] coeffsB;
    private final double[] historyX;
    private final double[] historyY;
    private final int order;

    /**
     * Construct an IIR Filter
     *
     * @param order the filter's order (length)
     *
     * @throws IllegalArgumentException if order is zero or less
     */
    public IIRFilter(final int order) throws IllegalArgumentException {
        super();

        if (order < 1) {
            throw new IllegalArgumentException("order must be greater than zero");
        }

        this.order = order;
        coeffsA = new double[order + 1];
        coeffsB = new double[order + 1];

        coeffsA[0] = 1.0D;
        coeffsB[0] = 1.0D;

        historyX = new double[order];
        historyY = new double[order];
    }

    /**
     * Process a single sample
     */
    public double process(final double sample) {
        double result = 0.0D;

        for (int i = 1; i <= order; i++) {
            result += (coeffsB[i] * historyX[i - 1] - coeffsA[i] * historyY[i - 1]);
        }

        result = (result + coeffsB[0] * sample) / coeffsA[0];

        // Feedback
        for (int i = order - 1; i > 0; i--) {
            historyX[i] = historyX[i - 1];
            historyY[i] = historyY[i - 1];
        }

        historyX[0] = sample;
        historyY[0] = result;

        return result;
    }

    /**
     * Set coefficients
     *
     * @param aCoeffs Denominator coefficients
     * @param bCoeffs Numerator coefficients
     *
     * @throws IllegalArgumentException if {@code aCoeffs} or {@code bCoeffs} is not of size {@code order}, or if {@code aCoeffs[0]} is 0.0
     */
    public void setCoeffs(final double[] aCoeffs, final double[] bCoeffs) throws IllegalArgumentException {
        if (aCoeffs.length != order) {
            throw new IllegalArgumentException("aCoeffs must be of size " + order + ", got " + aCoeffs.length);
        }

        if (Double.compare(aCoeffs[0], 0.0D) == 0) {
            throw new IllegalArgumentException("aCoeffs.get(0) must not be zero");
        }

        if (bCoeffs.length != order) {
            throw new IllegalArgumentException("bCoeffs must be of size " + order + ", got " + bCoeffs.length);
        }

        for (int i = 0; i <= order; i++) {
            coeffsA[i] = aCoeffs[i];
            coeffsB[i] = bCoeffs[i];
        }
    }
}
