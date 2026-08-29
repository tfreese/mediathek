package de.freese.player.core.ffmpeg;

import java.net.URI;

import de.freese.player.core.input.AudioSource;

/**
 * @author Thomas Freese
 * @since 19.07.2024
 */
public interface FFprobe {
    AudioSource getMetaData(URI uri);

    String getVersion();
}
