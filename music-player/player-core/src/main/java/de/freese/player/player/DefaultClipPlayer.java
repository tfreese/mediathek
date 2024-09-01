// Created: 14 Juli 2024
package de.freese.player.player;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import de.freese.player.exception.PlayerException;
import de.freese.player.input.AudioInputStreamFactory;
import de.freese.player.input.AudioSource;

/**
 * @author Thomas Freese
 */
public final class DefaultClipPlayer extends AbstractPlayer {

    private Clip clip;
    private volatile boolean looping;
    private volatile boolean running;

    public DefaultClipPlayer() {
        super();
    }

    public DefaultClipPlayer(final AudioSource audioSource) {
        super();

        setAudioSource(audioSource);
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
            setAudioInputStream(AudioInputStreamFactory.createAudioInputStream(getAudioSource()));

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

        firePlay();

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

        fireStop();

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
