// Created: 13 Aug. 2025
package de.freese.player.test;

import java.nio.ByteBuffer;

import org.jaudiotagger.audio.generic.Utils;

/**
 * @author Thomas Freese
 * @see Utils
 */
public final class Unsigned {
    /**
     * Return the nth Bit in the Value.
     */
    public static int getBit(final int value, final int n) {
        return (value >> n) & 1;
    }

    /**
     * Computes a number whereby the 1st byte is the least significant and the last
     * byte is the most significant. This version doesn't take a length,
     * and it returns an int rather than a long.
     *
     * @param b The byte array. Maximum length for valid results is 4 bytes.
     */
    public static int getIntLE(final byte[] b) {
        return (int) getLongLE(ByteBuffer.wrap(b), 0, b.length - 1);
    }

    /**
     * Computes a number whereby the 1st byte is the least significant and the last
     * byte is the most significant. end - start must be no greater than 4.
     *
     * @param b The byte array
     * @param start The starting offset in b (b[offset]). The less
     * significant byte
     * @param end The end index (included) in b (b[end])
     *
     * @return an int number represented by the byte sequence.
     */
    public static int getIntLE(final byte[] b, final int start, final int end) {
        return (int) getLongLE(ByteBuffer.wrap(b), start, end);
    }

    /**
     * Computes a number whereby the 1st byte is the least significant and the last byte is the most significant.
     * So if storing a number which only requires one byte it will be stored in the first byte.
     *
     * @param b The byte array @param start The starting offset in b
     * (b[offset]). The less significant byte @param end The end index
     * (included) in b (b[end]). The most significant byte
     *
     * @return a long number represented by the byte sequence.
     *
     */
    public static long getLongLE(final ByteBuffer b, final int start, final int end) {
        long number = 0;

        for (int i = 0; i < (end - start + 1); i++) {
            number += (long) (b.get(start + i) & 0xFF) << i * 8;
        }

        return number;
    }

    public static boolean isBit(final int value, final int n) {
        return getBit(value, n) == 1;
    }

    /**
     * Some values are stored as 3 byte integrals (instead of the more usual 2 or 4).
     */
    public static int readThreeByteInteger(final byte b1, final byte b2, final byte b3) {
        return (toIntFromByte(b1) << 16)
                + (toIntFromByte(b2) << 8)
                + toIntFromByte(b3);
    }

    /**
     * Used to convert (signed byte) to an integer as if signed byte was unsigned hence allowing
     * it to represent values 0 -> 255 rather than -128 -> 127.
     */
    public static int toIntFromByte(final byte value) {
        return value & 0xFF;
    }

    public static int toIntFromInt(final int value) {
        return value & 0xFF;
    }

    /**
     * Used to convert (signed short) to an integer as if signed short was unsigned hence allowing
     * it to represent values 0 -> 65536 rather than -32786 -> 32786.
     */
    public static int toIntFromShort(final short value) {
        return value & 0xFFFF;
    }

    private Unsigned() {
        super();
    }
}
