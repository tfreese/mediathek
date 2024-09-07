// Created: 24 Aug. 2024
package de.freese.player.input;

import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import de.freese.player.ffmpeg.FFLocator;
import de.freese.player.model.AudioCodec;

/**
 * @author Thomas Freese
 */
public final class AudioInputStreamFactory {
    // au -> AU;  wav -> WAVE; aif -> AIFF
    // private static final Set<AudioCodec> SUPPORTED_AUDIO_CODECS = Arrays.stream(AudioSystem.getAudioFileTypes())
    //         .filter(type -> !"aif".equals(type.getExtension())) // Stream of unsupported format !?
    //         .map(type -> AudioCodec.valueOf(type.toString()))
    //         .collect(Collectors.toUnmodifiableSet());

    public static AudioInputStream createAudioInputStream(final AudioSource audioSource) throws Exception {
        final AudioCodec audioCodec = audioSource.getAudioCodec();
        final AudioInputStream audioInputStream;

        if (AudioCodec.WAVE.equals(audioCodec)) {
            return AudioSystem.getAudioInputStream(new BufferedInputStream(audioSource.getUri().toURL().openStream()));
        }
        else {
            if (audioSource.getTmpFile() == null) {
                final Path tmpFile = FFLocator.createFFmpeg().encodeToWav(audioSource);

                audioSource.setTmpFile(tmpFile);
            }

            audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(Files.newInputStream(audioSource.getTmpFile())));

            // Results in Clicker-Sounds during playing.
            // audioInputStream = FFLocator.createFFmpeg().toAudioStreamWav(audioSource);
        }

        if (audioInputStream == null) {
            throw new IllegalArgumentException("can not create AudioInputStream from " + audioSource);
        }

        return audioInputStream;
    }

    public static void encodeToWav(final AudioSource audioSource) throws Exception {
        final AudioCodec audioCodec = audioSource.getAudioCodec();

        if (AudioCodec.WAVE.equals(audioCodec)) {
            return;
        }

        if (audioSource.getTmpFile() == null) {
            final Path tmpFile = FFLocator.createFFmpeg().encodeToWav(audioSource);

            audioSource.setTmpFile(tmpFile);
        }
    }

    private AudioInputStreamFactory() {
        super();
    }
}
