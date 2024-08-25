package de.freese.player.fft.output;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The result of an FFT being computed for a single sampling window of an audio file.<br>
 * An {@link SpectraResult} for a full audio file will contain an array of {@link Spectrum}.
 */
public final class Spectrum implements Iterable<Frequency> {
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

        Objects.requireNonNull(frequencies, "frequencies required");

        if (frequencies.length == 0) {
            throw new IllegalArgumentException("frequencies are empty");
        }

        this.frequencies = frequencies;
        this.frameStartMs = frameStartMs;
        this.frameEndMs = frameEndMs;
    }

    public Stream<Frequency> asStream() {
        return Arrays.stream(frequencies);
    }

    public double getFrameEndMs() {
        return frameEndMs;
    }

    public double getFrameStartMs() {
        return frameStartMs;
    }

    public Frequency getFrequency(final int index) {
        return frequencies[index];
    }

    public Iterator<Frequency> iterator() {
        return new Iterator<>() {
            private int index;

            @Override
            public boolean hasNext() {
                return index < length();
            }

            @Override
            public Frequency next() {
                if (index >= length()) {
                    throw new NoSuchElementException();
                }

                final Frequency frequency = getFrequency(index);

                index++;

                return frequency;
            }
        };
    }

    public int length() {
        return frequencies.length;
    }
}
