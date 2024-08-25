package de.freese.player.fft.reader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Factory to produce an appropriate subclass of {@link AudioReader} depending on whether input file is MP3 or WAV/AIFF.
 */
public final class AudioReaderFactory {
    /**
     * Produces a {@link PCMReader} if given file is WAV or AIFF, {@link MP3Reader} if MP3, throws exception otherwise.
     *
     * @throws IOException if an I/O exception arises during creation of audio reader
     * @throws UnsupportedAudioFileException if file isn't an MP3, WAV, or AIFF file
     */
    public static AudioReader of(final Path audioFile) throws IOException, UnsupportedAudioFileException {
        final String fileExtension = getFileExtension(audioFile);

        return switch (fileExtension) {
            case ".wav", ".aiff" -> PCMReader.of(new BufferedInputStream(Files.newInputStream(audioFile)), Files.size(audioFile));
            case ".mp3" -> MP3Reader.of(new BufferedInputStream(Files.newInputStream(audioFile)), Files.size(audioFile));
            default -> {
                final String msg = String.format("Cannot read file type %s; please provide a .wav, .aiff, or .mp3 file instead.", fileExtension);
                throw new UnsupportedAudioFileException(msg);
            }
        };
    }

    /**
     * Extracts file extension from end of file name
     *
     * @return extension of file, if exists (".wav", ".mp3", etc)
     *
     * @throws UnsupportedAudioFileException if file name doesn't include an extension
     */
    private static String getFileExtension(final Path file) throws UnsupportedAudioFileException {
        final String name = file.getFileName().toString();

        if (!name.contains(".")) {
            throw new UnsupportedAudioFileException();
        }

        return name.substring(name.lastIndexOf("."));
    }

    private AudioReaderFactory() {
        super();
    }
}
