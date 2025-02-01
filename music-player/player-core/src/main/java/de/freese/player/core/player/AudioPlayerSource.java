// Created: 29 Jan. 2025
package de.freese.player.core.player;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;

import de.freese.player.core.exception.PlayerException;
import de.freese.player.core.input.AudioSource;
import de.freese.player.core.input.AudioSourceFactory;
import de.freese.player.core.model.Window;

/**
 * @author Thomas Freese
 */
public interface AudioPlayerSource {

    static AudioPlayerSource of(final Path path) {
        return of(path.toUri());
    }

    static AudioPlayerSource of(final URI uri) {
        try {
            return of(AudioSourceFactory.createAudioSource(uri));
        }
        catch (Exception ex) {
            throw new PlayerException(ex);
        }
    }

    static AudioPlayerSource of(final AudioSource audioSource) {
        return new DefaultAudioPlayerSource(audioSource);
    }

    void close();

    void jumpTo(final Duration duration);

    Window nextWindow();
}
