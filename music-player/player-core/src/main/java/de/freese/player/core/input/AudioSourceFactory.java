package de.freese.player.core.input;

import java.net.URI;
import java.nio.file.Path;

import de.freese.player.core.ffmpeg.FFLocator;

/**
 * @author Thomas Freese
 * @since 24.08.2024
 */
public final class AudioSourceFactory {
    public static AudioSource createAudioSource(final URI uri) {
        return FFLocator.createFFprobe().getMetaData(uri);
    }

    public static AudioSource createAudioSource(final Path path) {
        return createAudioSource(path.toUri());
    }

    private AudioSourceFactory() {
        super();
    }
}
