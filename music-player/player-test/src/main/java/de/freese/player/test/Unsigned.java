// Created: 13 Aug. 2025
package de.freese.player.test;

/**
 * @author Thomas Freese
 */
public final class Unsigned {
    /**
     * Return the nth Bit in the Value.
     */
    public static int getBit(final int value, final int n) {
        return (value >> n) & 1;
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
