// Created: 19 Juli 2024
package de.freese.player.core.ffmpeg;

import java.nio.file.Path;

import javax.sound.sampled.AudioInputStream;

import de.freese.player.core.input.AudioSource;

/**
 * ffmpeg -formats | grep PCM
 *
 * @author Thomas Freese
 */
public interface FFmpeg {
    Path encodeToWav(AudioSource audioSource, final Path tempDir);

    String getVersion();

    AudioInputStream toAudioStreamWav(AudioSource audioSource);
}
