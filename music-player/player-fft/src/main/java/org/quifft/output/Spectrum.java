package org.quifft.output;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * The result of an FFT being computed for a single sampling window of an audio file.<br>
 * An {@link SpectraResult} for a full audio file will contain an array of {@link Spectrum}.
 */
public class Spectrum {
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

    public Spectrum(final Frequency[] frequencies, final double frameStartMs, final double frameEndMs) {
        super();

        this.frequencies = frequencies;
        this.frameStartMs = frameStartMs;
        this.frameEndMs = frameEndMs;
    }

    public Stream<Frequency> asStream() {
        return Arrays.stream(frequencies);
    }

    public void forEach(final Consumer<Frequency> consumer) {
        for (Frequency frequency : frequencies) {
            consumer.accept(frequency);
        }
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

    public Frequency getFrequency(final int index) {
        return frequencies[index];
    }

    public int length() {
        return frequencies.length;
    }
}
