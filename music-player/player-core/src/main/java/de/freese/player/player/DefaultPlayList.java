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
public final class DefaultPlayList implements PlayList {
    private final List<AudioSource> audioSources = new ArrayList<>(1024);

    private int currentIndex;

    public DefaultPlayList addAudioSource(final URI uri) throws Exception {
        return addAudioSource(AudioSourceFactory.createAudioSource(uri));
    }

    public DefaultPlayList addAudioSource(final Path path) throws Exception {
        return addAudioSource(AudioSourceFactory.createAudioSource(path));
    }

    @Override
    public DefaultPlayList addAudioSource(final AudioSource audioSource) {
        audioSources.add(audioSource);

        return this;
    }

    @Override
    public int currentIndex() {
        return currentIndex;
    }

    @Override
    public AudioSource getAudioSource(final int index) {
        return audioSources.get(index);
    }

    @Override
    public int indexOf(final AudioSource audioSource) {
        return audioSources.indexOf(audioSource);
    }

    /**
     * @return null if no more available
     */
    @Override
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
    @Override
    public AudioSource previous() {
        if (currentIndex == 0) {
            return null;
        }

        currentIndex--;

        return getAudioSource(currentIndex);
    }

    @Override
    public int size() {
        return audioSources.size();
    }

    @Override
    public Stream<AudioSource> stream() {
        return audioSources.stream();
    }
}
