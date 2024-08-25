package de.freese.player.fft.sampling;

/**
 * The type of window function to be applied to each section of waveform before computing its FFT
 */
public enum WindowFunction {
    /**
     * Extracts blocks of data from waveform without performing any transformation.
     */
    RECTANGULAR("Rectangular") {
        @Override
        public double[] generateWindow(final int length) {
            throw new UnsupportedOperationException("windowing for RECTANGULAR is not supported");
        }
    },

    /**
     * A triangular window.
     *
     * @see <a href="https://www.mathworks.com/help/signal/ref/triang.html">MATLAB reference</a>
     */
    TRIANGULAR("Triangular") {
        @Override
        public double[] generateWindow(final int length) {
            final double[] w = new double[length];

            int n = 0;

            if (length % 2 == 1) {
                for (; n < (length + 1) / 2; n++) {
                    w[n] = (2.0D * (n + 1)) / (length + 1);
                }

                for (; n < length; n++) {
                    w[n] = 2D - ((2.0D * (n + 1)) / (length + 1));
                }
            }
            else {
                for (; n < (length / 2); n++) {
                    w[n] = (2.0D * (n + 1) - 1) / length;
                }

                for (; n < length; n++) {
                    w[n] = 2D - ((2.0D * (n + 1) - 1) / length);
                }
            }

            return w;
        }
    },

    /**
     * A Bartlett window (triangular window with zeroes on each end).
     *
     * @see <a href="https://www.mathworks.com/help/signal/ref/bartlett.html">MATLAB reference</a>
     */
    BARTLETT("Bartlett") {
        @Override
        public double[] generateWindow(final int length) {
            final double[] w = new double[length];

            int n = 0;

            for (; n <= (length - 1) / 2; n++) {
                w[n] = (2.0 * n) / (length - 1);
            }

            for (; n < length; n++) {
                w[n] = 2 - (2.0 * n) / (length - 1);
            }

            return w;
        }
    },

    /**
     * A Hann (or Hanning) window.
     *
     * @see <a href="https://www.mathworks.com/help/signal/ref/hann.html">MATLAB reference</a>
     */
    HANNING("Hanning") {
        @Override
        public double[] generateWindow(final int length) {
            final double[] w = new double[length];

            for (int n = 0; n < length; n++) {
                // w[n] = 0.5D * (1D - Math.cos(2D * Math.PI * (n / (N - 1.0D))));
                w[n] = 0.5D * (1D - Math.cos(Math.TAU * (n / (length - 1.0D))));
            }

            return w;
        }
    },

    /**
     * A Hamming window.
     *
     * @see <a href="https://www.mathworks.com/help/signal/ref/hamming.html">MATLAB reference</a>
     */
    HAMMING("Hamming") {
        @Override
        public double[] generateWindow(final int length) {
            final double[] w = new double[length];

            for (int n = 0; n < length; n++) {
                // w[n] = 0.54D - 0.46D * Math.cos(2D * Math.PI * (n / (N - 1.0D)));
                w[n] = 0.54D - 0.46D * Math.cos(Math.TAU * (n / (length - 1.0D)));
            }

            return w;
        }
    },

    /**
     * A Blackman window.
     *
     * @see <a href="https://www.mathworks.com/help/signal/ref/blackman.html">MATLAB reference</a>
     */
    BLACKMAN("Blackman") {
        @Override
        public double[] generateWindow(final int length) {
            final double[] w = new double[length];

            for (int n = 0; n < length; n++) {
                // w[n] = 0.42D - 0.5D * Math.cos((2D * Math.PI * n) / (N - 1)) + 0.08D * Math.cos((4D * Math.PI * n) / (N - 1));
                w[n] = 0.42D - 0.5D * Math.cos((Math.TAU * n) / (length - 1)) + 0.08D * Math.cos((4D * Math.PI * n) / (length - 1));
            }

            return w;
        }
    };

    private final String name;

    WindowFunction(final String name) {
        this.name = name;
    }

    /**
     * Generates coefficients for a window of specified length and type
     *
     * @param length length of window (should be equal to number of samples taken from waveform)
     *
     * @return coefficients for window of specified length and type
     */
    public abstract double[] generateWindow(int length);

    @Override
    public String toString() {
        return name;
    }
}
