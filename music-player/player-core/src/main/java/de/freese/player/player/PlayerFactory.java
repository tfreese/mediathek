// Created: 14 Juli 2024
package de.freese.player.player;

import java.io.BufferedInputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;

import de.freese.player.ffmpeg.FFLocator;
import de.freese.player.input.AudioSource;
import de.freese.player.input.FileAudioSource;
import de.freese.player.model.AudioCodec;

/**
 * [flac, m4a, m4b, mp3, wma]
 *
 * @author Thomas Freese
 */
public final class PlayerFactory {
    // au -> AU;  wav -> WAVE; aif -> AIFF
    private static final Map<String, String> DEFAULT_AUDIO_TYPES = Arrays.stream(AudioSystem.getAudioFileTypes())
            .filter(type -> !"aif".equals(type.getExtension())) // Stream of unsupported format ?
            .collect(Collectors.toMap(type -> type.getExtension().toLowerCase(), AudioFileFormat.Type::toString));

    // au -> AU;  wav -> WAVE; aif -> AIFF
    private static final Set<AudioCodec> SUPPORTED_AUDIO_CODECS = Arrays.stream(AudioSystem.getAudioFileTypes())
            .filter(type -> !"aif".equals(type.getExtension())) // Stream of unsupported format ?
            .map(type -> AudioCodec.valueOf(type.toString()))
            .collect(Collectors.toUnmodifiableSet());

    public static ClipPlayer createPlayer(final AudioSource audioSource) {
        final String fileName = audioSource.getUri().toString();
        final String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        final AudioCodec audioCodec = AudioCodec.getByExtension(fileExtension);
        final ClipPlayer player;

        if (SUPPORTED_AUDIO_CODECS.contains(audioCodec)) {
            player = new DefaultClipPlayer(() -> AudioSystem.getAudioInputStream(new BufferedInputStream(audioSource.getUri().toURL().openStream())));
        }
        else if (AudioCodec.M4B.equals(audioCodec) || AudioCodec.OGG.equals(audioCodec)) {
            player = new DefaultClipPlayer(() -> {
                final Path tmpFile = FFLocator.createFFmpeg().encodeToWav(audioSource);

                if (audioSource instanceof FileAudioSource fas) {
                    fas.setTmpFile(tmpFile);
                }

                return AudioSystem.getAudioInputStream(new BufferedInputStream(Files.newInputStream(tmpFile)));
            });
        }
        else {
            player = new DefaultClipPlayer(() -> FFLocator.createFFmpeg().toAudioStreamWav(audioSource));
        }

        return player;
    }

    public static ClipPlayer createPlayer(final URI uri) {
        // PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.java");
        //
        // if (pathMatcher.matches(file.getFileName())) {
        //     System.out.println(file);
        // }

        final String fileName = uri.toString();
        final String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        final AudioCodec audioCodec = AudioCodec.getByExtension(fileExtension);
        final ClipPlayer player;

        if (SUPPORTED_AUDIO_CODECS.contains(audioCodec)) {
            player = new DefaultClipPlayer(() -> AudioSystem.getAudioInputStream(new BufferedInputStream(uri.toURL().openStream())));
        }
        else if (AudioCodec.M4B.equals(audioCodec) || AudioCodec.OGG.equals(audioCodec)) {
            player = new DefaultClipPlayer(() -> {
                final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(uri);

                final Path tmpFile = FFLocator.createFFmpeg().encodeToWav(audioSource);

                if (audioSource instanceof FileAudioSource fas) {
                    fas.setTmpFile(tmpFile);
                }

                return AudioSystem.getAudioInputStream(new BufferedInputStream(Files.newInputStream(tmpFile)));
            });
        }
        else {
            player = new DefaultClipPlayer(() -> {
                final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(uri);
                return FFLocator.createFFmpeg().toAudioStreamWav(audioSource);
            });
        }

        return player;
    }

    private PlayerFactory() {
        super();
    }
}
