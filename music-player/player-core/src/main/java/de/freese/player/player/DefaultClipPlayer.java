// Created: 14 Juli 2024
package de.freese.player.player;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.PlayerSettings;
import de.freese.player.exception.PlayerException;

/**
 * @author Thomas Freese
 */
class DefaultClipPlayer implements ClipPlayer {
    private final Callable<AudioInputStream> audioInputStreamCallable;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private AudioInputStream audioInputStream;
    private Clip clip;
    private volatile boolean looping;
    private volatile boolean running;

    DefaultClipPlayer(final Callable<AudioInputStream> audioInputStreamCallable) {
        super();

        this.audioInputStreamCallable = Objects.requireNonNull(audioInputStreamCallable, "audioInputStreamCallable required");
    }

    // public void init(final AudioInputStream audioInputStream) throws Exception {
    //     this.audioInputStream = Objects.requireNonNull(audioInputStream, "audioInputStream required");
    //
    //     getLogger().debug("Play audio format: {}", audioInputStream.getFormat());
    //
    //     clip = AudioSystem.getClip();
    //     clip.open(audioInputStream);
    // }

    // @Override
    // public boolean isResumed() {
    //     return getClip().getMicrosecondPosition() > 0;
    // }
    //
    // @Override
    // public void loop() {
    //     looping = true;
    //
    //     getExecutor().execute(() -> {
    //         getLogger().debug("loop");
    //
    //         getClip().start();
    //
    //         while (looping) {
    //             getClip().loop(Clip.LOOP_CONTINUOUSLY);
    //         }
    //     });
    // }

    @Override
    public void close() throws PlayerException {
        try {
            if (clip != null) {
                stop();

                getClip().close();
                clip = null;

                audioInputStream.close();
            }
        }
        catch (PlayerException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new PlayerException(ex);
        }
    }

    @Override
    public void pause() {
        getLogger().debug("pause");

        running = false;

        if (getClip() == null) {
            return;
        }

        getClip().stop();
    }

    @Override
    public void play() {
        checkInitialized();

        running = true;
        getClip().setFramePosition(0);

        getExecutor().execute(() -> {
            getLogger().info("play");

            getClip().start();

            while (true) {
                if (getClip().getMicrosecondPosition() == getClip().getMicrosecondLength()) {
                    running = false;
                    break;
                }

                if (!running) {
                    getLogger().debug("not running");
                    break;
                }
            }
        });
    }

    @Override
    public void resume() {
        checkInitialized();

        running = true;

        getExecutor().execute(() -> {
            getLogger().debug("resume");

            getClip().start();

            while (true) {
                if (getClip().getMicrosecondPosition() == getClip().getMicrosecondLength()) {
                    running = false;
                    break;
                }

                if (!running) {
                    getLogger().debug("not running");
                    break;
                }
            }
        });
    }

    @Override
    public void stop() {
        getLogger().debug("stop");

        running = false;
        looping = false;

        if (getClip() == null) {
            return;
        }

        // Continues data line I/O until its buffer is drained.
        // getClip().drain();

        getClip().stop();

        getClip().setFramePosition(0);
    }

    protected void checkInitialized() {
        if (clip != null && audioInputStream != null) {
            return;
        }

        try {
            audioInputStream = audioInputStreamCallable.call();

            final AudioFormat audioFormat = audioInputStream.getFormat();
            getLogger().debug("Play audio format: {}", audioFormat);

            final DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
            clip = (Clip) AudioSystem.getLine(info);

            // clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new PlayerException(ex);
        }
    }

    protected Clip getClip() {
        return clip;
    }

    protected Executor getExecutor() {
        return PlayerSettings.getExecutorService();
    }

    protected Logger getLogger() {
        return logger;
    }
}
