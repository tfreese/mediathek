// Created: 14 Juli 2024
package de.freese.player.model;

import java.util.Objects;

import javax.sound.sampled.AudioFileFormat;

/**
 * @author Thomas Freese
 * @see AudioFileFormat.Type
 */
public enum AudioCodec {
    AIFF("aif"),
    AU("au"),
    FLAC("flac"),
    M4A("m4a"),
    M4B("m4b"),
    // MIDI("midi"),
    MP3("mp3"),
    OGG("ogg"),
    WAVE("wav"),
    WMA("wma");

    public static AudioCodec getByExtension(final String fileExtension) {
        final String fe = Objects.requireNonNull(fileExtension, "fileExtension required").toLowerCase();

        if ("aiff".equals(fe)) {
            return AIFF;
        }

        for (AudioCodec audioCodec : values()) {
            if (audioCodec.getFileExtension().equals(fe)) {
                return audioCodec;
            }
        }

        throw new IllegalArgumentException("unsupported extension: " + fileExtension);
    }

    private final String fileExtension;

    AudioCodec(final String fileExtension) {
        this.fileExtension = Objects.requireNonNull(fileExtension, "fileExtension required");
    }

    public String getFileExtension() {
        return fileExtension;
    }
}