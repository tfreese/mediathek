package de.freese.player.fft.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Audio reader to extract waveform data from MP3 files.<br>
 * <strong>Depends on: </strong>MP3SPI, JLayer, and Tritonus.
 */
public final class MP3Reader extends AbstractAudioReader {
    public static AudioReader of(final InputStream inputStream, final long fileLength) throws UnsupportedAudioFileException, IOException {
        final AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(inputStream);

        // convert 8-bit audio into 16-bit
        final AudioInputStream audioInputStream = wrapTo16Bit(inputStream);

        return new MP3Reader(audioInputStream, audioFileFormat, fileLength);
    }

    /**
     * Format of MP3 file, used to compute duration.
     */
    private final AudioFileFormat audioFileFormat;

    private MP3Reader(final AudioInputStream audioInputStream, final AudioFileFormat audioFileFormat, final long fileLength) {
        super(audioInputStream, fileLength);

        this.audioFileFormat = audioFileFormat;
    }

    @Override
    public long getDurationMs() {
        final Map<String, Object> properties = audioFileFormat.properties();
        final long microseconds = (Long) properties.getOrDefault("duration", 0L);

        if (microseconds == 0L) {
            return 0L;
        }

        return (long) (microseconds / 1000D);
    }
}
