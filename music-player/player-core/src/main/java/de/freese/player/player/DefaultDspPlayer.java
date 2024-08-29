// Created: 28 Aug. 2024
package de.freese.player.player;

import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;

import de.freese.player.dsp.DspChain;
import de.freese.player.dsp.DspProcessor;
import de.freese.player.exception.PlayerException;
import de.freese.player.input.AudioInputStreamFactory;
import de.freese.player.model.Window;

/**
 * @author Thomas Freese
 */
public final class DefaultDspPlayer extends AbstractPlayer implements DspPlayer {
    private final DspChain dspChain = new DspChain();

    private volatile boolean running;

    private SourceDataLinePlayer sourceDataLinePlayer;

    @Override
    public void addProcessor(final DspProcessor processor) {
        dspChain.addProcessor(processor);
    }

    @Override
    public void pause() {
        getLogger().debug("pause: {}", getCurrentAudioSource());

        running = false;
    }

    @Override
    public void play() {
        try {
            setCurrentAudioSource(getAudioSource(getPlayerIndex()));
            setAudioInputStream(AudioInputStreamFactory.createAudioInputStream(getCurrentAudioSource()));

            final AudioFormat audioFormat = getAudioInputStream().getFormat();
            getLogger().debug("Play audio format: {}", audioFormat);

            sourceDataLinePlayer = new SourceDataLinePlayer(audioFormat);
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new PlayerException(ex);
        }

        running = true;

        getExecutor().execute(() -> {
            getLogger().info("play: {}", getCurrentAudioSource());

            streamMusic();
        });
    }

    @Override
    public void resume() {
        if (sourceDataLinePlayer == null) {
            return;
        }

        running = true;

        getExecutor().execute(() -> {
            getLogger().debug("resume: {}", getCurrentAudioSource());

            streamMusic();
        });
    }

    @Override
    public void stop() {
        getLogger().debug("stop: {}", getCurrentAudioSource());

        running = false;

        if (sourceDataLinePlayer == null) {
            return;
        }

        sourceDataLinePlayer.stop();

        close();
    }

    @Override
    protected void close() {
        try {
            if (sourceDataLinePlayer != null) {
                sourceDataLinePlayer.close();
                sourceDataLinePlayer = null;

                getAudioInputStream().close();
                setAudioInputStream(null);
                setCurrentAudioSource(null);
            }
        }
        catch (PlayerException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new PlayerException(ex);
        }
    }

    private void streamMusic() {
        final AudioFormat audioFormat = getAudioInputStream().getFormat();
        boolean doStop = false;

        while (true) {
            try {
                // final int bytesPerFrame = audioFormat.getChannels() == 1 ? 2 : 4;
                final int bytesPerFrame = audioFormat.getFrameSize();
                final int framesToRead = 1000;

                final byte[] audioBytes = new byte[bytesPerFrame * framesToRead * audioFormat.getChannels()];
                final int bytesRead = getAudioInputStream().read(audioBytes);
                final Window window;

                if (bytesRead == audioBytes.length) {
                    window = new Window(audioFormat, audioBytes);
                }
                else {
                    // End of Song.
                    window = new Window(audioFormat, Arrays.copyOf(audioBytes, bytesRead));
                    doStop = true;
                }

                dspChain.process(window);

                sourceDataLinePlayer.play(window);
            }
            catch (IOException ex) {
                // throw new PlayerException(ex);
                getLogger().error(ex.getMessage(), ex);
                running = false;
            }

            if (doStop) {
                getLogger().debug("not running: {}", getCurrentAudioSource());
                stop();
                break;
            }

            if (!running) {
                getLogger().debug("not running: {}", getCurrentAudioSource());
                break;
            }
        }
    }
}
