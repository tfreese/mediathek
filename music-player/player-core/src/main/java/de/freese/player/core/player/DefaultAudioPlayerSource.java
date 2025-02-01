// Created: 01 Feb. 2025
package de.freese.player.core.player;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import de.freese.player.core.exception.PlayerException;
import de.freese.player.core.input.AudioInputStreamFactory;
import de.freese.player.core.input.AudioSource;
import de.freese.player.core.model.Window;

/**
 * @author Thomas Freese
 */
class DefaultAudioPlayerSource implements AudioPlayerSource {
    private final AudioInputStream audioInputStream;
    private final AudioSource audioSource;

    private long framesRead;

    DefaultAudioPlayerSource(final AudioSource audioSource) {
        super();

        this.audioSource = Objects.requireNonNull(audioSource, "audioSource required");

        try {
            audioInputStream = AudioInputStreamFactory.createAudioInputStream(audioSource,
                    Executors.newVirtualThreadPerTaskExecutor(),
                    Path.of(System.getProperty("java.io.tmpdir"), ".music-player"));
            // audioInputStream = AudioSystem.getAudioInputStream(DefaultAudioPlayerSink.getTargetAudioFormat(),
            //         AudioInputStreamFactory.createAudioInputStream(audioSource,
            //                 Executors.newVirtualThreadPerTaskExecutor(),
            //                 Path.of(System.getProperty("java.io.tmpdir"), ".music-player")));
        }
        catch (Exception ex) {
            throw new PlayerException(ex);
        }
    }

    @Override
    public void close() {
        try {
            if (audioInputStream != null) {
                audioInputStream.close();
            }
        }
        catch (IOException ex) {
            throw new PlayerException(ex);
        }
    }

    @Override
    public void jumpTo(final Duration duration) {
        // TODO
    }

    @Override
    public Window nextWindow() {
        final AudioFormat audioFormat = audioInputStream.getFormat();

        final int bytesPerFrame = audioFormat.getFrameSize();
        final int framesToRead = (int) (audioFormat.getSampleRate() / 20D); // ~ 50ms
        final long framesTotal = audioInputStream.getFrameLength();
        final int byteLength = bytesPerFrame * framesToRead;

        framesRead += (long) framesToRead * audioFormat.getChannels();

        Window window = null;

        try {
            if (audioInputStream.available() == 0L) {
                return null;
            }

            final byte[] audioBytes = new byte[byteLength];

            final int bytesRead = audioInputStream.read(audioBytes);

            if (bytesRead == audioBytes.length) {
                window = Window.of(audioFormat, audioBytes, framesRead, framesTotal);
            }
            else if (bytesRead > -1) {
                // End of Song.
                window = Window.of(audioFormat, Arrays.copyOf(audioBytes, bytesRead), Math.max(framesRead, framesTotal), framesTotal);
            }
        }
        catch (IOException ex) {
            throw new PlayerException(ex);
        }

        return window;
    }
}
