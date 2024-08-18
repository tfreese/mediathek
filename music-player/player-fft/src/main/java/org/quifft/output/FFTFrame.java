package org.quifft.output;

/**
 * The result of an FFT being computed for a single sampling window of an audio file.<br>
 * An {@link FFTResult} for a full audio file will contain an array of FFTFrames.
 */
public class FFTFrame {
    /**
     * End time in milliseconds from the original audio file for the sampling window used to compute this frame.
     */
    private final double frameEndMs;
    /**
     * Start time in milliseconds from the original audio file for the sampling window used to compute this frame.
     */
    private final double frameStartMs;
    /**
     * In a discrete Fourier transform, each represents a range of frequencies in Hz.<br>
     * A {@link Frequency} contains the amplitude of this range from the original sound wave.
     */
    private final Frequency[] frequencies;

    public FFTFrame(final double startMs, final double endMs, final Frequency[] frequencies) {
        super();

        this.frameStartMs = startMs;
        this.frameEndMs = endMs;
        this.frequencies = frequencies;
    }

    public double getFrameEndMs() {
        return frameEndMs;
    }

    public double getFrameStartMs() {
        return frameStartMs;
    }

    public Frequency[] getFrequencies() {
        return frequencies;
    }
}
