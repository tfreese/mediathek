// Created: 24 Aug. 2024
package de.freese.player.core.input;

import java.net.URI;
import java.nio.file.Path;

import de.freese.player.core.ffmpeg.FFLocator;

/**
 * @author Thomas Freese
 */
public final class AudioSourceFactory {
    public static AudioSource createAudioSource(final URI uri) throws Exception {
        return FFLocator.createFFprobe().getMetaData(uri);
    }

    public static AudioSource createAudioSource(final Path path) throws Exception {
        return createAudioSource(path.toUri());
    }

    private AudioSourceFactory() {
        super();
    }
}
