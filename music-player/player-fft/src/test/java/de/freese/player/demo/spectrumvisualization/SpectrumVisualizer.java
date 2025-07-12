// Created: 08 Aug. 2024
package de.freese.player.demo.spectrumvisualization;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.fft.FFTFactory;
import de.freese.player.fft.config.FFTConfig;
import de.freese.player.fft.output.Spectrum;
import de.freese.player.fft.output.SpectrumStream;
import de.freese.player.fft.sampling.WindowFunction;

/**
 * @author Thomas Freese
 */
public final class SpectrumVisualizer {
    /**
     * Wrapper for JFreeChart line graph.
     */
    private static final LineChart LINE_CHART = new LineChart();
    private static final Logger LOGGER = LoggerFactory.getLogger(SpectrumVisualizer.class);
    private static Iterator<Spectrum> spectrumIterator;
    private static SpectrumStream spectrumStream;

    public static void main(final String[] args) {
        final SpectrumVisualizer visualizer = new SpectrumVisualizer();
        LINE_CHART.init();
        visualizer.visualizeSpectrum();
    }

    private static void graphThenComputeNextFrame() {
        if (spectrumIterator.hasNext()) {
            final Spectrum nextSpectrum = spectrumIterator.next();

            // Graph currently stored frame.
            LINE_CHART.updateChartData(nextSpectrum);
        }
        else {
            // Otherwise song has ended, so end program.
            System.exit(0);
        }
    }

    private final Path song;

    private SpectrumVisualizer() {
        super();

        // final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // song = Path.of(classLoader.getResource("600hz-tone-3secs-stereo.wav").toURI());
        // song = Path.of("samples/sample60s.wav");
        song = Path.of("samples/sample_music.wav");
    }

    private void visualizeSpectrum() {
        // Get SpectrumStream for song.
        try {
            spectrumStream = FFTFactory.createStream(song, new FFTConfig()
                    .windowSize(8192)
                    .windowOverlap(0.75D)
                    .normalized(true)
                    .decibelScale(false)
                    .windowFunction(WindowFunction.RECTANGULAR));
            spectrumIterator = spectrumStream.iterator();
        }
        catch (IOException | UnsupportedAudioFileException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        LOGGER.info("{}", spectrumStream);

        // Start playing audio.
        Thread.ofVirtual().start(() -> {
            try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(song))) {
                final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
                final DataLine.Info info = new DataLine.Info(Clip.class, audioInputStream.getFormat());
                final Clip audioClip = (Clip) AudioSystem.getLine(info);
                audioClip.open(audioInputStream);
                audioClip.start();
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        });

        // Calculate time between consecutive FFT frames.
        final double msBetweenFFTs = spectrumStream.getWindowDurationMs() * (1D - spectrumStream.getFFTConfig().getWindowOverlap());
        final long nanoTimeBetweenFFTs = Math.round(msBetweenFFTs * Math.pow(10D, 6D));

        // Begin visualization cycle.
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(SpectrumVisualizer::graphThenComputeNextFrame, 0, nanoTimeBetweenFFTs, TimeUnit.NANOSECONDS);
    }
}
