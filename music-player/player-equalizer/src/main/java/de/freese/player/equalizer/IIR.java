// Created: 13 Okt. 2024
package de.freese.player.equalizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper for Infinite Impulse Response Algorithm.
 *
 * @author Thomas Freese
 */
public class IIR {
    static final IIRCoefficients[] IIR_CF15_44100 = {
            // 25 Hz
            new IIRCoefficients(9.9834072702e-01, 8.2963648917e-04, 1.9983280505e+00),
            // 40 Hz
            new IIRCoefficients(9.9734652663e-01, 1.3267366865e-03, 1.9973140908e+00),
            // 63 Hz
            new IIRCoefficients(9.9582396353e-01, 2.0880182333e-03, 1.9957435641e+00),
            // 100 Hz
            new IIRCoefficients(9.9337951306e-01, 3.3102434709e-03, 1.9931771947e+00),
            // 160 Hz
            new IIRCoefficients(9.8942832039e-01, 5.2858398053e-03, 1.9889114258e+00),
            // 250 Hz
            new IIRCoefficients(9.8353109588e-01, 8.2344520610e-03, 1.9822729654e+00),
            // 400 Hz
            new IIRCoefficients(9.7378088082e-01, 1.3109559588e-02, 1.9705764276e+00),
            // 630 Hz
            new IIRCoefficients(9.5901979676e-01, 2.0490101620e-02, 1.9511333590e+00),
            // 1k Hz
            new IIRCoefficients(9.3574903986e-01, 3.2125480071e-02, 1.9161350100e+00),
            // 1.6k Hz
            new IIRCoefficients(8.9923630641e-01, 5.0381846793e-02, 1.8501014162e+00),
            // 2.5k Hz
            new IIRCoefficients(8.4722457681e-01, 7.6387711593e-02, 1.7312785699e+00),
            // 4k Hz
            new IIRCoefficients(7.6755471307e-01, 1.1622264346e-01, 1.4881981417e+00),
            // 6.3k Hz
            new IIRCoefficients(6.6125377473e-01, 1.6937311263e-01, 1.0357747868e+00),
            // 10k Hz
            new IIRCoefficients(5.2683267950e-01, 2.3658366025e-01, 2.2218349322e-01),
            // 16k Hz
            new IIRCoefficients(4.0179628792e-01, 2.9910185604e-01, -9.1248032613e-01)
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(IIR.class);

    private final int bands;
    private final int channels;
    private final EqualizerControls controls;
    private final History[][] dataHistory;
    private final IIRCoefficients[] iirCoefficients;
    /**
     * Indexes for the history arrays.
     * These have to be kept between calls to this function hence they are static.
     */
    private int i;
    private int j;
    private int k;

    public IIR() {
        this(15, 2, 0F);
    }

    /**
     * @param bands is the number of bands to be used
     * @param channels is the number of channels
     * @param rate is the sample rate of equalizer
     */
    private IIR(final int bands, final int channels, final float rate) {
        super();

        this.bands = bands;
        this.channels = channels;
        this.controls = new EqualizerControls(bands);

        dataHistory = new History[bands][channels];

        // Depends on BandCount.
        iirCoefficients = IIR_CF15_44100;

        for (int ii = 0; ii < bands; ii++) {
            for (int jj = 0; jj < channels; jj++) {
                dataHistory[ii][jj] = new History();
            }
        }

        i = 0;
        j = 2;
        k = 1;
    }

    public void cleanHistory() {
        for (int ii = 0; ii < bands; ii++) {
            for (int jj = 0; jj < channels; jj++) {
                dataHistory[ii][jj].clear();
            }
        }

        i = 0;
        j = 2;
        k = 1;
    }

    public EqualizerControls getControls() {
        return controls;
    }

    public void iir(final int[] samplesLeft, final int[] samplesRight) {
        final double preampValue = controls.getPreampValue();
        final double[] bandValues = controls.getBands();

        // IIR filter equation is
        // y[n] = 2 * (alpha*(x[n]-x[n-2]) + gamma*y[n-1] - beta*y[n-2])
        //
        // NOTE: The 2 factor was introduced in the coefficients to save a multiplication
        // This algorithm cascades two filters to get nice filtering at the expense of extra CPU cycles.

        for (int index = 0; index < samplesLeft.length; index++) {
            // Preamp gain
            final double pcmLeft = samplesLeft[index] * preampValue;
            final double pcmRight = samplesRight[index] * preampValue;

            double outLeft = 0D;
            double outRight = 0D;

            // For each band
            for (int band = 0; band < bands; band++) {
                // Store Xi(n)
                final History historyLeft = dataHistory[band][0];
                historyLeft.setX(i, pcmLeft);

                final History historyRight = dataHistory[band][1];
                historyRight.setX(i, pcmRight);

                // Calculate and store Yi(n)
                final IIRCoefficients coefficients = iirCoefficients[band];

                historyLeft.setY(i,
                        // = alpha * [x(n)-x(n-2)]
                        coefficients.alpha() * (pcmLeft - historyLeft.getX(k))
                                // + gamma * y(n-1)
                                + coefficients.gamma() * historyLeft.getY(j)
                                // - beta * y(n-2)
                                - coefficients.beta() * historyLeft.getY(k)
                );

                historyRight.setY(i,
                        coefficients.alpha() * (pcmRight - historyRight.getX(k))
                                + coefficients.gamma() * historyRight.getY(j)
                                - coefficients.beta() * historyRight.getY(k)
                );

                // The multiplication by 2.0 was 'moved' into the coefficients to save CPU cycles here.
                // Apply the gain
                outLeft += historyLeft.getY(i) * bandValues[band]; // * 2.0D;
                outRight += historyRight.getY(i) * bandValues[band]; // * 2.0D;
            }

            // Volume stuff
            // Scale down original PCM sample and add it to the filters output.
            // This substitutes the multiplication by 0.25.
            // Go back to use the floating point multiplication before the conversion to give more dynamic range.
            outLeft += pcmLeft * 0.25D;
            outRight += pcmRight * 0.25D;

            // Normalize the output
            outLeft *= 4D;
            outRight *= 4D;

            if (LOGGER.isTraceEnabled() && index % 50 == 0) {
                LOGGER.trace("left: in {} / out {}; right: in {} / out {}", samplesLeft[index], (int) outLeft, samplesRight[index], (int) outRight);
            }

            samplesLeft[index] = (int) outLeft;
            samplesRight[index] = (int) outRight;
        }

        i++;
        j++;
        k++;

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
