// Created: 08 Aug. 2024
package de.freese.player.fft;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;
import de.freese.player.fft.sampling.WindowFunction;

/**
 * @author Thomas Freese
 */
class TestWindowGeneration {
    // Expected window values from MATLAB
    private static final double[] BARTLETT_14 = {0, 0.154, 0.308, 0.462, 0.615, 0.769, 0.923, 0.923, 0.769, 0.615, 0.462, 0.308, 0.154, 0};
    private static final double[] BARTLETT_15 = {0, 0.143, 0.286, 0.429, 0.571, 0.714, 0.857, 1.0, 0.857, 0.714, 0.571, 0.429, 0.286, 0.143, 0};
    private static final double[] BLACKMAN_14 = {0, 0.023, 0.108, 0.282, 0.537, 0.804, 0.976, 0.976, 0.804, 0.537, 0.282, 0.108, 0.023, 0};
    private static final double[] BLACKMAN_15 = {0, 0.019, 0.090, 0.237, 0.459, 0.714, 0.920, 1.0, 0.920, 0.714, 0.459, 0.237, 0.090, 0.019, 0};
    private static final double[] HAMMING_14 = {0.080, 0.133, 0.279, 0.485, 0.703, 0.884, 0.987, 0.987, 0.884, 0.703, 0.485, 0.279, 0.133, 0.080};
    private static final double[] HAMMING_15 = {0.080, 0.126, 0.253, 0.438, 0.642, 0.827, 0.954, 1.0, 0.954, 0.827, 0.642, 0.438, 0.253, 0.126, 0.080};
    private static final double[] HANN_14 = {0, 0.057, 0.216, 0.440, 0.677, 0.874, 0.985, 0.985, 0.874, 0.677, 0.440, 0.216, 0.057, 0};
    private static final double[] HANN_15 = {0, 0.050, 0.188, 0.389, 0.611, 0.812, 0.950, 1.0, 0.950, 0.812, 0.611, 0.389, 0.188, 0.050, 0};
    private static final double[] TRIANG_14 = {0.071, 0.214, 0.357, 0.500, 0.643, 0.786, 0.929, 0.929, 0.786, 0.643, 0.500, 0.357, 0.214, 0.071};
    private static final double[] TRIANG_15 = {0.125, 0.250, 0.375, 0.500, 0.625, 0.750, 0.875, 1.0, 0.875, 0.750, 0.625, 0.500, 0.375, 0.250, 0.125};

    @Test
    void testBartlett() {
        double[] window = WindowFunction.BARTLETT.generateWindow(14);
        assertArrayEquals(BARTLETT_14, window, 0.001D);

        window = WindowFunction.BARTLETT.generateWindow(15);
        assertArrayEquals(BARTLETT_15, window, 0.001D);
    }

    @Test
    void testBlackman() {
        double[] window = WindowFunction.BLACKMAN.generateWindow(14);
        assertArrayEquals(BLACKMAN_14, window, 0.001D);

        window = WindowFunction.BLACKMAN.generateWindow(15);
        assertArrayEquals(BLACKMAN_15, window, 0.001D);
    }

    @Test
    void testHamming() {
        double[] window = WindowFunction.HAMMING.generateWindow(14);
        assertArrayEquals(HAMMING_14, window, 0.001D);

        window = WindowFunction.HAMMING.generateWindow(15);
        assertArrayEquals(HAMMING_15, window, 0.001D);
    }

    @Test
    void testHanning() {
        double[] window = WindowFunction.HANNING.generateWindow(14);
        assertArrayEquals(HANN_14, window, 0.001D);

        window = WindowFunction.HANNING.generateWindow(15);
        assertArrayEquals(HANN_15, window, 0.001D);
    }

    @Test
    void testTriangular() {
        double[] window = WindowFunction.TRIANGULAR.generateWindow(14);
        assertArrayEquals(TRIANG_14, window, 0.001D);

        window = WindowFunction.TRIANGULAR.generateWindow(15);
        assertArrayEquals(TRIANG_15, window, 0.001D);
    }
}
