// Created: 08 Sept. 2024
package de.freese.player.player;

import java.util.stream.Stream;

import de.freese.player.input.AudioSource;

/**
 * @author Thomas Freese
 */
public interface PlayList {
    PlayList addAudioSource(AudioSource audioSource);

    default AudioSource currentAudioSource() {
        return getAudioSource(currentIndex());
    }

    int currentIndex();

    AudioSource getAudioSource(int index);

    default boolean hasNext() {
        return currentIndex() < size() - 1;
    }

    default boolean hasPrevious() {
        return currentIndex() > 0;
    }

    int indexOf(AudioSource audioSource);

    AudioSource next();

    AudioSource previous();

    int size();

    Stream<AudioSource> stream();
}
