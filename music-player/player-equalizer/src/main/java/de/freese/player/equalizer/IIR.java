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
 * Generic wrapper around IIR algorithm.
 * Author: Dmitry Vaguine
 * Date: 02.05.2004
 * Time: 12:00:29
 */
public class IIR extends IIRBase {
    /**
     * Supported number of bands
     */
    public static final int EQ_10_BANDS = 10;
    /**
     * Supported sample rates
     */
    public static final float EQ_11025_RATE = 11025;
    public static final int EQ_15_BANDS = 15;
    public static final float EQ_22050_RATE = 22050;
    public static final int EQ_25_BANDS = 25;
    public static final int EQ_31_BANDS = 31;
    public static final float EQ_44100_RATE = 44100;
    public static final float EQ_48000_RATE = 48000;
    /**
     * Max bands supported by the code
     */
    public static final int EQ_MAX_BANDS = 31;
    /**
     * Max number of channels supported
     */
    public static final int EQ_MAX_CHANNELS = 2;

    /**
     * This is special method for checking of supported parameters of equalizer
     *
     * @param bands is the number of bands
     * @param rate is the sample rate of data
     * @param channels is the number of channels
     *
     * @return true if parameters are supported
     */
    public static boolean isParamsSupported(final int bands, final float rate, final int channels) {
        if (Float.compare(rate, EQ_11025_RATE) != 0
                && Float.compare(rate, EQ_22050_RATE) != 0
                && Float.compare(rate, EQ_44100_RATE) != 0
                && Float.compare(rate, EQ_48000_RATE) != 0) {
            return false;
        }

        switch (bands) {
            case EQ_10_BANDS:
            case EQ_15_BANDS:
            case EQ_25_BANDS:
            case EQ_31_BANDS:
                break;
            default:
                return false;
        }

        switch (channels) {
            case 1:
            case 2:
                break;
            default:
                return false;
        }

        return Float.compare(rate, EQ_11025_RATE) != 0 && Float.compare(rate, EQ_22050_RATE) != 0 || bands == EQ_10_BANDS;
    }

    private final int bands;
    private final int channels;
    /* History for two filters */
    private final XYData[][] dataHistory = new XYData[EQ_MAX_BANDS][EQ_MAX_CHANNELS];
    private final XYData[][] dataHistory2 = new XYData[EQ_MAX_BANDS][EQ_MAX_CHANNELS];
    /* Equalizer config */
    private final IIRControls eqcfg;
    private final float rate;

    /* Indexes for the history arrays
     * These have to be kept between calls to this function
     * hence they are static */
    private int i;
    private IIRCoefficients[] iircf;
    private int j;
    private int k;

    /**
     * Constructs equalizer with given config
     *
     * @param bands is the number of bands to be used
     * @param rate is the sample rate of equalizer
     * @param channels is the number of channels
     */
    public IIR(final int bands, final float rate, final int channels) {
        super();

        this.rate = rate;
        this.channels = channels;
        this.bands = bands;
        this.eqcfg = new IIRControls(bands, channels);

        if (!isParamsSupported(bands, rate, channels)) {
            throw new IllegalArgumentException("Unsupported parameters");
        }

        initIir();
    }

    /**
     * Clear filter history.
     */
    public void cleanHistory() {
        for (int ii = 0; ii < EQ_MAX_BANDS; ii++) {
            for (int jj = 0; jj < EQ_MAX_CHANNELS; jj++) {
                dataHistory[ii][jj].zero();
                dataHistory2[ii][jj].zero();
            }
        }

        i = 0;
        j = 2;
        k = 1;
    }

    /**
     * Returns Controls of equalizer
     */
    public IIRControls getControls() {
        return eqcfg;
    }

    /**
     * Main filtering method.
     *
     * @param data - data to be filtered
     * @param length - length of data in buffer
     */
    public void iir(final int[] data, final int length) {
        final float[] eqpreamp = eqcfg.getPreamp();
        final float[][] eqbands = eqcfg.getBands();

        // IIR filter equation is
        // y[n] = 2 * (alpha*(x[n]-x[n-2]) + gamma*y[n-1] - beta*y[n-2])
        //
        // NOTE: The 2 factor was introduced in the coefficients to save a multiplication
        //
        // This algorithm cascades two filters to get nice filtering
        // at the expense of extra CPU cycles
        IIRCoefficients tempcf;
        XYData tempd;

        for (int index = 0; index < length; index += channels) {
            // For each channel
            for (int channel = 0; channel < channels; channel++) {
                // Preamp gain
                final double pcm = data[index + channel] * eqpreamp[channel];

                double out = 0F;

                // For each band
                for (int band = 0; band < bands; band++) {
                    // Store Xi(n)
                    tempd = dataHistory[band][channel];
                    tempd.setX(i, pcm);
                    // Calculate and store Yi(n)
                    tempcf = iircf[band];
                    tempd.setY(i,
                            // = alpha * [x(n)-x(n-2)]
                            tempcf.getAlpha() * (pcm - tempd.getX(k))
                                    // + gamma * y(n-1)
                                    + tempcf.getGamma() * tempd.getY(j)
                                    // - beta * y(n-2)
                                    - tempcf.getBeta() * tempd.getY(k)
                    );

                    // The multiplication by 2.0 was 'moved' into the coefficients to save CPU cycles here
                    // Apply the gain
                    out += tempd.getY(i) * eqbands[band][channel]; // * 2.0D;
                } // For each band

                // Volume stuff
                // Scale down original PCM sample and add it to the filters output.
                // This substitutes the multiplication by 0.25.
                // Go back to use the floating point multiplication before the conversion to give more dynamic range.
                out += pcm * 0.25D;

                // Normalize the output
                out *= 4D;

                // Round and convert to integer
                data[index + channel] = (int) out;
            }

            i++;
            j++;
            k++;

            // Wrap around the indexes
            if (i == 3) {
                i = 0;
            }
            else if (j == 3) {
                j = 0;
            }
            else {
                k = 0;
            }

        }
    }

    /**
     * Init the filters
     */
    private void initIir() {
        setFilters();

        for (int ii = 0; ii < EQ_MAX_BANDS; ii++) {
            for (int jj = 0; jj < EQ_MAX_CHANNELS; jj++) {
                dataHistory[ii][jj] = new XYData();
                dataHistory2[ii][jj] = new XYData();
            }
        }

        i = 0;
        j = 2;
        k = 1;
    }

    private void setFilters() {
        if (Float.compare(rate, EQ_11025_RATE) == 0) {
            iircf = IIR_CF10_11K_11025;
        }
        else if (Float.compare(rate, EQ_22050_RATE) == 0) {
            iircf = IIR_CF10_22K_22050;
        }
        else if (Float.compare(rate, EQ_44100_RATE) == 0) {
            switch (bands) {
                case 31:
                    iircf = IIR_CF31_44100;
                    break;
                case 25:
                    iircf = IIR_CF25_44100;
                    break;
                case 15:
                    iircf = IIR_CF15_44100;
                    break;
                default:
                    iircf = IIR_CF10_44100;
                    break;
            }
        }
        else if (Float.compare(rate, EQ_48000_RATE) == 0) {
            switch (bands) {
                case 31:
                    iircf = IIR_CF31_48000;
                    break;
                case 25:
                    iircf = IIR_CF25_48000;
                    break;
                case 15:
                    iircf = IIR_CF15_48000;
                    break;
                default:
                    iircf = IIR_CF10_48000;
                    break;
            }
        }
    }
}
