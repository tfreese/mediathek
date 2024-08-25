package de.freese.player.fft.config;

import java.io.Serial;

import de.freese.player.fft.output.SpectrumStream;

/**
 * Raised when {@link FFTConfig} is illogical, invalid, or otherwise incorrect.
 * <p>This exception will be raised in the following cases: </p>
 * <ul>
 *     <li>{@code windowSize} is less than or equal to 0</li>
 *     <li>{@code numPoints} is not set and {@code windowSize} is not a power of 2</li>
 *     <li>{@code windowFunction} is null</li>
 *     <li>{@code windowOverlap} is negative</li>
 *     <li>{@code windowOverlap} is 1.00 or greater</li>
 *     <li>{@code numPoints} is negative</li>
 *     <li>{@code numPoints} is set to be less than {@code windowSize}</li>
 *     <li>{@code numPoints} is set to a value that is not a power of 2</li>
 *     <li>{@code useDecibelScale} is set to false and {@code isNormalzed} is set to true when using an {@link SpectrumStream}</li>
 *  </ul>
 *
 * @see FFTConfig
 */
public class BadConfigException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7337564780038375321L;

    /**
     * Constructs a new {@link BadConfigException} with an explanation of which parameter is invalid.
     *
     * @param msg explanation of which FFT parameter is invalid or illogical
     */
    public BadConfigException(final String msg) {
        super(msg);
    }
}
