// Created: 19 Juli 2024
package de.freese.player.core.ffmpeg;

import java.net.URI;

import de.freese.player.core.input.AudioSource;

/**
 * @author Thomas Freese
 */
public interface FFprobe {
    AudioSource getMetaData(URI uri) throws Exception;

    String getVersion() throws Exception;
}
