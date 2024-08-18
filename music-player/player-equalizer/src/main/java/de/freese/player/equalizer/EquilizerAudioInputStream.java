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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * The EqualizerInputStream input stream
 * Author: Dmitry Vaguine
 * Date: 02.05.2004
 * Time: 12:00:29
 */
public class EquilizerAudioInputStream extends AudioInputStream {
    /**
     * This is special method helps to determine supported audio format
     *
     * @param format is an audio format
     * @param bands is the number of bands
     *
     * @return true if params supported
     */
    public static boolean isParamsSupported(final AudioFormat format, final int bands) {
        if (!format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
            return false;
        }

        return EqualizerInputStream.isParamsSupported(
                format.getSampleRate(),
                format.getChannels(),
                format.getSampleSizeInBits(),
                bands);
    }

    private final EqualizerInputStream eq;

    /**
     * Constructs new audio stream
     *
     * @param stream input stream with audio data
     * @param bands is the number of bands
     */
    public EquilizerAudioInputStream(final AudioInputStream stream, final int bands) {
        super(stream, stream.getFormat(), stream.getFrameLength());

        final AudioFormat audioFormat = stream.getFormat();

        if (!audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
            throw new IllegalArgumentException("Unsupported encoding");
        }

        eq = new EqualizerInputStream(stream,
                audioFormat.getSampleRate(),
                audioFormat.getChannels(),
                audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED),
                audioFormat.getSampleSizeInBits(),
                audioFormat.isBigEndian(),
                bands);
    }

    @Override
    public int available() throws IOException {
        return eq.available();
    }

    @Override
    public void close() throws IOException {
        eq.close();
    }

    /**
     * Returns Controls of equalizer
     */
    public IIRControls getControls() {
        return eq.getControls();
    }

    @Override
    public void mark(final int readLimit) {
        eq.mark(readLimit);
    }

    @Override
    public boolean markSupported() {
        return eq.markSupported();
    }

    @Override
    public int read() throws IOException {
        return eq.read();
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return eq.read(b, off, len);
    }

    @Override
    public void reset() throws IOException {
        eq.reset();
    }

    @Override
    public long skip(final long n) throws IOException {
        return eq.skip(n);
    }
}
