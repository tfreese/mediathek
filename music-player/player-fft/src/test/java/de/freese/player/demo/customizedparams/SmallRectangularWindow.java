// Created: 08 Aug. 2024
package de.freese.player.demo.customizedparams;

import java.io.IOException;
import java.nio.file.Path;

import javax.sound.sampled.UnsupportedAudioFileException;

import de.freese.player.fft.FFTFactory;
import de.freese.player.fft.config.FFTConfig;
import de.freese.player.fft.output.Frequency;
import de.freese.player.fft.output.SpectraResult;
import de.freese.player.fft.sampling.WindowFunction;

/**
 * @author Thomas Freese
 */
public final class SmallRectangularWindow {
    public static void main(final String[] args) throws Exception {
        final SmallRectangularWindow normalizedFFT = new SmallRectangularWindow();
        normalizedFFT.computeFFTWithCustomizedParameters();
    }

    private final Path sineWave600Hz;

    private SmallRectangularWindow() throws Exception {
        super();

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        sineWave600Hz = Path.of(classLoader.getResource("600hz-tone-3secs-mono.wav").toURI());
    }

    private void computeFFTWithCustomizedParameters() {
        // compute an FFT with customized settings
        SpectraResult result = null;

        try {
            // create FFT with window size of 1024 samples and overlap of 75%
            result = FFTFactory.createFull(sineWave600Hz, new FFTConfig().windowFunction(WindowFunction.RECTANGULAR).windowSize(1024).windowOverlap(0.75));
        }
        catch (IOException ex) {
            System.out.println("An I/O exception occurred when opening an input stream to the audio file");
        }
        catch (UnsupportedAudioFileException ex) {
            System.out.println("Invalid audio file");
        }

        // print the SpectraResult to see details about the transformation and the audio file on which it was performed
        System.out.println(result);

        // get individual frames (sampling windows) from FFT
        System.out.println("There are " + result.length() + " spectra in this FFT, each of which was computed from a sampling window that was about "
                + Math.round(result.getWindowDurationMs()) + " milliseconds long.");

        // inspect amplitudes of individual frequency in the first Spectrum
        final Frequency firstFrequency = result.getSpectrum(0).getFrequency(0);
        System.out.println("The first Frequency, located at " + Math.round(firstFrequency.getHz()) + " Hz, has an amplitude of "
                + Math.round(firstFrequency.getAmplitude()) + " dB.");

        final Frequency mostPowerfulFrequency = result.getSpectrum(0).getFrequency(14); // closest to 600 Hz
        System.out.println("The 14th Frequency, located at " + Math.round(mostPowerfulFrequency.getHz()) + " Hz, has an amplitude of "
                + Math.round(mostPowerfulFrequency.getAmplitude()) + " dB.");

        // remarks about window size's effect on frequency resolution
        System.out.println();
        System.out.println("Notice that the frequency resolution has worsened from the \"First FFT\" example "
                + "because we're taking sample windows of smaller size (1024 samples instead of the default 4096).");
    }
}
