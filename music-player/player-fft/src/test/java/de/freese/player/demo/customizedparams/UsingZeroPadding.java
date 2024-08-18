// Created: 08 Aug. 2024
package de.freese.player.demo.customizedparams;

import java.io.IOException;
import java.nio.file.Path;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.quifft.QuiFFT;
import org.quifft.config.FFTConfig;
import org.quifft.output.FFTFrame;
import org.quifft.output.FFTResult;
import org.quifft.output.Frequency;

/**
 * @author Thomas Freese
 */
public final class UsingZeroPadding {
    public static void main(final String[] args) throws Exception {
        final UsingZeroPadding normalizedFFT = new UsingZeroPadding();
        normalizedFFT.computeFFTWithCustomizedParameters();
    }

    private final Path sineWave600Hz;

    private UsingZeroPadding() throws Exception {
        super();

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        sineWave600Hz = Path.of(classLoader.getResource("600hz-tone-3secs-mono.wav").toURI());
    }

    private void computeFFTWithCustomizedParameters() {
        // compute an FFT with customized settings
        FFTResult fft = null;

        try {
            // create QuiFFT instance with window sizes of 1000 and num points of 1024
            // (each window will be padded by 1024 - 1000 = 24 zeroes)
            final QuiFFT quiFFT = new QuiFFT(sineWave600Hz, new FFTConfig().windowSize(1000).numPoints(1024));
            fft = quiFFT.fullFFT();
        }
        catch (IOException ex) {
            System.out.println("An I/O exception occurred while QuiFFT was opening an input stream to the audio file");
        }
        catch (UnsupportedAudioFileException ex) {
            System.out.println("QuiFFT was given an invalid audio file");
        }

        // print the FFTResult to see details about the transformation and the audio file on which it was performed
        System.out.println(fft);

        // get individual frames (sampling windows) from FFT
        final FFTFrame[] fftFrames = fft.getFFTFrames();
        System.out.println("There are " + fftFrames.length + " frames in this FFT, each of which was computed from a sampling window that was about "
                + Math.round(fft.getWindowDurationMs()) + " milliseconds long.");

        // inspect amplitudes of individual frequency in the first frame
        final Frequency firstFrequency = fftFrames[0].getFrequencies()[0];
        System.out.println("The first Frequency, located at " + Math.round(firstFrequency.getFrequency()) + " Hz, has an amplitude of "
                + Math.round(firstFrequency.getAmplitude()) + " dB.");

        final Frequency mostPowerfulFrequency = fftFrames[0].getFrequencies()[14]; // closest to 600 Hz
        System.out.println("The 14th Frequency, located at " + Math.round(mostPowerfulFrequency.getFrequency()) + " Hz, has an amplitude of "
                + Math.round(mostPowerfulFrequency.getAmplitude()) + " dB.");
    }
}
