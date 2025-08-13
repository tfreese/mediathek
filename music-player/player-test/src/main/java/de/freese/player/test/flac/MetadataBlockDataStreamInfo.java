// Created: 13 Aug. 2025
package de.freese.player.test.flac;

import static de.freese.player.test.Unsigned.toIntFromByte;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.HexFormat;
import java.util.StringJoiner;

import de.freese.player.test.Unsigned;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc9639.html#name-streaminfo">streaminfo</a>
 *
 * @author Thomas Freese
 * @see org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataStreamInfo
 */
public final class MetadataBlockDataStreamInfo {
    private static final int STREAM_INFO_DATA_LENGTH = 34;

    /**
     * Stored in last bit of byte 12 and first 4 bits of byte 13.
     */
    private static int readBitsPerSample(final ByteBuffer byteBuffer) {
        return ((toIntFromByte(byteBuffer.get(12)) & 0x01) << 4)
                + ((toIntFromByte(byteBuffer.get(13)) & 0xF0) >>> 4) + 1;
    }

    /**
     * The last 16 Bytes.
     */
    private static String readMd5(final ByteBuffer byteBuffer) {
        if (byteBuffer.limit() >= 34) {
            // final char[] hexArray = "0123456789abcdef".toCharArray();
            //
            // final char[] hexChars = new char[32]; // MD5 is always 32 characters.
            //
            // if (byteBuffer.limit() >= 34) {
            //     for (int i = 0; i < 16; i++) {
            //         final int v = byteBuffer.get(i + 18) & 0xFF; // Offset 18
            //         hexChars[i * 2] = hexArray[v >>> 4];
            //         hexChars[i * 2 + 1] = hexArray[v & 0x0F];
            //     }
            // }
            //
            // return new String(hexChars);

            // Offset: 34 -16
            byteBuffer.position(18);

            final byte[] buffer = new byte[16];
            byteBuffer.get(buffer);

            return HexFormat.of().formatHex(buffer);
        }

        return "";
    }

    /**
     * Stored in 5th to 7th bits of byte 12.
     */
    private static int readNoOfChannels(final ByteBuffer byteBuffer) {
        return ((toIntFromByte(byteBuffer.get(12)) & 0x0E) >>> 1) + 1;
    }

    /**
     * Sampling rate is stored over 20 bits bytes 10 and 11 and half of bytes 12 so have to mask third one.
     */
    private static int readSamplingRate(final ByteBuffer byteBuffer) {
        return (toIntFromByte(byteBuffer.get(10)) << 12)
                + (toIntFromByte(byteBuffer.get(11)) << 4)
                + ((toIntFromByte(byteBuffer.get(12)) & 0xF0) >>> 4);
    }

    /**
     * Stored in second half of byte 13 plus bytes 14 - 17.
     */
    private static int readTotalNumberOfSamples(final ByteBuffer byteBuffer) {
        int nb = toIntFromByte(byteBuffer.get(17));
        nb += toIntFromByte(byteBuffer.get(16)) << 8;
        nb += toIntFromByte(byteBuffer.get(15)) << 16;
        nb += toIntFromByte(byteBuffer.get(14)) << 24;
        nb += toIntFromByte(byteBuffer.get(13)) & 0x0F;
        // nb += (toIntFromByte(byteBuffer.get(13)) & 0x0F) << 32;

        return nb;
    }

    private final int bitsPerSample;
    private final int maxBlockSize;
    private final int maxFrameSize;
    private final String md5;
    private final int minBlockSize;
    private final int minFrameSize;
    private final int noOfChannels;
    private final int noOfSamples;
    private final int samplingRate;
    private final int samplingRatePerChannel;
    private final double trackLength;
    private boolean isValid = true;

    public MetadataBlockDataStreamInfo(final MetadataBlockHeader header, final FileChannel fileChannel) throws Exception {
        super();

        if (header.getDataLength() < STREAM_INFO_DATA_LENGTH) {
            isValid = false;
            throw new IllegalArgumentException("MetadataBlockDataStreamInfo HeaderDataSize is invalid: " + header.getDataLength());
        }

        final ByteBuffer byteBuffer = ByteBuffer.allocate(header.getDataLength());
        byteBuffer.order(ByteOrder.BIG_ENDIAN);

        final int bytesRead = fileChannel.read(byteBuffer);

        if (bytesRead < header.getDataLength()) {
            isValid = false;
            throw new IllegalArgumentException("Unable to read required number of bytes, read:" + bytesRead + ":required:" + header.getDataLength());
        }

        byteBuffer.flip();

        minBlockSize = Unsigned.toIntFromShort(byteBuffer.getShort());
        maxBlockSize = Unsigned.toIntFromShort(byteBuffer.getShort());
        minFrameSize = Unsigned.readThreeByteInteger(byteBuffer.get(), byteBuffer.get(), byteBuffer.get());
        maxFrameSize = Unsigned.readThreeByteInteger(byteBuffer.get(), byteBuffer.get(), byteBuffer.get());
        samplingRate = readSamplingRate(byteBuffer);
        noOfChannels = readNoOfChannels(byteBuffer);
        bitsPerSample = readBitsPerSample(byteBuffer);
        noOfSamples = readTotalNumberOfSamples(byteBuffer);
        md5 = readMd5(byteBuffer);

        trackLength = (double) noOfSamples / samplingRate;
        samplingRatePerChannel = samplingRate / noOfChannels;

        // byteBuffer.rewind();
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }

    public int getMaxBlockSize() {
        return maxBlockSize;
    }

    public int getMaxFrameSize() {
        return maxFrameSize;
    }

    public String getMd5() {
        return md5;
    }

    public int getMinBlockSize() {
        return minBlockSize;
    }

    public int getMinFrameSize() {
        return minFrameSize;
    }

    public int getNoOfChannels() {
        return noOfChannels;
    }

    public int getNoOfSamples() {
        return noOfSamples;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public int getSamplingRatePerChannel() {
        return samplingRatePerChannel;
    }

    public double getTrackLength() {
        return trackLength;
    }

    public boolean isValid() {
        return isValid;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MetadataBlockDataStreamInfo.class.getSimpleName() + "[", "]")
                .add("bitsPerSample=" + bitsPerSample)
                .add("maxBlockSize=" + maxBlockSize)
                .add("maxFrameSize=" + maxFrameSize)
                .add("md5='" + md5 + "'")
                .add("minBlockSize=" + minBlockSize)
                .add("minFrameSize=" + minFrameSize)
                .add("noOfChannels=" + noOfChannels)
                .add("noOfSamples=" + noOfSamples)
                .add("samplingRate=" + samplingRate)
                .add("samplingRatePerChannel=" + samplingRatePerChannel)
                .add("trackLength=" + trackLength)
                .add("isValid=" + isValid)
                .toString();
    }
}
