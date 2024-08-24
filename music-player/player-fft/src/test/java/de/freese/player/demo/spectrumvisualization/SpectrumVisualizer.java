// Created: 08 Aug. 2024
package de.freese.player.demo.spectrumvisualization;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.quifft.QuiFFT;
import org.quifft.config.FFTConfig;
import org.quifft.output.FFTStream;
import org.quifft.output.Spectrum;
import org.quifft.sampling.WindowFunction;

/**
 * @author Thomas Freese
 */
public final class SpectrumVisualizer {
    /**
     * Wrapper for JFreeChart line graph
     */
    private static final LineChart LINE_CHART = new LineChart();
    /**
     * FFTStream used to compute FFT frames
     */
    private static FFTStream fftStream;

    public static void main(final String[] args) {
        final SpectrumVisualizer visualizer = new SpectrumVisualizer();
        LINE_CHART.init();
        visualizer.visualizeSpectrum();
    }

    private static void graphThenComputeNextFrame() {
        if (fftStream.hasNext()) {
            final Spectrum nextSpectrum = fftStream.next();

            // Graph currently stored frame
            LINE_CHART.updateChartData(nextSpectrum);
        }
        else { // otherwise song has ended, so end program
            System.exit(0);
        }
    }

    private final Path song;

    private SpectrumVisualizer() {
        super();

        // final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // song = Path.of(classLoader.getResource("600hz-tone-3secs-stereo.wav").toURI());
        // song = Path.of("samples/sample.wav");
        song = Path.of("samples/sample.wav");
    }

    private void visualizeSpectrum() {
        // Obtain FFTStream for song from QuiFFT
        QuiFFT quiFFT = null;

        try {
            // quiFFT = new QuiFFT(song, new FFTConfig().windowSize(8192).windowOverlap(0.75D)); // decibelScale
            quiFFT = new QuiFFT(song, new FFTConfig().windowSize(8192).windowOverlap(0.75D).normalized(true).decibelScale(false).windowFunction(WindowFunction.RECTANGULAR));

            // final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(Files.newInputStream(song)));
            // quiFFT = new QuiFFT(new DefaultAudioReader(audioInputStream),
            //         new FFTConfig().windowSize(8192).windowOverlap(0.75D).normalized(true).decibelScale(false).windowFunction(WindowFunction.RECTANGULAR));
        }
        catch (IOException | UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        }

        fftStream = quiFFT.fftStream();
        System.out.println(fftStream);

        // Start playing audio
        Thread.ofVirtual().start(() -> {
            try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(song))) {
                final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
                final DataLine.Info info = new DataLine.Info(Clip.class, audioInputStream.getFormat());
                final Clip audioClip = (Clip) AudioSystem.getLine(info);
                audioClip.open(audioInputStream);
                audioClip.start();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Calculate time between consecutive FFT frames
        final double msBetweenFFTs = fftStream.getWindowDurationMs() * (1D - fftStream.getFFTConfig().getWindowOverlap());
        final long nanoTimeBetweenFFTs = Math.round(msBetweenFFTs * Math.pow(10D, 6D));

        // Begin visualization cycle
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(SpectrumVisualizer::graphThenComputeNextFrame, 0, nanoTimeBetweenFFTs, TimeUnit.NANOSECONDS);
    }
}
