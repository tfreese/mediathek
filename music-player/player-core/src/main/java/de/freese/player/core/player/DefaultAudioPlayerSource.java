// Created: 01 Feb. 2025
package de.freese.player.core.player;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import de.freese.player.core.exception.PlayerException;
import de.freese.player.core.input.AudioSource;
import de.freese.player.core.model.Window;
import de.freese.player.core.util.PlayerUtils;

/**
 * @author Thomas Freese
 */
final class DefaultAudioPlayerSource implements AudioPlayerSource {
    private final AudioInputStream audioInputStream;
    private final AudioSource audioSource;

    private long framesRead;

    DefaultAudioPlayerSource(final AudioSource audioSource, final AudioInputStream audioInputStream) {
        super();

        this.audioSource = Objects.requireNonNull(audioSource, "audioSource required");
        this.audioInputStream = Objects.requireNonNull(audioInputStream, "audioInputStream required");
    }

    @Override
    public void close() {
        try {
            framesRead = 0L;

            audioInputStream.close();
        }
        catch (IOException ex) {
            throw new PlayerException(ex);
        }
    }

    @Override
    public AudioFormat getAudioFormat() {
        return audioInputStream.getFormat();
    }

    @Override
    public void jumpTo(final Duration duration) {
        if (duration.toMillis() >= audioSource.getDuration().toMillis()) {
            return;
        }

        final AudioFormat audioFormat = getAudioFormat();

        try {
            // final double percent = (double) duration.toMillis() / (double) audioSource.getDuration().toMillis();
            // final long byteIndex = (long) (Files.size(audioSource.getTmpFile()) * percent);

            final long byteIndex = PlayerUtils.millisToBytes(audioFormat, duration.toMillis());
            framesRead = PlayerUtils.milliesToFrames(audioFormat, duration.toMillis());

            audioInputStream.reset();
            audioInputStream.skip(byteIndex);
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new PlayerException(ex);
        }
    }

    @Override
    public Window nextWindow() {
        final AudioFormat audioFormat = getAudioFormat();

        final int bytesPerFrame = audioFormat.getFrameSize();
        final int framesToRead = (int) (audioFormat.getSampleRate() / 20D); // ~ 50ms
        final long framesTotal = audioInputStream.getFrameLength();
        final int byteLength = bytesPerFrame * framesToRead;

        framesRead += (long) framesToRead * audioFormat.getChannels();

        Window window = null;

        try {
            if (framesRead >= framesTotal || audioInputStream.available() == 0L) {
                return null;
            }

            final byte[] audioBytes = new byte[byteLength];

            final int bytesRead = audioInputStream.read(audioBytes);

            if (bytesRead == audioBytes.length) {
                window = Window.of(audioFormat, audioBytes, framesRead, framesTotal);
            }
            else if (bytesRead > -1) {
                // End of Song.
                window = Window.of(audioFormat, Arrays.copyOf(audioBytes, bytesRead), Math.min(framesRead, framesTotal), framesTotal);
            }
        }
        catch (IOException ex) {
            throw new PlayerException(ex);
        }

        return window;
    }
}
