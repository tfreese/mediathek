// Created: 08 Aug. 2024
package de.freese.player.demo.firstfft;

import java.io.IOException;
import java.nio.file.Path;

import javax.sound.sampled.UnsupportedAudioFileException;

import de.freese.player.fft.FFTFactory;
import de.freese.player.fft.output.Frequency;
import de.freese.player.fft.output.SpectraResult;

/**
 * @author Thomas Freese
 */
public final class FirstFFT {
    public static void main(final String[] args) throws Exception {
        final FirstFFT firstFFT = new FirstFFT();

        firstFFT.computeDefaultFFT();
    }

    private final Path sineWave600Hz;

    private FirstFFT() throws Exception {
        super();

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        sineWave600Hz = Path.of(classLoader.getResource("600hz-tone-3secs-mono.wav").toURI());
    }

    private void computeDefaultFFT() {
        // compute an FFT with default settings.
        SpectraResult result = null;

        try {
            result = FFTFactory.createFull(sineWave600Hz);
        }
        catch (IOException ex) {
            System.out.println("An I/O exception occurred when opening an input stream to the audio file");
        }
        catch (UnsupportedAudioFileException ex) {
            System.out.println("Invalid audio file");
        }

        // Print the SpectraResult to see details about the transformation and the audio file on which it was performed.
        System.out.println(result);

        // Get individual frames (sampling windows) from FFT.
        System.out.println("There are " + result.length() + " spectra in this FFT, each of which was computed from a sampling window that was about "
                + Math.round(result.getWindowDurationMs()) + " milliseconds long.");

        // Inspect amplitudes of individual frequency in the first Spectrum.
        final Frequency firstFrequency = result.getSpectrum(0).getFrequency(0);
        System.out.println("The first Frequency, located at " + Math.round(firstFrequency.getHz()) + " Hz, has an amplitude of "
                + Math.round(firstFrequency.getAmplitude()) + " dB.");

        final Frequency mostPowerfulFrequency = result.getSpectrum(0).getFrequency(56); // closest to 600 Hz
        System.out.println("The 56th Frequency, located at " + Math.round(mostPowerfulFrequency.getHz()) + " Hz, has an amplitude of "
                + Math.round(mostPowerfulFrequency.getAmplitude()) + " dB.");
    }
}
