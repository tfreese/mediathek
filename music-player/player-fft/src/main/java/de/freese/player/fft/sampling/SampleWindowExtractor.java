package de.freese.player.fft.sampling;

import de.freese.player.fft.config.FFTConfig;

/**
 * Applies zero-padding and smoothing functions to extract sample windows from a longer waveform.
 *
 * @see FFTConfig
 * @see WindowFunction
 */
public final class SampleWindowExtractor {
    /**
     * delta sample (distance between start indices between consecutive windows)
     */
    private final int ds;
    /**
     * true if signal is stereo (2 channels), false if mono
     */
    private final boolean isStereo;
    /**
     * windowing function to be applied to input signal
     */
    private final WindowFunction windowFunction;
    /**
     * size of window as defined by FFT parameters (excludes zero-padding)
     */
    private final int windowSize;
    /**
     * number of zeroes to be appended to windowed signal
     */
    private final int zeroPadLength;

    /**
     * Constructs a SampleWindowExtractor to take windows from an input signal for use in FFTs
     *
     * @param isStereo true if waveform is stereo, false if mono
     * @param windowSize size of window as defined by FFT parameters (excludes zero-padding)
     * @param windowFunction windowing function to be applied to input signal
     * @param windowOverlap window overlap percentage
     * @param zeroPadLength number of zeroes to be appended to windowed signal
     */
    public SampleWindowExtractor(final boolean isStereo, final int windowSize, final WindowFunction windowFunction,
                                 final double windowOverlap, final int zeroPadLength) {
        super();

        this.isStereo = isStereo;
        this.windowSize = windowSize;
        this.windowFunction = windowFunction;
        this.zeroPadLength = zeroPadLength;
        this.ds = (int) Math.floor(windowSize * (1 - windowOverlap));
    }

    /**
     * Applies zero-padding and the selected smoothing function to a given window; used with SpectrumStream.
     *
     * @param window sampling window to which smoothing function should be applied
     *
     * @return sampling window with smoothing function applied
     */
    public int[] convertSamplesToWindow(final int[] window) {
        final int[] fullWindow = new int[windowSize + zeroPadLength];

        int j = 0;
        int samplesCopied = 0;

        while (samplesCopied < windowSize) {
            if (isStereo) {
                fullWindow[samplesCopied++] = (int) Math.round((window[j] + window[j + 1]) / 2.0D);
                j += 2;
            }
            else {
                fullWindow[samplesCopied++] = window[j++];
            }
        }

        applyWindowingFunction(fullWindow);

        return fullWindow;
    }

    /**
     * Extracts the {@code i}th sampling window from a full-length waveform.<br>
     * If is stereo signal, adjacent values will be averaged to produce mono samples.
     *
     * @param i index of window to be extracted
     *
     * @return a single window extracted from full-length audio waveform
     */
    public int[] extractWindow(final int[] wave, final int i) {
        // copy section of original waveform into sample array
        final int[] window = new int[windowSize + zeroPadLength];

        int j = i * ds * (isStereo ? 2 : 1); // index into source waveform array
        int samplesCopied = 0; // count samples copied to terminate loop once window size has been reached

        while (samplesCopied < windowSize && j < wave.length) {
            if (isStereo) {
                window[samplesCopied++] = (int) Math.round((wave[j] + wave[j + 1]) / 2.0D);
                j += 2;
            }
            else {
                window[samplesCopied++] = wave[j++];
            }
        }

        applyWindowingFunction(window);

        return window;
    }

    /**
     * Modifies a sample window by performing element-wise multiplication of samples with window function coefficients.
     *
     * @param window sample window to which windowing function should be applied
     */
    private void applyWindowingFunction(final int[] window) {
        if (WindowFunction.RECTANGULAR.equals(windowFunction)) {
            return;
        }

        windowFunction.generateWindow(windowSize, (index, coefficient) -> window[index] = (int) Math.round(window[index] * coefficient));
        // final double[] coefficients = windowFunction.generateWindow(windowSize);
        //
        // for (int i = 0; i < windowSize; i++) {
        //     window[i] = (int) Math.round(window[i] * coefficients[i]);
        // }
    }
}
