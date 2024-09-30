// Created: 30 Sept. 2024
package de.freese.player.test;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.exception.PlayerException;

/**
 * @author Thomas Freese
 */
public final class WavHeaderDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(WavHeaderDemo.class);

    public static void main(final String[] args) {
        final Path path = Path.of("music-player/samples/sample.wav");

        try (InputStream inputStream = path.toUri().toURL().openStream()) {
            final byte[] header = new byte[44];
            final int bytesRead = inputStream.read(header);

            if (bytesRead != 44L) {
                throw new PlayerException("Could not read complete WAV-header from pipe. This could result in mis-aligned frames!");
            }

            // RIFF-Section, 12 Byte
            LOGGER.info("TYPE: {}", new String(Arrays.copyOfRange(header, 0, 4)));
            LOGGER.debug("FileSize[b]: {}", toInt(Arrays.copyOfRange(header, 4, 8), false));
            LOGGER.info("WAVE: {}", new String(Arrays.copyOfRange(header, 8, 12)));

            // FMT-Section, 24 Byte
            LOGGER.info("FMT: {}", new String(Arrays.copyOfRange(header, 12, 16)));
            LOGGER.info("FMT-Length: {}", toInt(Arrays.copyOfRange(header, 16, 20), false));
            LOGGER.info("FORMAT (1=PCM): {}", toShort(Arrays.copyOfRange(header, 20, 22), false));
            LOGGER.info("Channels: {}", toShort(Arrays.copyOfRange(header, 22, 24), false));
            LOGGER.info("SampleRate: {}", toInt(Arrays.copyOfRange(header, 24, 28), false));
            LOGGER.info("Bytes/Second: {}", toInt(Arrays.copyOfRange(header, 28, 32), false));
            LOGGER.info("FrameSize: {}", toShort(Arrays.copyOfRange(header, 32, 34), false));
            LOGGER.info("Bits/Sample: {}", toShort(Arrays.copyOfRange(header, 34, 36), false));

            // Data-Section, 8 Byte
            LOGGER.info("Signature: {}", new String(Arrays.copyOfRange(header, 36, 40)));
            LOGGER.info("Data-Length: {}", toInt(Arrays.copyOfRange(header, 40, 44), false));
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static int toInt(final byte[] value, final boolean bigEndian) {
        if (bigEndian) {
            return ((value[0] & 0xFF) << 24)
                    + ((value[1] & 0xFF) << 16)
                    + ((value[2] & 0xFF) << 8)
                    + (value[3] & 0xFF);
        }

        return ((value[3] & 0xFF) << 24)
                + ((value[2] & 0xFF) << 16)
                + ((value[1] & 0xFF) << 8)
                + (value[0] & 0xFF);
    }

    private static short toShort(final byte[] value, final boolean bigEndian) {
        if (bigEndian) {
            return (short) (((value[0] & 0xFF) << 8) + (value[1] & 0xFF));
        }

        return (short) (((value[1] & 0xFF) << 8) + (value[0] & 0xFF));
    }

    private WavHeaderDemo() {
        super();
    }
}
