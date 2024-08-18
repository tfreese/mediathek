/*
 *  21.04.2004 Original verion. davagin@udm.ru.
 *-----------------------------------------------------------------------
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */
package de.freese.player.equalizer;

import java.io.IOException;
import java.io.InputStream;

/**
 * The EqualizerInputStream class
 * Author: Dmitry Vaguine
 * Date: 02.05.2004
 * Time: 12:00:29
 */
public class EqualizerInputStream extends InputStream {
    private static final int BUFFER_SIZE = 65536;

    /**
     * This is special method for checking of supported parameters of equalizer
     *
     * @param bands is the number of bands
     * @param sampleRate is the sample rate of data
     * @param channels is the number of channels
     * @param samplesize is the size of sample in bits
     *
     * @return true if parameters are supported
     */
    public static boolean isParamsSupported(final float sampleRate, final int channels, final int samplesize, final int bands) {
        switch (samplesize) {
            case 8:
            case 16:
            case 24:
                break;
            default:
                return false;
        }

        return IIR.isParamsSupported(bands, sampleRate, channels);
    }

    private static int wrap16Bit(final int data) {
        int value = data;

        if (value > 32767) {
            value = 32767;
        }
        else if (value < -32768) {
            value = -32768;
        }

        if (value < 0) {
            value += 65536;
        }

        return value;
    }

    private static int wrap24Bit(final int data) {
        int value = data;

        if (value > 8_388_607) {
            value = 8_388_607;
        }
        else if (value < -8_388_608) {
            value = -8_388_608;
        }

        if (value < 0) {
            value += 16_777_216;
        }

        return value;
    }

    private static int wrap8Bit(final int data) {
        int value = data;

        if (value > 127) {
            value = 127;
        }
        else if (value < -128) {
            value = -128;
        }

        if (value < 0) {
            value += 256;
        }

        return value;
    }

    private final boolean bigEndian;
    private final IIR iir;
    private final byte[] inbuf = new byte[BUFFER_SIZE];
    private final byte[] outbuf = new byte[BUFFER_SIZE];
    private final int sampleSize;
    private final boolean signed;
    private final InputStream stream;
    private final int[] workbuf = new int[BUFFER_SIZE];

    private int inlen;
    private int inpos;
    private int outlen;
    private int outpos;

    /**
     * Constructs new EqualizerInputStream object
     *
     * @param stream is an input stream for pcm data
     * @param samplerate is a sample rate of input data
     * @param channels is the number of channels
     * @param signed indicates that the data is signed
     * @param sampleSize is the sample bit size of data
     * @param bigEndian indicates that the dat is in "big endian" encoding
     * @param bands is the number of bands
     */
    public EqualizerInputStream(final InputStream stream, final float samplerate, final int channels, final boolean signed, final int sampleSize, final boolean bigEndian,
                                final int bands) {
        super();

        this.stream = stream;
        this.iir = new IIR(bands, samplerate, channels);
        this.signed = signed;
        this.sampleSize = sampleSize;
        this.bigEndian = bigEndian;

        if (!isParamsSupported(samplerate, channels, sampleSize, bands)) {
            throw new IllegalArgumentException("Unsupported sample bit size");
        }
    }

    @Override
    public int available() throws IOException {
        return outlen;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    /**
     * Returns Controls of equalizer
     */
    public IIRControls getControls() {
        return iir.getControls();
    }

    @Override
    public void mark(final int readLimit) {
        // Empty
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int read() throws IOException {
        if (outlen == 0) {
            final boolean eof = fillInBuffer();
            fillOutBuffer();

            if (outlen == 0 && eof) {
                return -1;
            }

            if (outlen == 0 && !eof) {
                throw new IOException("Impossible state");
            }
        }

        final int b = outbuf[outpos++];
        outlen--;

        return b;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        int length = len;

        if (outlen < length) {
            final boolean isEof = fillInBuffer();
            fillOutBuffer();

            if (outlen == 0 && isEof) {
                return -1;
            }

            if (outlen == 0 && !isEof) {
                throw new IOException("Impossible state");
            }
        }

        length = Math.min(outlen, length);

        if (length > 0) {
            System.arraycopy(outbuf, outpos, b, off, length);
            outpos += length;
            outlen -= length;
        }

        return length;
    }

    @Override
    public void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    @Override
    public long skip(long n) throws IOException {
        int l;

        if (n <= outlen) {
            outpos += n;
            outlen -= n;
            return n;
        }

        n -= outlen;
        l = outlen;
        outlen = 0;
        outpos = 0;

        if (n <= inlen) {
            inpos += n;
            inlen -= n;
            return l + n;
        }

        n -= inlen;
        l += inlen;
        inlen = 0;
        inpos = 0;

        return stream.skip(n) + l;
    }

    private int convertToByte(final byte[] b, final int off, final int length) {
        int p = off;
        int d;

        switch (sampleSize) {
            case 8: {
                for (int i = 0; i < length; i++) {
                    b[p++] = (byte) (wrap8Bit(workbuf[i]) & 0xff);
                }
                break;
            }
            case 16: {
                if (bigEndian) {
                    for (int i = 0; i < length; i++) {
                        d = wrap16Bit(workbuf[i]);
                        b[p++] = (byte) ((d & 0xff00) >> 8);
                        b[p++] = (byte) (d & 0xff);
                    }
                }
                else {
                    for (int i = 0; i < length; i++) {
                        d = wrap16Bit(workbuf[i]);
                        b[p++] = (byte) (d & 0xff);
                        b[p++] = (byte) ((d & 0xff00) >> 8);
                    }
                }
                break;
            }
            case 24: {
                if (bigEndian) {
                    for (int i = 0; i < length; i++) {
                        d = wrap24Bit(workbuf[i]);
                        b[p++] = (byte) (d & 0xff);
                        b[p++] = (byte) ((d & 0xff00) >> 8);
                        b[p++] = (byte) ((d & 0xff0000) >> 16);
                    }
                }
                else {
                    for (int i = 0; i < length; i++) {
                        d = wrap24Bit(workbuf[i]);
                        b[p++] = (byte) ((d & 0xff0000) >> 16);
                        b[p++] = (byte) ((d & 0xff00) >> 8);
                        b[p++] = (byte) (d & 0xff);
                    }
                }
                break;
            }
        }

        return p - off;
    }

    private int convertToInt(final int length) {
        int l = length;
        int temp;
        byte[] a1;

        switch (sampleSize) {
            case 8: {
                if (length > 0) {
                    System.arraycopy(inbuf, 0, workbuf, 0, length);
                    inpos += length;
                    inlen -= length;
                }

                break;
            }
            case 16: {
                l = length >> 1;

                if (l > 0) {
                    if (bigEndian) {
                        for (int i = 0; i < l; i++) {
                            temp = (((a1 = inbuf)[inpos++] & 0xff) << 8) | (a1[inpos++] & 0xff);
                            workbuf[i] = signed && temp > 32767 ? temp - 65536 : temp;
                        }
                    }
                    else {
                        for (int i = 0; i < l; i++) {
                            temp = ((a1 = inbuf)[inpos++] & 0xff) | ((a1[inpos++] & 0xff) << 8);
                            workbuf[i] = signed && temp > 32767 ? temp - 65536 : temp;
                        }
                    }

                    inlen -= inpos;
                }

                break;
            }
            case 24: {
                l = length / 3;

                if (l > 0) {
                    if (bigEndian) {
                        for (int i = 0; i < l; i++) {
                            temp = ((a1 = inbuf)[inpos++] & 0xff) | ((a1[inpos++] & 0xff) << 8) | ((a1[inpos++] & 0xff) << 16);
                            workbuf[i] = signed && temp > 8_388_607 ? temp - 16_777_216 : temp;
                        }
                    }
                    else {
                        for (int i = 0; i < l; i++) {
                            temp = (((a1 = inbuf)[inpos++] & 0xff) << 16) | ((a1[inpos++] & 0xff) << 8) | (a1[inpos++] & 0xff);
                            workbuf[i] = signed && temp > 8_388_607 ? temp - 16_777_216 : temp;
                        }
                    }

                    inlen -= inpos;
                }

                break;
            }
        }

        return l;
    }

    private boolean fillInBuffer() throws IOException {
        if (inpos != 0 && inlen > 0) {
            System.arraycopy(inbuf, inpos, inbuf, 0, inlen);
        }

        inpos = 0;
        int num;
        boolean eof = false;

        while (inlen != inbuf.length) {
            num = stream.read(inbuf, inlen, inbuf.length - inlen);

            if (num < 0) {
                eof = true;
                break;
            }

            inlen += num;
        }

        return eof;
    }

    private void fillOutBuffer() {
        if (outpos != 0 && outlen > 0) {
            System.arraycopy(outbuf, outpos, outbuf, 0, outlen);
        }

        outpos = 0;
        int len = outbuf.length - outlen;
        len = inlen < len ? inlen : len;
        len = convertToInt(len);

        if (len > 0) {
            iir.iir(workbuf, len);
            len = convertToByte(outbuf, outlen, len);
            outlen += len;
        }
    }
}
