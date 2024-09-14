// Created: 08 Sept. 2024
package de.freese.player.player;

import java.util.Collection;
import java.util.stream.Stream;

import de.freese.player.input.AudioSource;

/**
 * @author Thomas Freese
 */
public interface PlayList {
    PlayList addAudioSource(AudioSource audioSource);

    PlayList addAudioSources(Collection<AudioSource> audioSources);

    void clear();

    AudioSource getAudioSource(int index);

    default AudioSource getCurrentAudioSource() {
        return getAudioSource(getCurrentIndex());
    }

    int getCurrentIndex();

    default boolean hasNext() {
        return getCurrentIndex() < size() - 1;
    }

    default boolean hasPrevious() {
        return getCurrentIndex() > 0;
    }

    int indexOf(AudioSource audioSource);

    AudioSource next();

    AudioSource previous();

    void setCurrentIndex(int index);

    int size();

    Stream<AudioSource> stream();
}
