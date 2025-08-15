// Created: 29 Jan. 2025
package de.freese.player.core.player;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import de.freese.player.core.exception.PlayerException;
import de.freese.player.core.input.AudioInputStreamFactory;
import de.freese.player.core.input.AudioSource;
import de.freese.player.core.input.AudioSourceFactory;
import de.freese.player.core.model.Window;

/**
 * @author Thomas Freese
 */
public interface AudioPlayerSource {

    static AudioPlayerSource of(final Path path, final Path tempDir) {
        return of(path.toUri(), tempDir);
    }

    static AudioPlayerSource of(final URI uri, final Path tempDir) {
        try {
            return of(AudioSourceFactory.createAudioSource(uri), tempDir);
        }
        catch (Exception ex) {
            throw new PlayerException(ex);
        }
    }

    static AudioPlayerSource of(final AudioSource audioSource, final Path tempDir) throws Exception {
        final AudioInputStream audioInputStream = AudioInputStreamFactory.createAudioInputStream(audioSource, tempDir);
        // final AudioInputStream  audioInputStream = AudioSystem.getAudioInputStream(DefaultAudioPlayerSink.getTargetAudioFormat(),

        // See Player#jumpTo
        audioInputStream.mark(Integer.MAX_VALUE);

        return new DefaultAudioPlayerSource(audioSource, audioInputStream);
    }

    void close();

    AudioFormat getAudioFormat();

    void jumpTo(Duration duration);

    Window nextWindow();
}
