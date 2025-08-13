// Created: 30 Sept. 2024
package de.freese.player.test.flac;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.flac.FlacAudioHeader;
import org.jaudiotagger.audio.flac.FlacInfoReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc9639.html">rfc</a>
 *
 * @author Thomas Freese
 */
public final class FlacHeaderDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlacHeaderDemo.class);

    static void main(final String[] args) throws Exception {
        final AudioFile audioFile = AudioFileIO.read(Path.of("music-player/samples/sample.m4b").toFile());
        LOGGER.info("{}", audioFile);

        final Path path = Path.of("music-player/samples/sample.flac");

        final FlacAudioHeader flacAudioHeader = new FlacInfoReader().read(path);
        LOGGER.info("{} - MD5={}", flacAudioHeader, flacAudioHeader.getMd5());

        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
            if (fileChannel.size() == 0L) {
                throw new IllegalArgumentException("File is empty: " + path);
            }

            readFileHeader(fileChannel);
            findStreamInfoBlock(fileChannel);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * <a href="https://www.rfc-editor.org/rfc/rfc9639.html#name-file-level-metadata">file-level-metadata</a>
     */
    private static void findStreamInfoBlock(final FileChannel fileChannel) throws Exception {
        boolean isLastBlock = false;
        MetadataBlockDataStreamInfo mbdsi = null;

        // Search for StreamInfo Block, but even after we found it we still have to continue through all
        // the metadata blocks so that we can find the start of the audio frames which we need to calculate
        // the bitrate.
        while (!isLastBlock) {
            final MetadataBlockHeader mbh = MetadataBlockHeader.readHeader(fileChannel);
            LOGGER.info("{}", mbh);

            if (mbh.getBlockType() == BlockType.STREAMINFO) {
                if (mbh.getDataLength() == 0) {
                    throw new IllegalArgumentException("FLAC StreamInfo has zero data length");
                }

                mbdsi = new MetadataBlockDataStreamInfo(mbh, fileChannel);

                if (!mbdsi.isValid()) {
                    throw new IllegalArgumentException(":FLAC StreamInfo not valid");
                }
            }
            else {
                fileChannel.position(fileChannel.position() + mbh.getDataLength());
            }

            isLastBlock = mbh.isLastBlock();
        }

        // Audio continues from this point to end of file (normally - might need to allow for an ID3v1 tag at file end ?).
        final long streamStart = fileChannel.position();

        if (mbdsi == null) {
            throw new IllegalArgumentException("Unable to find Flac StreamInfo");
        }

        LOGGER.info("Min. Block Size: {}", mbdsi.getMinBlockSize());
        LOGGER.info("Max. Block Size: {}", mbdsi.getMaxBlockSize());
        LOGGER.info("Min. Frame Size: {}", mbdsi.getMinFrameSize());
        LOGGER.info("Max. Frame Size: {}", mbdsi.getMaxFrameSize());
        LOGGER.info("Sample Rate [Hz]: {}", mbdsi.getSamplingRate());
        LOGGER.info("Sample Rate per Channel[Hz]: {}", mbdsi.getSamplingRatePerChannel());
        LOGGER.info("Number of channels: {}", mbdsi.getNoOfChannels());
        LOGGER.info("Number of samples: {}", mbdsi.getNoOfSamples());
        LOGGER.info("Bits per sample: {}", mbdsi.getBitsPerSample());
        LOGGER.info("TrackLength: {}", mbdsi.getTrackLength());
        LOGGER.info("AudioDataStartPosition: {}", streamStart);

        final long audioDataLength = fileChannel.size() - streamStart;
        LOGGER.info("AudioDataLength: {}", audioDataLength);
        LOGGER.info("AudioDataEndPosition: {}", fileChannel.size());

        // KILOBYTE_MULTIPLIER = 1000
        // BITS_IN_BYTE_MULTIPLIER = 8
        LOGGER.info("Bitrate: {}", (int) ((audioDataLength / 1000D) * 8D / mbdsi.getTrackLength()));
        LOGGER.info("EncodingType: FLAC {} bits", mbdsi.getBitsPerSample());
        LOGGER.info("MD5: {}", mbdsi.getMd5());
    }

    /**
     * <a href="https://www.rfc-editor.org/rfc/rfc9639.html#name-signature-and-streaminfo">signature-and-streaminfo</a>
     */
    private static void readFileHeader(final FileChannel fileChannel) throws IOException {
        fileChannel.position(0L);

        final ByteBuffer tagBuffer = ByteBuffer.allocate(4);
        fileChannel.read(tagBuffer);
        tagBuffer.position(0);
        tagBuffer.order(ByteOrder.BIG_ENDIAN);

        final byte[] buffer = new byte[4];
        tagBuffer.get(buffer);

        // fLaC
        LOGGER.info("Signature: {}", new String(buffer, StandardCharsets.UTF_8));
    }

    private FlacHeaderDemo() {
        super();
    }
}
