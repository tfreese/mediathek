// Created: 14 Sept. 2025
package de.freese.player.test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * @author chatgpt.com
 */
public final class WavStreamingDecoder {
    // WavData speichert die grundlegenden WAV-Daten
    public static class WavData {
        public int bitsPerSample;
        public int channels;
        public int sampleRate;

        public WavData(final int sampleRate, final int channels, final int bitsPerSample) {
            super();

            this.sampleRate = sampleRate;
            this.channels = channels;
            this.bitsPerSample = bitsPerSample;
        }
    }

    // Dekodiert 16-Bit PCM-Daten aus dem Byte-Array in ein 2D-Array von shorts (für Mehrkanäle)
    public static short[][] decode16BitPcmMultiChannel(final byte[] rawData, final int numSamples, final int numChannels) {
        final short[][] samples = new short[numChannels][numSamples];
        final ByteBuffer buffer = ByteBuffer.wrap(rawData);
        buffer.order(ByteOrder.LITTLE_ENDIAN); // WAV verwendet Little Endian

        // Für jedes Sample und jeden Kanal den Wert extrahieren
        for (int i = 0; i < numSamples; i++) {
            for (int ch = 0; ch < numChannels; ch++) {
                samples[ch][i] = buffer.getShort(); // Ein 16-Bit Sample für jeden Kanal
            }
        }

        return samples;
    }

    // Liest und dekodiert die PCM-Daten in Echtzeit (streaming)
    public static void decodeWavStream(final String filename, final WavData wavData, final int bufferSize) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename))) {
            // Positioniere uns nach dem Header (bis "data"-Block).
            in.skip(44); // Standard-WAV-Headergröße (44 Byte).

            final byte[] buffer = new byte[bufferSize];
            final ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN); // WAV verwendet Little Endian.

            while (in.available() > 0) {
                // Lese den nächsten Block.
                final int bytesRead = in.read(buffer);

                if (bytesRead == -1) {
                    break; // Ende der Datei erreicht.
                }

                byteBuffer.clear();
                byteBuffer.put(buffer, 0, bytesRead);

                // Hier dekodieren wir die Daten (16-Bit PCM) für Mehrkanal-Audio.
                final short[][] samples = decode16BitPcmMultiChannel(byteBuffer.array(), bytesRead / (2 * wavData.channels), wavData.channels);

                // Hier kannst du die Samples weiterverarbeiten oder an eine Audio-Wiedergabe-Pipeline weitergeben.
                processSamples(samples);
            }
        }
    }

    // Liest Header und Audioinformationen der WAV-Datei.
    public static WavData readWavHeader(final String filename) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            // == HEADER: "RIFF" ==
            final byte[] riff = new byte[4];
            in.readFully(riff);

            if (!new String(riff).equals("RIFF")) {
                throw new IllegalArgumentException("Not a RIFF file");
            }

            in.skipBytes(4); // skip chunk size

            final byte[] wave = new byte[4];
            in.readFully(wave);

            if (!new String(wave).equals("WAVE")) {
                throw new IllegalArgumentException("Not a WAVE file");
            }

            // == Subchunks ==
            int sampleRate = 0;
            int channels = 0;
            int bitsPerSample = 0;

            while (in.available() > 0) {
                final byte[] chunkIdBytes = new byte[4];
                in.readFully(chunkIdBytes);
                final String chunkId = new String(chunkIdBytes);
                final int chunkSize = Integer.reverseBytes(in.readInt());

                if (chunkId.equals("fmt ")) {
                    final short audioFormat = Short.reverseBytes(in.readShort());

                    if (audioFormat != 1) {
                        throw new UnsupportedOperationException("Only PCM encoding supported");
                    }

                    channels = Short.reverseBytes(in.readShort());
                    sampleRate = Integer.reverseBytes(in.readInt());
                    final int byteRate = Integer.reverseBytes(in.readInt());
                    final short blockAlign = Short.reverseBytes(in.readShort());
                    bitsPerSample = Short.reverseBytes(in.readShort());

                    // Skip remaining bytes in fmt chunk.
                    final int remaining = chunkSize - 16;

                    if (remaining > 0) {
                        in.skipBytes(remaining);
                    }
                }
                else if (chunkId.equals("data")) {
                    // Wir haben den "data"-Block gefunden, den wir später streamen.
                    break;
                }
                else {
                    // Unbekannter Chunk, überspringen.
                    in.skipBytes(chunkSize);
                }
            }

            if (sampleRate == 0 || channels == 0 || bitsPerSample == 0) {
                throw new IOException("Invalid WAV header or missing required information");
            }

            return new WavData(sampleRate, channels, bitsPerSample);
        }
    }

    static void main(final String[] args) {
        try {
            // final Path path = Path.of("music-player/samples/sample.wav");
            final String filename = "music-player/samples/sample.wav";  // Pfad zur WAV-Datei
            final WavData wavData = readWavHeader(filename);

            System.out.println("Sample Rate: " + wavData.sampleRate);
            System.out.println("Channels: " + wavData.channels);
            System.out.println("Bits per Sample: " + wavData.bitsPerSample);

            // Beispiel: Streamen mit einem Buffer von 1024 Byte (angepasst an die Datei).
            final int bufferSize = 1024;
            decodeWavStream(filename, wavData, bufferSize);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Beispielmethode für die Verarbeitung der Samples (hier könntest du die Daten z.B. wiedergeben).
    private static void processSamples(final short[][] samples) {
        // Hier kannst du die Samples weiterverarbeiten, z.B. an ein Audio-Output-System weitergeben
        // Beispiel: Ausgabe der ersten 5 Samples für jeden Kanal.
        for (int ch = 0; ch < samples.length; ch++) {
            System.out.println("Kanal " + (ch + 1) + ": " + Arrays.toString(Arrays.copyOfRange(samples[ch], 0, Math.min(5, samples[ch].length))));
        }
    }
}
