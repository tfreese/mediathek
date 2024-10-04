// Created: 14 Juli 2024
package de.freese.player.core.player;

import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;

import de.freese.player.core.exception.PlayerException;
import de.freese.player.core.input.AudioInputStreamFactory;

/**
 * @author Thomas Freese
 */
public final class DefaultClipPlayer extends AbstractPlayer {

    private Clip clip;
    private volatile boolean looping;
    private volatile boolean running;

    public DefaultClipPlayer(final Executor executor, final Path tempDir) {
        super(executor, tempDir);
    }

    @Override
    public void configureVolumeControl(final Consumer<FloatControl> consumer) {
        if (!isPlaying()) {
            return;
        }

        if (clip.isOpen() && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            consumer.accept((FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN));
        }
    }

    @Override
    public void pause() {
        getLogger().debug("pause: {}", getAudioSource());

        running = false;

        if (clip == null) {
            return;
        }

        clip.stop();
    }

    // @Override
    // public boolean isResumed() {
    //     return clip.getMicrosecondPosition() > 0;
    // }
    //
    // @Override
    // public void loop() {
    //     looping = true;
    //
    //     getExecutor().execute(() -> {
    //         getLogger().debug("loop");
    //
    //         clip.start();
    //
    //         while (looping) {
    //             getClip().loop(Clip.LOOP_CONTINUOUSLY);
    //         }
    //     });
    // }

    @Override
    public void play() {
        try {
            setAudioInputStream(AudioInputStreamFactory.createAudioInputStream(getAudioSource(), getExecutor(), getTempDir()));

            final AudioFormat audioFormat = getAudioInputStream().getFormat();
            getLogger().debug("Play audio format: {}", audioFormat);

            final DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
            clip = (Clip) AudioSystem.getLine(info);
            // clip = AudioSystem.getClip();

            clip.open(getAudioInputStream());
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new PlayerException(ex);
        }

        running = true;
        clip.setFramePosition(0);

        getExecutor().execute(() -> {
            getLogger().info("play: {}", getAudioSource());

            clip.start();

            while (true) {
                if (clip.getMicrosecondPosition() == clip.getMicrosecondLength()) {
                    running = false;
                    stop();
                    break;
                }

                if (!running) {
                    getLogger().debug("not running: {}", getAudioSource());
                    break;
                }
            }
        });
    }

    @Override
    public void resume() {
        if (clip == null) {
            return;
        }

        running = true;

        getExecutor().execute(() -> {
            getLogger().debug("resume: {}", getAudioSource());

            clip.start();

            while (true) {
                if (clip.getMicrosecondPosition() == clip.getMicrosecondLength()) {
                    running = false;
                    stop();
                    fireSongFinished();
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
        getLogger().debug("stop: {}", getAudioSource());

        running = false;
        looping = false;

        if (clip == null) {
            return;
        }

        // Continues data line I/O until its buffer is drained.
        // clip.drain();

        clip.stop();

        clip.setFramePosition(0);

        close();
    }

    protected void close() {
        getLogger().debug("close: {}", getAudioSource());

        try {
            if (clip != null) {
                clip.close();
                clip = null;

                getAudioInputStream().close();
                setAudioInputStream(null);
            }
        }
        catch (PlayerException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new PlayerException(ex);
        }
    }
}
