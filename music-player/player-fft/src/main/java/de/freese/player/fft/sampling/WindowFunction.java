package de.freese.player.fft.sampling;

import java.util.function.BiConsumer;

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

        @Override
        public void generateWindow(final int length, final BiConsumer<Integer, Double> consumer) {
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
        public void generateWindow(final int length, final BiConsumer<Integer, Double> consumer) {
            int i = 0;

            if (length % 2 == 1) {
                for (; i < (length + 1) / 2; i++) {
                    final double coefficient = (2.0D * (i + 1D)) / (length + 1);
                    consumer.accept(i, coefficient);
                }

                for (; i < length; i++) {
                    final double coefficient = 2D - ((2.0D * (i + 1D)) / (length + 1));
                    consumer.accept(i, coefficient);
                }
            }
            else {
                for (; i < (length / 2); i++) {
                    final double coefficient = (2.0D * (i + 1D) - 1D) / length;
                    consumer.accept(i, coefficient);
                }

                for (; i < length; i++) {
                    final double coefficient = 2D - ((2.0D * (i + 1D) - 1D) / length);
                    consumer.accept(i, coefficient);
                }
            }
        }
    },

    /**
     * A Bartlett window (triangular window with zeroes on each end).
     *
     * @see <a href="https://www.mathworks.com/help/signal/ref/bartlett.html">MATLAB reference</a>
     */
    BARTLETT("Bartlett") {
        @Override
        public void generateWindow(final int length, final BiConsumer<Integer, Double> consumer) {
            int i = 0;

            for (; i <= (length - 1) / 2; i++) {
                final double coefficient = (2.0D * i) / (length - 1);

                consumer.accept(i, coefficient);
            }

            for (; i < length; i++) {
                final double coefficient = 2D - (2.0D * i) / (length - 1);

                consumer.accept(i, coefficient);
            }
        }
    },

    /**
     * A Hann (or Hanning) window.
     *
     * @see <a href="https://www.mathworks.com/help/signal/ref/hann.html">MATLAB reference</a>
     */
    HANNING("Hanning") {
        @Override
        public void generateWindow(final int length, final BiConsumer<Integer, Double> consumer) {
            for (int i = 0; i < length; i++) {
                // w[n] = 0.5D * (1D - Math.cos(2D * Math.PI * (i / (length - 1.0D))));
                final double coefficient = 0.5D * (1D - Math.cos(Math.TAU * (i / (length - 1.0D))));

                consumer.accept(i, coefficient);
            }
        }
    },

    /**
     * A Hamming window.
     *
     * @see <a href="https://www.mathworks.com/help/signal/ref/hamming.html">MATLAB reference</a>
     */
    HAMMING("Hamming") {
        @Override
        public void generateWindow(final int length, final BiConsumer<Integer, Double> consumer) {
            for (int i = 0; i < length; i++) {
                // final double coefficient = 0.54D - 0.46D * Math.cos(2D * Math.PI * (i / (length - 1.0D)));
                final double coefficient = 0.54D - 0.46D * Math.cos(Math.TAU * (i / (length - 1.0D)));

                consumer.accept(i, coefficient);
            }
        }
    },

    /**
     * A Blackman window.
     *
     * @see <a href="https://www.mathworks.com/help/signal/ref/blackman.html">MATLAB reference</a>
     */
    BLACKMAN("Blackman") {
        @Override
        public void generateWindow(final int length, final BiConsumer<Integer, Double> consumer) {
            for (int i = 0; i < length; i++) {
                // final double coefficient = 0.42D - 0.5D * Math.cos((2D * Math.PI * i) / (length - 1)) + 0.08D * Math.cos((4D * Math.PI * i) / (length - 1));
                final double coefficient = 0.42D - 0.5D * Math.cos((Math.TAU * i) / (length - 1)) + 0.08D * Math.cos((4D * Math.PI * i) / (length - 1));

                consumer.accept(i, coefficient);
            }
        }
    };

    private final String name;

    WindowFunction(final String name) {
        this.name = name;
    }

    /**
     * Generates coefficients for a window of specified length and type.
     *
     * @param length length of window (should be equal to number of samples taken from waveform)
     *
     * @return coefficients for window of specified length and type
     */
    public double[] generateWindow(final int length) {
        final double[] coefficients = new double[length];

        generateWindow(length, (index, coefficient) -> coefficients[index] = coefficient);

        return coefficients;
    }

    /**
     * Generates coefficients for a window of specified length and type.
     *
     * @param length length of window (should be equal to number of samples taken from waveform)
     * @param consumer with Index and the coefficient
     */
    public abstract void generateWindow(int length, BiConsumer<Integer, Double> consumer);

    @Override
    public String toString() {
        return name;
    }
}
