package de.freese.player.fft.math;

public final class FFT {
    /**
     * Compute the FFT of a complex sequence in-place.<br>
     * Uses a non-recursive version of the Cooley-Tukey FFT.<br>
     * Runs in O(n log n) time.<br>
     * <br>
     * Reference:  Algorithm 1.6.1 in Computational Frameworks for the Fast Fourier Transform by Charles Van Loan.<br>
     * <br>
     */
    public static void fft(final Complex[] x) {
        final int length = x.length;

        // radix 2 Cooley-Tukey FFT
        if (length % 2 != 0) {
            throw new RuntimeException("length is not a power of 2");
        }

        // bit reversal permutation
        final int shift = 1 + Integer.numberOfLeadingZeros(length);

        for (int k = 0; k < length; k++) {
            final int j = Integer.reverse(k) >>> shift;

            if (j > k) {
                final Complex temp = x[j];
                x[j] = x[k];
                x[k] = temp;
            }
        }

        // butterfly updates
        for (int l = 2; l <= length; l = l + l) {
            for (int k = 0; k < l / 2; k++) {
                final double kth = -2 * k * Math.PI / l;
                final Complex w = new Complex(Math.cos(kth), Math.sin(kth));

                for (int j = 0; j < length / l; j++) {
                    final Complex tao = w.times(x[j * l + k + l / 2]);
                    x[j * l + k + l / 2] = x[j * l + k].minus(tao);
                    x[j * l + k] = x[j * l + k].plus(tao);
                }
            }
        }
    }

    public static Complex[] fftRecursive(final Complex[] x) {
        final int length = x.length;

        // base case
        if (length == 1) {
            return new Complex[]{x[0]};
        }

        // radix 2 Cooley-Tukey FFT
        if (length % 2 != 0) {
            throw new RuntimeException("length is not a power of 2");
        }

        // fft of even terms
        final Complex[] even = new Complex[length / 2];

        for (int k = 0; k < length / 2; k++) {
            even[k] = x[2 * k];
        }

        final Complex[] q = fftRecursive(even);

        // fft of odd terms
        final Complex[] odd = even; // reuse the array

        for (int k = 0; k < length / 2; k++) {
            odd[k] = x[2 * k + 1];
        }

        final Complex[] r = fftRecursive(odd);

        // combine
        final Complex[] y = new Complex[length];

        for (int k = 0; k < length / 2; k++) {
            final double kth = -2 * k * Math.PI / length;
            final Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k] = q[k].plus(wk.times(r[k]));
            y[k + length / 2] = q[k].minus(wk.times(r[k]));
        }

        return y;
    }

    /**
     * Returns the inverse FFT of the specified complex array.
     *
     * @return the inverse FFT of the complex array {@code x}
     *
     * @throws IllegalArgumentException if the length of {@code x} is not a power of 2
     */
    public static Complex[] ifft(final Complex[] x) {
        final int n = x.length;
        final Complex[] y = new Complex[n];

        // take conjugate
        for (int i = 0; i < n; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        fft(y);

        // take conjugate again
        for (int i = 0; i < n; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by n
        for (int i = 0; i < n; i++) {
            y[i] = y[i].scale(1.0D / n);
        }

        return y;

    }

    /**
     * Divide by n if we want the inverse FFT.
     */
    public static Complex[] inverseFFT(final int n, final Complex[] x) {
        for (int i = 0; i < x.length; i++) {
            final Complex z = x[i];
            x[i] = z.divide(n);
        }

        return x;
    }

    private FFT() {
        super();
    }
}
