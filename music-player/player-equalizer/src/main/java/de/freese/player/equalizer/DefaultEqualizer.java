// Created: 05 Nov. 2024
package de.freese.player.equalizer;

/**
 * Equalizer with Infinite Impulse Response (IIR) Algorithm.
 *
 * @author Thomas Freese
 * @see <a href="https://github.com/sedmelluq/lavaplayer/tree/master/main/src/main/java/com/sedmelluq/discord/lavaplayer/filter/equalizer">lavaplayer-equalizer</a>
 */
public final class DefaultEqualizer implements Equalizer {
    private static final IIRCoefficients[] COEFFICIENTS_48000 = {
            // 25 Hz
            new IIRCoefficients(9.9847546664e-01D, 7.6226668143e-04D, 1.9984647656e+00D),
            // 40 Hz
            new IIRCoefficients(9.9756184654e-01D, 1.2190767289e-03D, 1.9975344645e+00D),
            // 63 Hz
            new IIRCoefficients(9.9616261379e-01D, 1.9186931041e-03D, 1.9960947369e+00D),
            // 100 Hz
            new IIRCoefficients(9.9391578543e-01D, 3.0421072865e-03D, 1.9937449618e+00D),
            // 160 Hz
            new IIRCoefficients(9.9028307215e-01D, 4.8584639242e-03D, 1.9898465702e+00D),
            // 250 Hz
            new IIRCoefficients(9.8485897264e-01D, 7.5705136795e-03D, 1.9837962543e+00D),
            // 400 Hz
            new IIRCoefficients(9.7588512657e-01D, 1.2057436715e-02D, 1.9731772447e+00D),
            // 630 Hz
            new IIRCoefficients(9.6228521814e-01D, 1.8857390928e-02D, 1.9556164694e+00D),
            // 1k Hz
            new IIRCoefficients(9.4080933132e-01D, 2.9595334338e-02D, 1.9242054384e+00D),
            // 1.6k Hz
            new IIRCoefficients(9.0702059196e-01D, 4.6489704022e-02D, 1.8653476166e+00D),
            // 2.5k Hz
            new IIRCoefficients(8.5868004289e-01D, 7.0659978553e-02D, 1.7600401337e+00D),
            // 4k Hz
            new IIRCoefficients(7.8409610788e-01D, 1.0795194606e-01D, 1.5450725522e+00D),
            // 6.3k Hz
            new IIRCoefficients(6.8332861002e-01D, 1.5833569499e-01D, 1.1426447155e+00D),
            // 10k Hz
            new IIRCoefficients(5.5267518228e-01D, 2.2366240886e-01D, 4.0186190803e-01D),
            // 16k Hz
            new IIRCoefficients(4.1811888447e-01D, 2.9094055777e-01D, -7.0905944223e-01D)
    };

    private final EqualizerControls controls = new EqualizerControls(COEFFICIENTS_48000.length);
    private final double[] historyLeft = new double[COEFFICIENTS_48000.length * 6];
    private final double[] historyRight = new double[COEFFICIENTS_48000.length * 6];

    private int current;
    private int minusOne;
    private int minusTwo;

    public DefaultEqualizer() {
        super();

        cleanHistory();
    }

    @Override
    public void cleanHistory() {
        for (int i = 0; i < historyLeft.length; i++) {
            historyLeft[i] = 0F;
            historyRight[i] = 0F;
        }

        current = 0;
        minusOne = 2;
        minusTwo = 1;
    }

    @Override
    public void equalize(final int[] samplesLeft, final int[] samplesRight) {
        // IIR filter equation is
        // y[n] = 2 * (alpha*(x[n]-x[n-2]) + gamma*y[n-1] - beta*y[n-2])
        //
        // NOTE: The 2 factor was introduced in the coefficients to save a multiplication
        // This algorithm cascades two filters to get nice filtering at the expense of extra CPU cycles.

        final double preampValue = controls.getPreampValue();
        final double[] bandValues = controls.getBands();

        for (int sampleIndex = 0; sampleIndex < samplesLeft.length; sampleIndex++) {
            final int sampleLeft = samplesLeft[sampleIndex];
            final int sampleRight = samplesRight[sampleIndex];

            // Volume stuff
            // Scale down the original PCM sample and add it to the filter output.
            // This substitutes the multiplication by 0.25.
            // Go back to use the floating point multiplication before the conversion to give more dynamic range.
            double resultLeft = sampleLeft * 0.25D * preampValue;
            double resultRight = sampleRight * 0.25D * preampValue;

            for (int bandIndex = 0; bandIndex < bandValues.length; bandIndex++) {
                final int x = bandIndex * 6;
                final int y = x + 3;

                final IIRCoefficients coefficients = COEFFICIENTS_48000[bandIndex];

                final double bandResultLeft =
                        // = alpha * [x(n)-x(n-2)]
                        coefficients.alpha() * (sampleLeft - historyLeft[x + minusTwo])
                                +
                                // + gamma * y(n-1)
                                coefficients.gamma() * historyLeft[y + minusOne]
                                -
                                // - beta * y(n-2)
                                coefficients.beta() * historyLeft[y + minusTwo];

                final double bandResultRight =
                        coefficients.alpha() * (sampleRight - historyRight[x + minusTwo])
                                +
                                coefficients.gamma() * historyRight[y + minusOne]
                                -
                                coefficients.beta() * historyRight[y + minusTwo];

                historyLeft[x + current] = sampleLeft;
                historyLeft[y + current] = bandResultLeft;

                historyRight[x + current] = sampleRight;
                historyRight[y + current] = bandResultRight;

                resultLeft += bandResultLeft * bandValues[bandIndex];
                resultRight += bandResultRight * bandValues[bandIndex];
            }

            // Normalize the output.
            // samplesLeft[sampleIndex] = (int) Math.min(Math.max(resultLeft * 4.0D, -1.0D), 1.0D);
            samplesLeft[sampleIndex] = (int) (resultLeft * 4.0D);
            // samplesRight[sampleIndex] = (int) Math.min(Math.max(resultRight * 4.0D, -1.0D), 1.0D);
            samplesRight[sampleIndex] = (int) (resultRight * 4.0D);

            current++;
            if (current == 3) {
                current = 0;
            }

            minusOne++;
            if (minusOne == 3) {
                minusOne = 0;
            }

            minusTwo++;
            if (minusTwo == 3) {
                minusTwo = 0;
            }
        }
    }

    @Override
    public EqualizerControls getControls() {
        return controls;
    }
}
