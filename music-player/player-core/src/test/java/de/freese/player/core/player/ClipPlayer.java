// Created: 14 Juli 2024
package de.freese.player.core.player;

import java.util.concurrent.Executor;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import de.freese.player.core.exception.PlayerException;

/**
 * @author Thomas Freese
 */
public final class ClipPlayer {

    private Clip clip;
    private volatile boolean running;

    public void pause() {
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

    public void play(final AudioInputStream audioInputStream, final Executor executor) {
        try {
            final AudioFormat audioFormat = audioInputStream.getFormat();

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

        running = true;
        clip.setFramePosition(0);

        executor.execute(() -> {
            clip.start();

            while (true) {
                if (clip.getMicrosecondPosition() == clip.getMicrosecondLength()) {
                    running = false;
                    stop();
                    break;
                }

                if (!running) {
                    break;
                }
            }
        });
    }

    public void resume(final Executor executor) {
        if (clip == null) {
            return;
        }

        running = true;

        executor.execute(() -> {
            clip.start();

            while (true) {
                if (clip.getMicrosecondPosition() == clip.getMicrosecondLength()) {
                    running = false;
                    stop();
                    break;
                }

                if (!running) {
                    break;
                }
            }
        });
    }

    public void stop() {
        running = false;

        if (clip == null) {
            return;
        }

        // Continues data line I/O until its buffer is drained.
        // clip.drain();

        clip.stop();

        clip.setFramePosition(0);

        close();
    }

    private void close() {
        try {
            if (clip != null) {
                clip.close();
                clip = null;
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
