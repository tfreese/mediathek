// Created: 02 Feb. 2025
package de.freese.player.core.player;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.FloatControl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.dsp.DspChain;
import de.freese.player.core.dsp.DspProcessor;
import de.freese.player.core.exception.PlayerException;
import de.freese.player.core.input.AudioInputStreamFactory;
import de.freese.player.core.input.AudioSource;
import de.freese.player.core.model.Window;

/**
 * @author Thomas Freese
 */
public final class DefaultPlayer implements Player {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPlayer.class);

    private final AudioPlayerSink audioPlayerSink;
    private final DspChain dspChain = new DspChain();
    private final ExecutorService executorService;
    private final AtomicBoolean playing = new AtomicBoolean(false);
    private final List<Consumer<AudioSource>> songFinishedListener = new ArrayList<>();
    private final Path tempDir;

    private AudioPlayerSource audioPlayerSource;
    private AudioSource audioSource;

    public DefaultPlayer() {
        this(Executors.newVirtualThreadPerTaskExecutor(), Path.of(System.getProperty("java.io.tmpdir"), ".music-player"));
    }

    public DefaultPlayer(final ExecutorService executorService, final Path tempDir) {
        super();

        this.executorService = Objects.requireNonNull(executorService, "executorService required");
        this.tempDir = Objects.requireNonNull(tempDir, "tempDir required");

        audioPlayerSink = new DefaultAudioPlayerSink(DefaultAudioPlayerSink.getTargetAudioFormat());
    }

    @Override
    public void addProcessor(final DspProcessor processor) {
        dspChain.addProcessor(Objects.requireNonNull(processor, "processor required"));
    }

    @Override
    public void addSongFinishedListener(final Consumer<AudioSource> consumer) {
        songFinishedListener.add(Objects.requireNonNull(consumer, "consumer required"));
    }

    @Override
    public void configureVolumeControl(final Consumer<FloatControl> consumer) {
        audioPlayerSink.configureVolumeControl(consumer);
    }

    @Override
    public boolean isPlaying() {
        return playing.get();
    }

    @Override
    public void jumpTo(final Duration duration) {
        if (!isPlaying()) {
            return;
        }

        audioPlayerSource.jumpTo(duration);
    }

    @Override
    public void pause() {
        if (!isPlaying()) {
            return;
        }

        LOGGER.debug("pause: {}", audioSource);

        playing.set(false);
    }

    @Override
    public void play() {
        if (isPlaying()) {
            return;
        }

        LOGGER.debug("play: {}", audioSource);

        executorService.execute(() -> {
                    playing.set(true);

                    try {
                        while (playing.get()) {
                            final Window window = audioPlayerSource.nextWindow();

                            if (window != null) {
                                dspChain.process(window);

                                audioPlayerSink.play(window);
                            }
                            else {
                                stop();
                                fireSongFinished();

                                return;
                            }
                        }
                    }
                    // catch (RuntimeException ex) {
                    //     LOGGER.error(ex.getMessage(), ex);
                    //
                    //     stop();
                    //     fireSongFinished();
                    //
                    //     // throw ex;
                    // }
                    catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);

                        stop();
                        fireSongFinished();

                        // throw new PlayerException(ex);
                    }
                }
        );
    }

    @Override
    public void resume() {
        LOGGER.debug("resume: {}", audioSource);

        play();
    }

    @Override
    public void setAudioSource(final AudioSource audioSource) {
        if (isPlaying()) {
            throw new PlayerException("Stop player before set new AudioSource");
        }

        if (this.audioSource != null && this.audioSource.equals(audioSource)) {
            return;
        }

        if (this.audioPlayerSource != null) {
            this.audioPlayerSource.close();
            dspChain.reset();
        }

        this.audioSource = Objects.requireNonNull(audioSource, "audioSource required");

        if (audioSource.getTmpFile() == null) {
            try {
                AudioInputStreamFactory.encodeToWav(audioSource, tempDir);
            }
            // catch (PlayerException ex) {
            //     throw ex;
            // }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                // throw new RuntimeException(ex);
            }
        }

        try {
            final AudioInputStream audioInputStream = AudioInputStreamFactory.createAudioInputStream(audioSource, tempDir);

            // See Player#jumpTo
            audioInputStream.mark(Integer.MAX_VALUE);

            audioPlayerSource = new DefaultAudioPlayerSource(audioSource, audioInputStream);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void stop() {
        LOGGER.debug("stop: {}", audioSource);

        playing.set(false);

        audioPlayerSink.stop();

        dspChain.reset();
    }

    private void fireSongFinished() {
        LOGGER.debug("fire songFinished: {}", audioSource);

        songFinishedListener.forEach(consumer -> consumer.accept(audioSource));
    }
}
