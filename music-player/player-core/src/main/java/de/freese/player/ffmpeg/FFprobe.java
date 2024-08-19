// Created: 19 Juli 2024
package de.freese.player.ffmpeg;

import java.net.URI;

import de.freese.player.input.AudioSource;

/**
 * @author Thomas Freese
 */
public interface FFprobe {
    AudioSource getMetaData(URI uri) throws Exception;

    String getVersion() throws Exception;
}
