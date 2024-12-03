package de.freese.player.fft.output;

/**
 * A pair of numbers representing the amplitude of a certain frequency.<br>
 * Frequency buckets are produced as a result of the Fourier transform.
 */
public final class Frequency {
    /**
     * The frequency in Hz.
     */
    private final double hz;

    /**
     * The amplitude of the signal at this frequency.<br>
     * For normalized FFTs, this will be a value between 0 and 1.<br>
     * For un-normalized FFTs, this value could be arbitrarily large.
     */
    private double amplitude;

    public Frequency(final double hz, final double amp) {
        super();

        this.hz = hz;
        this.amplitude = amp;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double getHz() {
        return hz;
    }

    public void setAmplitude(final double amplitude) {
        this.amplitude = amplitude;
    }
}
