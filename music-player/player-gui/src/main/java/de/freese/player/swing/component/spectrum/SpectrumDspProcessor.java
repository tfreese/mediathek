// Created: 29 Aug. 2024
package de.freese.player.swing.component.spectrum;

import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(SpectrumDspProcessor.class);

    /**
     * Smoothing
     */
    private static void doSmoothing(final int length, final int[] window) {
        WindowFunction.HANNING.generateWindow(length, (index, coefficient) -> window[index] = (int) Math.round(window[index] * coefficient));
    }

    private final FFTConfig fftConfig = new FFTConfig().windowOverlap(0.5D);
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
        Thread.startVirtualThread(() -> doFFT(window));
        // PlayerSettings.getExecutorService().execute(() -> doFFT(window));
        // doFFT(window);
    }

    @Override
    public void reset() {
        maxAmp = 0D;
    }

    private void doFFT(final Window window) {
        final int[] mergedSamples = window.getMergedSamples();

        final int[] fftSamples;

        if (lastSamples == null) {
            fftSamples = mergedSamples;
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

            fftSamples = new int[samplesToKeep + samplesToRead];
            System.arraycopy(lastSamples, lastSamples.length - samplesToKeep, fftSamples, 0, samplesToKeep);
            System.arraycopy(mergedSamples, 0, fftSamples, samplesToKeep, samplesToRead);
        }

        lastSamples = mergedSamples;

        doSmoothing(fftSamples.length, fftSamples);

        fftConfig.windowSize(fftSamples.length);

        final Spectrum spectrum = FFTComputationWrapper.createSpectrum(fftSamples, window.getAudioFormat().getSampleRate(), fftConfig);

        final Frequency frequency = FFTMath.findMaxAmplitude(spectrum);
        // FFTMath.normalize(spectrum, frequency.getAmplitude());

        maxAmp = Math.max(maxAmp, frequency.getAmplitude());
        FFTMath.normalize(spectrum, maxAmp);

        LOGGER.trace("maxAmp = {}", maxAmp);

        SwingUtilities.invokeLater(() -> spectrumConsumer.accept(spectrum));
    }
}
