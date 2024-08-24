// Created: 14 Juli 2024
package de.freese.player.player;

import java.net.URI;
import java.nio.file.Path;

import de.freese.player.input.AudioSource;
import de.freese.player.input.AudioSourceFactory;

/**
 * @author Thomas Freese
 */
// extends AutoCloseable
public interface Player {
    default Player addAudioSource(final URI uri) throws Exception {
        return addAudioSource(AudioSourceFactory.createAudioSource(uri));
    }

    default Player addAudioSource(final Path path) throws Exception {
        return addAudioSource(AudioSourceFactory.createAudioSource(path));
    }

    Player addAudioSource(AudioSource audioSource);

    void backward();

    void forward();

    void pause();

    void play();

    void resume();

    void stop();
}
