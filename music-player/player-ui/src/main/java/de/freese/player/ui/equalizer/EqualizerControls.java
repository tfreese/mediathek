// Created: 21 Sept. 2024
package de.freese.player.ui.equalizer;

/**
 * @author Thomas Freese
 */
public final class EqualizerControls {
    /**
     * Gain for each band.
     * Values should be between -0.2 and 1.0.
     */
    private final double[] bands;

    /**
     * Volume gain.
     * Values should be between 0.0 and 1.0.
     */
    private double preamp = 1D;

    public EqualizerControls(final int bandCount) {
        super();

        bands = new double[bandCount];
    }

    public double getBand(final int index) {
        return bands[index];
    }

    public double[] getBands() {
        return bands;
    }

    // /**
    //  * Returns the maximum value for band control (in Db).
    //  */
    // public double getMaximumBandDbValue() {
    //     return 12D;
    // }

    /**
     * Returns the maximum value for band control.
     */
    public double getMaximumBandValue() {
        return 0.5D;
    }

    // /**
    //  * Returns the maximum value for preamp control (in Db).
    //  */
    // public double getMaximumPreampDbValue() {
    //     return 12D;
    // }

    /**
     * Returns the maximum value for preamp control.
     */
    public double getMaximumPreampValue() {
        return 1.5D;
    }

    // /**
    //  * Returns the minimum value for band control (in Db).
    //  */
    // public double getMinimumBandDbValue() {
    //     return -12D;
    // }

    /**
     * Returns the minimum value for band control.
     */
    public double getMinimumBandValue() {
        return -0.5D;
    }

    // /**
    //  * Returns the minimum value for preamp control (in Db).
    //  */
    // public double getMinimumPreampDbValue() {
    //     return -12D;
    // }

    /**
     * Returns the minimum value for preamp control.
     */
    public double getMinimumPreampValue() {
        return 0D;
    }

    /**
     * Returns value of preamp control.
     */
    public double getPreampValue() {
        return preamp;
    }

    public boolean isEnabled() {
        // return enabled;
        double bandSum = 0D;

        for (double band : bands) {
            bandSum += band;
        }

        return Double.compare(preamp, 1D) != 0 || Double.compare(bandSum, 0D) != 0;
    }

    /**
     * Setter for value of control for given band (in Db).
     */
    public void setBandDbValue(final int band, final double value) {
        // Map the gain and preamp values.
        // -12dB .. 12dB mapping
        bands[band] = 2.5220207857061455181125E-01D * Math.exp(8.0178361802353992349168E-02D * value) - 2.5220207852836562523180E-01D;
        // bands[band] = PlayerUtils.rescale(value, getMinimumBandDbValue(), getMaximumBandDbValue(), -0.2D, 1D);
    }

    /**
     * Setter for value of control for given band.
     */
    public void setBandValue(final int band, final double value) {
        bands[band] = value;
    }

    /**
     * Setter for value of preamp control (in Db).
     */
    public void setPreampDbValue(final double value) {
        // -12dB .. 12dB mapping
        preamp = 9.9999946497217584440165E-01D * Math.exp(6.9314738656671842642609E-02D * value) + 3.7119444716771825623636E-07D;
        // preamp = PlayerUtils.rescale(value, getMinimumPreampDbValue(), getMaximumPreampDbValue(), 0D, 1D);
    }

    /**
     * Setter for value of preamp control.
     */
    public void setPreampValue(final double value) {
        preamp = value;
    }
}
