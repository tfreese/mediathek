package org.quifft.reader;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Audio reader to extract waveform data from PCM-formatted files (WAV and AIFF).
 */
public final class PCMReader extends AbstractAudioReader {
    public static AudioReader of(final InputStream inputStream, final long fileLength) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);

        if (audioInputStream.getFormat().getSampleSizeInBits() == 8) {
            // convert 8-bit audio into 16-bit
            audioInputStream = wrapTo16Bit(inputStream);
        }

        return new PCMReader(audioInputStream, fileLength);
    }

    private PCMReader(final AudioInputStream audioInputStream, final long fileLength) {
        super(audioInputStream, fileLength);
    }

    @Override
    public long getDurationMs() {
        final AudioFormat format = getAudioFormat();
        final int frameSize = format.getFrameSize();
        final double frameRate = format.getFrameRate();

        return (long) Math.ceil((getLength() / (frameSize * frameRate)) * 1000D);
    }
}
