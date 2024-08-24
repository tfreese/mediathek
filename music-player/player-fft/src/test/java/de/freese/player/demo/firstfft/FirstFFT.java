// Created: 08 Aug. 2024
package de.freese.player.demo.firstfft;

import java.io.IOException;
import java.nio.file.Path;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.quifft.QuiFFT;
import org.quifft.output.Frequency;
import org.quifft.output.SpectraResult;
import org.quifft.output.Spectrum;

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
        // compute an FFT with QuiFFT's default settings
        SpectraResult result = null;

        try {
            final QuiFFT quiFFT = new QuiFFT(sineWave600Hz);
            result = quiFFT.fullFFT();
        }
        catch (IOException ex) {
            System.out.println("An I/O exception occurred while QuiFFT was opening an input stream to the audio file");
        }
        catch (UnsupportedAudioFileException ex) {
            System.out.println("QuiFFT was given an invalid audio file");
        }

        // print the SpectraResult to see details about the transformation and the audio file on which it was performed
        System.out.println(result);

        // get individual frames (sampling windows) from FFT
        final Spectrum[] spectra = result.getSpectra();
        System.out.println("There are " + spectra.length + " spectra in this FFT, each of which was computed from a sampling window that was about "
                + Math.round(result.getWindowDurationMs()) + " milliseconds long.");

        // inspect amplitudes of individual frequency in the first Spectrum
        final Frequency firstFrequency = spectra[0].getFrequency(0);
        System.out.println("The first Frequency, located at " + Math.round(firstFrequency.getFrequency()) + " Hz, has an amplitude of "
                + Math.round(firstFrequency.getAmplitude()) + " dB.");

        final Frequency mostPowerfulFrequency = spectra[0].getFrequency(56); // closest to 600 Hz
        System.out.println("The 56th Frequency, located at " + Math.round(mostPowerfulFrequency.getFrequency()) + " Hz, has an amplitude of "
                + Math.round(mostPowerfulFrequency.getAmplitude()) + " dB.");
    }
}
