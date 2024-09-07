// Created: 31 Aug. 2024
package de.freese.player.player;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import de.freese.player.input.AudioSource;
import de.freese.player.input.AudioSourceFactory;

/**
 * @author Thomas Freese
 */
public final class PlayList {
    private final List<AudioSource> audioSources = new ArrayList<>(1024);

    private int currentIndex;

    public PlayList addAudioSource(final URI uri) throws Exception {
        return addAudioSource(AudioSourceFactory.createAudioSource(uri));
    }

    // public List<AudioSource> getAudioSources() {
    //     return Collections.unmodifiableList(audioSources);
    // }

    public PlayList addAudioSource(final Path path) throws Exception {
        return addAudioSource(AudioSourceFactory.createAudioSource(path));
    }

    public PlayList addAudioSource(final AudioSource audioSource) {
        audioSources.add(audioSource);

        return this;
    }

    public AudioSource currentAudioSource() {
        return getAudioSource(currentIndex);
    }

    public int currentIndex() {
        return currentIndex;
    }

    public AudioSource getAudioSource(final int index) {
        return audioSources.get(index);
    }

    public boolean hasNext() {
        return currentIndex < size() - 1;
    }

    public boolean hasPrevious() {
        return currentIndex > 0;
    }

    public int indexOf(final AudioSource audioSource) {
        return audioSources.indexOf(audioSource);
    }

    /**
     * @return null if no more available
     */
    public AudioSource next() {
        if (currentIndex >= size() - 1) {
            return null;
        }

        currentIndex++;

        return getAudioSource(currentIndex);
    }

    /**
     * @return null if no more available
     */
    public AudioSource previous() {
        if (currentIndex == 0) {
            return null;
        }

        currentIndex--;

        return getAudioSource(currentIndex);
    }

    public int size() {
        return audioSources.size();
    }

    public Stream<AudioSource> stream() {
        return audioSources.stream();
    }
}
