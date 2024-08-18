package org.quifft.fft;

/**
 * Compute the FFT of a length n complex sequence in-place.<br>
 * Uses a non-recursive version of the Cooley-Tukey FFT.<br>
 * Runs in O(n log n) time.<br>
 * <br>
 * Reference:  Algorithm 1.6.1 in Computational Frameworks for the Fast Fourier Transform by Charles Van Loan.<br>
 * <br>
 * Limitations<br>
 * -----------<br>
 * -  assumes n is a power of 2<br>
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public final class InplaceFFT {
    /**
     * compute the FFT of x[], assuming its length is a power of 2
     */
    public static void fft(final Complex[] x) {
        // assume length is a power of 2
        final int n = x.length;

        // bit reversal permutation
        final int shift = 1 + Integer.numberOfLeadingZeros(n);

        for (int k = 0; k < n; k++) {
            final int j = Integer.reverse(k) >>> shift;

            if (j > k) {
                final Complex temp = x[j];
                x[j] = x[k];
                x[k] = temp;
            }
        }

        // butterfly updates
        for (int l = 2; l <= n; l = l + l) {
            for (int k = 0; k < l / 2; k++) {
                final double kth = -2 * k * Math.PI / l;
                final Complex w = new Complex(Math.cos(kth), Math.sin(kth));

                for (int j = 0; j < n / l; j++) {
                    final Complex tao = w.times(x[j * l + k + l / 2]);
                    x[j * l + k + l / 2] = x[j * l + k].minus(tao);
                    x[j * l + k] = x[j * l + k].plus(tao);
                }
            }
        }
    }

    private InplaceFFT() {
        super();
    }
}
