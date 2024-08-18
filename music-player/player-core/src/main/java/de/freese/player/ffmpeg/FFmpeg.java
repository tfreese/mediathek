// Created: 19 Juli 2024
package de.freese.player.ffmpeg;

import java.nio.file.Path;

import javax.sound.sampled.AudioInputStream;

import de.freese.player.input.AudioFile;
import de.freese.player.input.AudioSource;

/**
 * @author Thomas Freese
 */
public interface FFmpeg {
    Path encodeToWav(AudioSource audioSource) throws Exception;

    String getVersion() throws Exception;

    AudioInputStream toAudioStreamWav(AudioFile audioFile) throws Exception;
}
