// Created: 28 Aug. 2024
package de.freese.player.core.player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.FloatControl;

import de.freese.player.core.dsp.DspChain;
import de.freese.player.core.dsp.DspProcessor;
import de.freese.player.core.exception.PlayerException;
import de.freese.player.core.input.AudioInputStreamFactory;
import de.freese.player.core.model.Window;
import de.freese.player.core.util.PlayerUtils;

/**
 * @author Thomas Freese
 */
public final class DefaultDspPlayer extends AbstractPlayer implements DspPlayer {
    private final DspChain dspChain = new DspChain();
    private long framesRead;
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
    public void configureVolumeControl(final Consumer<FloatControl> consumer) {
        if (!isPlaying()) {
            return;
        }

        sourceDataLinePlayer.configureVolumeControl(consumer);
    }

    @Override
    public void jumpTo(final Duration duration) {
        if (!isPlaying() || duration == null) {
            return;
        }

        if (duration.toMillis() >= getAudioSource().getDuration().toMillis()) {
            return;
        }

        try {
            // final double percent = (double) duration.toMillis() / (double) getAudioSource().getDuration().toMillis();
            // final long byteIndex = (long) (Files.size(getAudioSource().getTmpFile()) * percent);

            final long byteIndex = PlayerUtils.millisToBytes(getAudioInputStream().getFormat(), duration.toMillis());
            framesRead = PlayerUtils.milliesToFrames(getAudioInputStream().getFormat(), duration.toMillis());

            getAudioInputStream().reset();
            getAudioInputStream().skip(byteIndex);
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new PlayerException(ex);
        }
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
        framesRead = 0;

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

            if (getAudioSource().getTmpFile() != null) {
                Files.delete(getAudioSource().getTmpFile());
                getAudioSource().setTmpFile(null);
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

        // final int bytesPerFrame = audioFormat.getChannels() == 1 ? 2 : 4;
        final int bytesPerFrame = audioFormat.getFrameSize();
        final int framesToRead = (int) (audioFormat.getSampleRate() / 20D); // ~ 50ms
        final int byteLength = bytesPerFrame * audioFormat.getChannels() * framesToRead;

        while (true) {
            try {
                framesRead += (long) framesToRead * audioFormat.getChannels();

                final byte[] audioBytes = new byte[byteLength];
                final int bytesRead = getAudioInputStream().read(audioBytes);
                Window window = null;

                if (bytesRead == audioBytes.length) {
                    window = new Window(audioFormat, audioBytes, framesRead, getAudioInputStream().getFrameLength());
                }
                else if (bytesRead > -1) {
                    // End of Song.
                    window = new Window(audioFormat, Arrays.copyOf(audioBytes, bytesRead), framesRead, getAudioInputStream().getFrameLength());
                    doStop = true;
                }
                else {
                    doStop = true;
                }

                if (window != null) {
                    dspChain.process(window);

                    sourceDataLinePlayer.play(window);
                }
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
