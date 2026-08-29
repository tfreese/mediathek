package de.freese.player.core.ffmpeg;

import java.nio.file.Path;

import javax.sound.sampled.AudioInputStream;

import de.freese.player.core.input.AudioSource;

/**
 * ffmpeg -formats | grep PCM
 *
 * @author Thomas Freese
 * @since 19.07.2024
 */
public interface FFmpeg {
    Path encodeToWav(AudioSource audioSource, Path tempDir);

    String getVersion();

    AudioInputStream toAudioStreamWav(AudioSource audioSource);
}
