/*
 *  21.04.2004 Original verion. davagin@udm.ru.
 *-----------------------------------------------------------------------
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */
package de.freese.player.equalizer;

/**
 * Controls of equalizer
 * Author: Dmitry Vaguine
 * Date: 02.05.2004
 * Time: 12:00:29
 */
public class IIRControls {
    /**
     * Gain for each band
     * values should be between -0.2 and 1.0
     */
    private final float[][] bands;
    /**
     * Volume gain
     * values should be between 0.0 and 1.0
     */
    private final float[] preamp;

    /**
     * Creates new IIRControls object for given number of bands
     *
     * @param bandsNum is the number of bands
     * @param channels is the number of channels
     */
    public IIRControls(final int bandsNum, final int channels) {
        super();

        preamp = new float[channels];
        bands = new float[bandsNum][channels];

        for (int j = 0; j < channels; j++) {
            preamp[j] = 1.0F;

            for (int i = 0; i < bandsNum; i++) {
                bands[i][j] = 0F;
            }
        }
    }

    /**
     * Returns value of control for given band and channel
     *
     * @param band is the index of band
     * @param channel is the index of channel
     */
    public float getBandValue(final int band, final int channel) {
        return bands[band][channel];
    }

    /**
     * Returns the maximum value for band control (in Db)
     *
     * @return the maximum value for band control
     */
    public float getMaximumBandDbValue() {
        return 12F;
    }

    /**
     * Returns the maximum value for band control
     *
     * @return the maximum value for band control
     */
    public float getMaximumBandValue() {
        return 1.0F;
    }

    /**
     * Returns the maximum value for preamp control (in Db)
     *
     * @return the maximum value for preamp control
     */
    public float getMaximumPreampDbValue() {
        return 12F;
    }

    /**
     * Returns the maximum value for preamp control
     *
     * @return the maximum value for preamp control
     */
    public float getMaximumPreampValue() {
        return 1.0F;
    }

    /**
     * Returns the minimum value for band control (in Db)
     *
     * @return the minimum value for band control
     */
    public float getMinimumBandDbValue() {
        return -12F;
    }

    /**
     * Returns the minimum value for band control
     *
     * @return the minimum value for band control
     */
    public float getMinimumBandValue() {
        return -0.2F;
    }

    /**
     * Returns the minimum value for preamp control (in Db)
     *
     * @return the minimum value for preamp control
     */
    public float getMinimumPreampDbValue() {
        return -12F;
    }

    /**
     * Returns the minimum value for preamp control
     *
     * @return the minimum value for preamp control
     */
    public float getMinimumPreampValue() {
        return 0F;
    }

    /**
     * Returns value of preamp control for given channel
     *
     * @param channel is the index of channel
     */
    public float getPreampValue(final int channel) {
        return preamp[channel];
    }

    /**
     * Setter for value of control for given band and channel
     *
     * @param band is the index of band
     * @param channel is the index of channel
     * @param value is the new value
     */
    public void setBandValue(final int band, final int channel, final float value) {
        bands[band][channel] = value;
    }

    /**
     * Setter for value of preamp control for given channel (in Db)
     *
     * @param channel is the index of channel
     * @param value is the new value
     */
    public void setPreampDbValue(final int channel, final float value) {
        // -12dB .. 12dB mapping
        preamp[channel] = (float) (9.9999946497217584440165E-01 * Math.exp(6.9314738656671842642609E-02 * value) + 3.7119444716771825623636E-07);
    }

    /**
     * Setter for value of preamp control for given channel
     *
     * @param channel is the index of channel
     * @param value is the new value
     */
    public void setPreampValue(final int channel, final float value) {
        preamp[channel] = value;
    }

    /**
     * Returns bands array
     *
     * @return bands array
     */
    float[][] getBands() {
        return bands;
    }

    /**
     * Returns preamp array
     *
     * @return preamp array
     */
    float[] getPreamp() {
        return preamp;
    }

    /**
     * Setter for value of control for given band and channel (in Db)
     *
     * @param band is the index of band
     * @param channel is the index of channel
     * @param value is the new value
     */
    void setBandDbValue(final int band, final int channel, final float value) {
        // Map the gain and preamp values.
        // -12dB .. 12dB mapping
        bands[band][channel] = (float) (2.5220207857061455181125E-01 * Math.exp(8.0178361802353992349168E-02 * value) - 2.5220207852836562523180E-01);
    }
}
