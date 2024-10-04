// Created: 14 Juli 2024
package de.freese.player.core.player;

import java.util.function.Consumer;

import javax.sound.sampled.FloatControl;

import de.freese.player.core.input.AudioSource;

/**
 * @author Thomas Freese
 */
// extends AutoCloseable
public interface Player {
    void addSongFinishedListener(Consumer<AudioSource> consumer);

    void configureVolumeControl(Consumer<FloatControl> consumer);

    boolean isPlaying();

    void pause();

    void play();

    void resume();

    void setAudioSource(AudioSource audioSource);

    void stop();
}
