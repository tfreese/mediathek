// Created: 28 Aug. 2024
package de.freese.player.core.player;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Executor;

import javax.sound.sampled.AudioFormat;

import de.freese.player.core.dsp.DspChain;
import de.freese.player.core.dsp.DspProcessor;
import de.freese.player.core.exception.PlayerException;
import de.freese.player.core.input.AudioInputStreamFactory;
import de.freese.player.core.model.Window;

/**
 * @author Thomas Freese
 */
public final class DefaultDspPlayer extends AbstractPlayer implements DspPlayer {
    private final DspChain dspChain = new DspChain();

    private volatile boolean running;

    private SourceDataLinePlayer sourceDataLinePlayer;

    public DefaultDspPlayer(final Executor executor, final Path tempDir) {
        super(executor, tempDir);
    }

    @Override
    public void addProcessor(final DspProcessor processor) {
        dspChain.addProcessor(processor);
    }

    @Override
    public void pause() {
        getLogger().debug("pause: {}", getAudioSource());

        running = false;
    }

    @Override
    public void play() {
        try {
            setAudioInputStream(AudioInputStreamFactory.createAudioInputStream(getAudioSource(), getExecutor(), getTempDir()));

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
            getLogger().info("play: {}", getAudioSource());

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
            getLogger().debug("resume: {}", getAudioSource());

            streamMusic();
        });
    }

    @Override
    public void stop() {
        getLogger().debug("stop: {}", getAudioSource());

        running = false;

        if (sourceDataLinePlayer == null) {
            return;
        }

        sourceDataLinePlayer.stop();

        close();
    }

    @Override
    protected void close() {
        getLogger().debug("close: {}", getAudioSource());

        try {
            if (sourceDataLinePlayer != null) {
                sourceDataLinePlayer.close();
                sourceDataLinePlayer = null;

                getAudioInputStream().close();
                setAudioInputStream(null);

                dspChain.reset();
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
                getLogger().debug("not running: {}", getAudioSource());
                stop();
                fireSongFinished();
                break;
            }

            if (!running) {
                getLogger().debug("not running: {}", getAudioSource());
                break;
            }
        }
    }
}
