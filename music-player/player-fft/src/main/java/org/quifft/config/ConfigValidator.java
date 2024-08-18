package org.quifft.config;

/**
 * Validates {@link FFTConfig} prior to the computation of FFT and throws a {@link BadConfigException} if
 * any invalid parameters are found.
 */
public final class ConfigValidator {
    /**
     * Runs through checklist of parameter validations and throws exception if any issues are identified.
     *
     * @param config parameters of the FFT to be computed
     * @param isFFTStream true if is FFTStream, false if is FFTResult
     *
     * @throws BadConfigException if there is an invalid parameter
     */
    public static void validate(final FFTConfig config, final boolean isFFTStream) {
        // window size must be > 0
        if (config.getWindowSize() <= 0) {
            throw new BadConfigException(String.format("Window size must be positive; was set to %d", config.getWindowSize()));
        }

        // window size must be a power of 2 if num points is not set
        if (config.getNumPoints() == null && !isPow2(config.getWindowSize())) {
            throw new BadConfigException(String.format("If number of points is not set, window size must be a "
                    + "power of 2; was set to %1$d. If you'd like to use a window of size %1$d, "
                    + "set numPoints to the next power of 2 greater than %1$d so the signal will "
                    + "be zero-padded up to that length.", config.getWindowSize()));
        }

        // window function cannot be null
        if (config.getWindowFunction() == null) {
            throw new BadConfigException("Window function cannot be null");
        }

        // window overlap must be positive and less than 1
        if (config.getWindowOverlap() < 0D || config.getWindowOverlap() >= 1D) {
            throw new BadConfigException(String.format("Window overlap must be a positive value between 0 and 0.99; was set to %f", config.getWindowOverlap()));
        }

        // num points, if set, must be positive
        if (config.getNumPoints() != null && config.getNumPoints() < 0) {
            throw new BadConfigException(String.format("Number of points in FFT must be positive; was set to %d", config.getNumPoints()));
        }

        // num points, if set, must be greater than or equal to window size
        if (config.getNumPoints() != null && config.getNumPoints() < config.getWindowSize()) {
            throw new BadConfigException(String.format("Number of points in FFT must be at least as large as window size; window size was %d but numPoints was only %d",
                    config.getWindowSize(), config.getNumPoints()));
        }

        // num points, if set, must be a power of 2
        if (config.getNumPoints() != null && !isPow2(config.getNumPoints())) {
            throw new BadConfigException(String.format("Number of points in FFT must be a power of two; was set to %d", config.getNumPoints()));
        }

        // // normalization without dB scale can't be on for an FFTStream
        // if (isFFTStream && !config.isDecibelScale() && config.isNormalized()) {
        //     throw new BadConfigException("Normalization can't be used without also using dB scale for an FFTStream "
        //             + "because it doesn't make any sense -- normalization relies on knowing the maximum amplitude across "
        //             + "any frequency in the entire file, and FFTStream only knows the maximum frequency of one window "
        //             + "at a time. If you'd like to use normalization with an FFTStream, it's recommended that you implement this yourself");
        // }
    }

    private static boolean isPow2(final int n) {
        return n > 1 && (n & (n - 1)) == 0;
    }

    private ConfigValidator() {
        super();
    }
}
