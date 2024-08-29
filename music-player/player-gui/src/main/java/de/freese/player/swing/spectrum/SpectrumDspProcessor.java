// Created: 29 Aug. 2024
package de.freese.player.swing.spectrum;

import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import de.freese.player.PlayerSettings;
import de.freese.player.dsp.DspProcessor;
import de.freese.player.fft.config.FFTConfig;
import de.freese.player.fft.math.FFTComputationWrapper;
import de.freese.player.fft.math.FFTMath;
import de.freese.player.fft.output.Frequency;
import de.freese.player.fft.output.Spectrum;
import de.freese.player.fft.sampling.WindowFunction;
import de.freese.player.model.Window;

/**
 * @author Thomas Freese
 */
public class SpectrumDspProcessor implements DspProcessor {
    private final Consumer<Spectrum> spectrumConsumer;

    private int[] lastSamples;
    private double maxAmp;

    public SpectrumDspProcessor(final Consumer<Spectrum> spectrumConsumer) {
        super();

        this.spectrumConsumer = Objects.requireNonNull(spectrumConsumer, "spectrumConsumer required");
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void process(final Window window) {
        PlayerSettings.getExecutorService().execute(() -> doFFT(window));
        // doFFT(audioFormat, window);
    }

    private void doFFT(final Window window) {
        final int[] mergedSamples = window.getMergedSamples();

        FFTConfig fftConfig = new FFTConfig().windowOverlap(0.5D);

        final int[] samples;

        if (lastSamples == null) {
            samples = mergedSamples;

            // Smoothing
            final double[] coefficients = WindowFunction.HANNING.generateWindow(samples.length);

            for (int i = 0; i < samples.length; i++) {
                samples[i] = (int) Math.round(samples[i] * coefficients[i]);
            }
        }
        else {
            final int samplesToKeep = (int) (lastSamples.length * fftConfig.getWindowOverlap());
            final int samplesToRead;

            if (mergedSamples.length - samplesToKeep < 0) {
                // End of Song.
                samplesToRead = mergedSamples.length;
            }
            else {
                samplesToRead = mergedSamples.length - samplesToKeep;
            }

            // Smoothing
            final double[] coefficients = WindowFunction.HANNING.generateWindow(samplesToRead);

            for (int i = 0; i < samplesToRead; i++) {
                mergedSamples[i] = (int) Math.round(mergedSamples[i] * coefficients[i]);
            }

            samples = new int[samplesToKeep + samplesToRead];
            System.arraycopy(lastSamples, lastSamples.length - samplesToKeep, samples, 0, samplesToKeep);
            System.arraycopy(mergedSamples, 0, samples, samplesToKeep, samplesToRead);
        }

        lastSamples = samples;

        fftConfig = fftConfig.windowSize(samples.length);

        final Spectrum spectrum = FFTComputationWrapper.createSpectrum(samples, window.getAudioFormat().getSampleRate(), fftConfig);

        final Frequency frequency = FFTMath.findMaxAmplitude(spectrum);
        // FFTMath.normalize(spectrum, frequency.getAmplitude());

        maxAmp = Math.max(maxAmp, frequency.getAmplitude());
        FFTMath.normalize(spectrum, maxAmp);

        // System.out.println("maxAmp = " + maxAmp);

        SwingUtilities.invokeLater(() -> spectrumConsumer.accept(spectrum));
    }
}
