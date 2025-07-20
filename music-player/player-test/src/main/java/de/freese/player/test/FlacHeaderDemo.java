// Created: 30 Sept. 2024
package de.freese.player.test;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.BitSet;

import org.jaudiotagger.audio.flac.FlacAudioHeader;
import org.jaudiotagger.audio.flac.FlacInfoReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.exception.PlayerException;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc9639.html">rfc</a>
 *
 * @author Thomas Freese
 */
public final class FlacHeaderDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlacHeaderDemo.class);

    public static void main(final String[] args) throws Exception {
        final Path path = Path.of("music-player/samples/sample.flac");
        // final Path path = Path.of("/mnt/mediathek/musik/ATB/Contact/ATB (Contact) - 2 - 01 - Contact.flac");

        final FlacAudioHeader flacAudioHeader = new FlacInfoReader().read(path);
        LOGGER.info(flacAudioHeader.toString());

        // org/jaudiotagger/audio/flac/metadatablock/MetadataBlockDataStreamInfo.java
        try (InputStream inputStream = path.toUri().toURL().openStream()) {
            byte[] buffer = new byte[4];
            final int bytesRead = inputStream.read(buffer);

            if (bytesRead != buffer.length) {
                throw new PlayerException("Could not read complete FLAC-header from pipe. This could result in mis-aligned frames!");
            }

            // fLaC
            LOGGER.info("fLaC: {}", new String(buffer));

            // Values expressed as u(n) represent an unsigned big-endian integer using n bits.
            // Values expressed as s(n) represent a signed big-endian integer using n bits, signed two's complement.

            // Streaminfo
            // 272 Bits = 34 Byte
            buffer = new byte[34];
            inputStream.read(buffer);

            final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.rewind();
            // byteBuffer.flip();

            final BitSet bitSet = BitSet.valueOf(byteBuffer);

            LOGGER.info("Min. Block Size u(16): {}", getUnsignedInt(bitSet.get(0, 16)));
            LOGGER.info("Max. Block Size u(16): {}", getUnsignedInt(bitSet.get(16, 32)));
            LOGGER.info("Min. Frame Size u(24): {}", getUnsignedInt(bitSet.get(32, 56)));
            LOGGER.info("Max. Frame Size u(24): {}", getUnsignedInt(bitSet.get(56, 80)));
            LOGGER.info("Sample Rate [Hz] u(20): {}", getUnsignedInt(bitSet.get(80, 100)));
            LOGGER.info("Number of channels u(3): {}", getUnsignedInt(bitSet.get(100, 103)));
            LOGGER.info("bits per sample u(5): {}", getUnsignedInt(bitSet.get(103, 108)));
            LOGGER.info("Total number of interchannel samples u(36): {}", getUnsignedInt(bitSet.get(108, 144)));
            LOGGER.info("MD5 checksum of the unencoded audio data u(128): {}", new String(bitSet.get(144, 272).toByteArray()));
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static long getUnsignedInt(final BitSet bitSet) {
        long value = 0L;

        for (int i = 0; i < bitSet.length(); ++i) {
            value += bitSet.get(i) ? (1L << i) : 0L;
        }

        return value & 0xFF;
    }

    private static long getUnsignedInt(final byte[] data) {
        if (data == null || data.length == 0) {
            return 0L;
        }

        // final String hex = "0x" + HexFormat.of().formatHex(data);
        // final Long longHex = Long.decode(hex);
        //
        // return longHex;

        long result = 0L;

        for (int i = 0; i < data.length; i++) {
            result += (long) data[i] << 8 * (data.length - 1 - i);
        }

        return result;

        // final ByteBuffer bb = ByteBuffer.wrap(data);
        // bb.order(ByteOrder.LITTLE_ENDIAN);
        //
        // return bb.getInt() & 0xffffffffL;
    }

    private FlacHeaderDemo() {
        super();
    }
}
