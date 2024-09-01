// Created: 24 Aug. 2024
package de.freese.player.input;

import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import de.freese.player.ffmpeg.FFLocator;
import de.freese.player.model.AudioCodec;

/**
 * @author Thomas Freese
 */
public final class AudioInputStreamFactory {
    // au -> AU;  wav -> WAVE; aif -> AIFF
    private static final Set<AudioCodec> SUPPORTED_AUDIO_CODECS = Arrays.stream(AudioSystem.getAudioFileTypes())
            .filter(type -> !"aif".equals(type.getExtension())) // Stream of unsupported format !?
            .map(type -> AudioCodec.valueOf(type.toString()))
            .collect(Collectors.toUnmodifiableSet());

    public static AudioInputStream createAudioInputStream(final AudioSource audioSource) throws Exception {
        // PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.java");
        //
        // if (pathMatcher.matches(file.getFileName())) {
        //     System.out.println(file);
        // }

        final String fileName = audioSource.getUri().toString();
        final String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        final AudioCodec audioCodec = AudioCodec.getByExtension(fileExtension);
        final AudioInputStream audioInputStream;

        // if (SUPPORTED_AUDIO_CODECS.contains(audioCodec)) {
        if (AudioCodec.WAVE.equals(audioCodec)) {
            return AudioSystem.getAudioInputStream(new BufferedInputStream(audioSource.getUri().toURL().openStream()));
        }
        // else if (AudioCodec.M4B.equals(audioCodec) || AudioCodec.OGG.equals(audioCodec)) {
        //     if (audioSource.getTmpFile() == null) {
        //         final Path tmpFile = FFLocator.createFFmpeg().encodeToWav(audioSource);
        //
        //         audioSource.setTmpFile(tmpFile);
        //     }
        //
        //     audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(Files.newInputStream(audioSource.getTmpFile())));
        // }
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

    private AudioInputStreamFactory() {
        super();
    }
}
