// Created: 13 Aug. 2025
package de.freese.player.test.flac;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import de.freese.player.test.Unsigned;

/**
 * <a href="https://www.rfc-editor.org/rfc/rfc9639.html#name-metadata-block-header">metadata-block-header</a>
 * <a href="https://www.rfc-editor.org/rfc/rfc9639.html#name-streaminfo">streaminfo</a>
 *
 * @author Thomas Freese
 * @see org.jaudiotagger.audio.flac.metadatablock.MetadataBlockHeader
 */
public final class MetadataBlockHeader {
    private static final int HEADER_LENGTH = 4;

    public static MetadataBlockHeader readHeader(final FileChannel fileChannel) throws Exception {
        // 8.1 Each metadata block starts with a 4-byte header.
        final ByteBuffer byteBuffer = ByteBuffer.allocate(HEADER_LENGTH);

        final long startByte = fileChannel.position();
        final int bytesRead = fileChannel.read(byteBuffer);

        if (bytesRead < HEADER_LENGTH) {
            throw new IllegalArgumentException("Unable to read required number of bytes, read:" + bytesRead + ":required:" + HEADER_LENGTH);
        }

        byteBuffer.rewind();

        return new MetadataBlockHeader(startByte, byteBuffer);
    }

    private final BlockType blockType;
    private final int dataLength;
    private final boolean isLastBlock;
    private final long startByte;

    public MetadataBlockHeader(final long startByte, final ByteBuffer byteBuffer) {
        super();

        this.startByte = startByte;

        // 8.1 The first bit in this header flags whether a metadata block is the last one. It is 0 when other metadata blocks follow; otherwise, it is 1.
        isLastBlock = ((byteBuffer.get(0) & 0x80) >>> 7) == 1;

        // 8.1 The 7 remaining bits of the first header byte contain the type of the metadata block as an unsigned number between 0.
        final int type = byteBuffer.get(0) & 0x7F;

        if (type < BlockType.values().length) {
            blockType = BlockType.values()[type];

            // 8.1 The three bytes that follow code for the size of the metadata block in bytes, excluding the 4 header bytes, as an unsigned number coded big-endian.
            dataLength = (Unsigned.toIntFromInt(byteBuffer.get(1)) << 16) + (Unsigned.toIntFromInt(byteBuffer.get(2)) << 8) + (Unsigned.toIntFromInt(byteBuffer.get(3)));
        }
        else {
            throw new IllegalArgumentException("Flac file has invalid block type:" + type);
        }
    }

    // public MetadataBlockHeader(final boolean isLastBlock, final BlockType blockType, final int dataLength) {
    //     super();
    //
    //     this.blockType = blockType;
    //     this.isLastBlock = isLastBlock;
    //     this.dataLength = dataLength;
    //
    //     final byte type;
    //
    //     if (isLastBlock) {
    //         type = (byte) (0x80 | blockType.getId());
    //     }
    //     else {
    //         type = (byte) blockType.getId();
    //     }
    //
    //     final ByteBuffer byteBuffer = ByteBuffer.allocate(HEADER_LENGTH);
    //     byteBuffer.put(type);
    //
    //     // Size is 3Byte BigEndian int
    //     byteBuffer.put((byte) ((dataLength & 0xFF0000) >>> 16));
    //     byteBuffer.put((byte) ((dataLength & 0xFF00) >>> 8));
    //     byteBuffer.put((byte) (dataLength & 0xFF));
    //
    //     // bytes = new byte[HEADER_LENGTH];
    //     //
    //     // for (int i = 0; i < HEADER_LENGTH; i++) {
    //     //     bytes[i] = byteBuffer.get(i);
    //     // }
    // }

    public BlockType getBlockType() {
        return blockType;
    }

    public int getDataLength() {
        return dataLength;
    }

    public long getStartByte() {
        return startByte;
    }

    public boolean isLastBlock() {
        return isLastBlock;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return String.format("StartByte:%d, BlockType:%s, DataLength:%d, isLastBlock:%s,", startByte, blockType, dataLength, isLastBlock);
    }
}
