// Created: 14 Juli 2024
package de.freese.player.player;

import java.util.function.Consumer;

import de.freese.player.input.AudioSource;

/**
 * @author Thomas Freese
 */
// extends AutoCloseable
public interface Player {
    void addSongFinishedListener(Consumer<AudioSource> consumer);

    boolean isPlaying();

    void pause();

    void play();

    void resume();

    void setAudioSource(AudioSource audioSource);

    void stop();
}
