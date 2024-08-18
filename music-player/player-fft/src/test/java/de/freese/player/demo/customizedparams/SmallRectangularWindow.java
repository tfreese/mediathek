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
import org.quifft.sampling.WindowFunction;

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
        FFTResult fft = null;

        try {
            // create QuiFFT instance with window size of 1024 samples and overlap of 75%
            final QuiFFT quiFFT = new QuiFFT(sineWave600Hz, new FFTConfig().windowFunction(WindowFunction.RECTANGULAR).windowSize(1024).windowOverlap(0.75));
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

        // remarks about window size's effect on frequency resolution
        System.out.println();
        System.out.println("Notice that the frequency resolution has worsened from the \"First FFT\" example "
                + "because we're taking sample windows of smaller size (1024 samples instead of the default 4096).");
    }
}
