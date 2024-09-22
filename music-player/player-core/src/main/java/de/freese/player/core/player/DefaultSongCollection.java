// Created: 31 Aug. 2024
package de.freese.player.core.player;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import de.freese.player.core.input.AudioSource;
import de.freese.player.core.input.AudioSourceFactory;

/**
 * @author Thomas Freese
 */
public final class DefaultSongCollection implements SongCollection {
    private final List<AudioSource> audioSources = new ArrayList<>(1024);

    private int currentIndex;
    private Duration durationTotal = Duration.ZERO;

    public DefaultSongCollection addAudioSource(final URI uri) throws Exception {
        return addAudioSource(AudioSourceFactory.createAudioSource(uri));
    }

    public DefaultSongCollection addAudioSource(final Path path) throws Exception {
        return addAudioSource(AudioSourceFactory.createAudioSource(path));
    }

    @Override
    public DefaultSongCollection addAudioSource(final AudioSource audioSource) {
        audioSources.add(audioSource);

        durationTotal = durationTotal.plus(audioSource.getDuration());

        return this;
    }

    @Override
    public SongCollection addAudioSources(final Collection<AudioSource> audioSources) {
        audioSources.forEach(this::addAudioSource);

        return this;
    }

    @Override
    public void clear() {
        audioSources.clear();
        currentIndex = 0;
        durationTotal = Duration.ZERO;
    }

    @Override
    public AudioSource getAudioSource(final int index) {
        return audioSources.get(index);
    }

    @Override
    public int getCurrentIndex() {
        return currentIndex;
    }

    @Override
    public Duration getDurationTotal() {
        return durationTotal;
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
    public void setCurrentIndex(final int index) {
        currentIndex = index;
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
