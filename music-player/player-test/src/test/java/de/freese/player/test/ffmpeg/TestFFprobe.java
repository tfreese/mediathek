// Created: 18 Juli 2024
package de.freese.player.test.ffmpeg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.ffmpeg.FFLocator;
import de.freese.player.core.input.AudioSource;

/**
 * @author Thomas Freese
 */
class TestFFprobe {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestFFprobe.class);

    @Test
    @Disabled("only for temp. test")
    void testMetaData() {
        final Path path = Path.of("");
        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(path.toUri());

        assertNotNull(audioSource);
    }

    @Test
    void testMetaDataAif() throws Exception {
        final Path path = Path.of("..", "samples", "sample.aif");

        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(path.toUri());
        assertNotNull(audioSource);
        assertEquals(384, audioSource.getBitRate());
        assertEquals(2, audioSource.getChannels());
        assertEquals("PT1M0.48S", audioSource.getDuration().toString());
        assertEquals("aiff/adpcm_ima_qt", audioSource.getFormat());
        assertEquals(48000, audioSource.getSampleRate());

        final AudioFile audioFile = AudioFileIO.read(path.toFile());
        assertNotNull(audioFile);
        assertNotNull(audioFile.getAudioHeader());
        assertNotNull(audioFile.getTag());
        assertEquals(26112L, audioFile.getAudioHeader().getBitRateAsNumber());
        assertEquals(1, audioFile.getAudioHeader().getTrackLength());
        assertEquals("Aif", audioFile.getAudioHeader().getFormat());
        assertEquals(48000, audioFile.getAudioHeader().getSampleRateAsNumber());
    }

    @Test
    void testMetaDataAu() throws Exception {
        final Path path = Path.of("..", "samples", "sample.au");

        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(path.toUri());
        assertNotNull(audioSource);
        assertEquals(352, audioSource.getBitRate());
        assertEquals(1, audioSource.getChannels());
        assertEquals("PT2.42S", audioSource.getDuration().toString());
        assertEquals("au/pcm_s8", audioSource.getFormat());
        assertEquals(44100, audioSource.getSampleRate());

        // https://en.wikipedia.org/wiki/Au_file_format
        // https://docs.oracle.com/cd/E36784_01/html/E36882/au-4.html
        final int headerSize = 28;

        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
            if (fileChannel.size() == 0L) {
                throw new IllegalArgumentException("File is empty: " + path);
            }

            // Header
            final ByteBuffer headerBuffer = ByteBuffer.allocate(headerSize);
            headerBuffer.order(ByteOrder.BIG_ENDIAN);

            final int bytesRead = fileChannel.read(headerBuffer);
            if (bytesRead < headerSize) {
                throw new IllegalArgumentException("Unable to read required number of bytes read:" + bytesRead + ":required:" + headerSize);
            }

            headerBuffer.flip();

            // final byte[] buffer = new byte[4];
            // headerBuffer.get(buffer);
            // String magicNumber =new String(buffer, StandardCharsets.UTF_8);
            final int magicNumber = headerBuffer.getInt();

            LOGGER.info("Magic Number: {}", magicNumber);
            // LOGGER.info("Magic Number: {}", headerBuffer.getInt());

            // 0x2e736e64 (four ASCII characters ".snd")
            if (magicNumber != 0x2e736e64) {
                throw new IllegalArgumentException("Invalid magic number read:" + magicNumber + ":required:0x2e736e64 = '.snd'");
            }

            LOGGER.info("Data offset: {}", headerBuffer.getInt());

            final int dataSize = headerBuffer.getInt();
            LOGGER.info("Data size: {}", dataSize);
            LOGGER.info("Encoding: {}", headerBuffer.getInt());

            final int sampleRate = headerBuffer.getInt();
            final int channels = headerBuffer.getInt();

            LOGGER.info("Sample rate: {}", sampleRate);
            LOGGER.info("Channels: {}", channels);
            LOGGER.info("Offset: {}", headerBuffer.getInt());

            final int bitRate = (sampleRate * 8) / 1000; // Encoding = 2 = 8-bit linear PCM
            final double trackLength = (double) dataSize / sampleRate; // Seconds
            // Duration.ofMillis((long) (trackLength * 1000D));

            LOGGER.info("Bit rate: {}", bitRate);
            LOGGER.info("Track length [s]: {}", trackLength);

            assertEquals(352, bitRate);
            assertEquals(1, channels);
            assertEquals(44100, sampleRate);
            assertEquals(2.42D, trackLength, 0.0017D);
        }
    }

    @Test
    void testMetaDataFlac() throws Exception {
        final Path path = Path.of("..", "samples", "sample.flac");

        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(path.toUri());
        assertNotNull(audioSource);
        assertEquals(809, audioSource.getBitRate());
        assertEquals(2, audioSource.getChannels());
        assertEquals("PT2M2.09S", audioSource.getDuration().toString());
        assertEquals("flac", audioSource.getFormat());
        assertEquals(44100, audioSource.getSampleRate());

        final AudioFile audioFile = AudioFileIO.read(path.toFile());
        assertNotNull(audioFile);
        assertNotNull(audioFile.getAudioHeader());
        assertNotNull(audioFile.getTag());
        assertEquals(809L, audioFile.getAudioHeader().getBitRateAsNumber());
        assertEquals("2", audioFile.getAudioHeader().getChannels());
        assertEquals(122, audioFile.getAudioHeader().getTrackLength());
        assertEquals("Flac", audioFile.getAudioHeader().getFormat());
        assertEquals(44100, audioFile.getAudioHeader().getSampleRateAsNumber());
    }

    @Test
    void testMetaDataM4a() throws Exception {
        final Path path = Path.of("..", "samples", "sample.m4a");

        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(path.toUri());
        assertNotNull(audioSource);
        assertEquals(198, audioSource.getBitRate());
        assertEquals(2, audioSource.getChannels());
        assertEquals("PT39.94S", audioSource.getDuration().toString());
        assertEquals("mov/aac", audioSource.getFormat());
        assertEquals(48000, audioSource.getSampleRate());
        assertEquals("Bee Moved", audioSource.getAlbum());
        assertEquals("Blue Monday FM", audioSource.getArtist());
        assertEquals("Bee Moved", audioSource.getTitle());
        assertNull(audioSource.getGenre());

        final AudioFile audioFile = AudioFileIO.read(path.toFile());
        assertNotNull(audioFile);
        assertNotNull(audioFile.getAudioHeader());
        assertNotNull(audioFile.getTag());
        assertEquals(192L, audioFile.getAudioHeader().getBitRateAsNumber());
        // assertEquals("2", audioFile.getAudioHeader().getChannels());
        assertEquals(40, audioFile.getAudioHeader().getTrackLength());
        assertEquals("Aac", audioFile.getAudioHeader().getFormat());
        assertEquals(48000, audioFile.getAudioHeader().getSampleRateAsNumber());
        assertEquals("Bee Moved", audioFile.getTag().getFirst(FieldKey.ALBUM));
        assertEquals("Blue Monday FM", audioFile.getTag().getFirst(FieldKey.ARTIST));
        assertEquals("Bee Moved", audioFile.getTag().getFirst(FieldKey.TITLE));
    }

    @Test
    void testMetaDataM4b() throws Exception {
        final Path path = Path.of("..", "samples", "sample.m4b");

        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(path.toUri());
        assertNotNull(audioSource);
        assertEquals(16, audioSource.getBitRate());
        assertEquals(1, audioSource.getChannels());
        assertEquals("PT3M8.04S", audioSource.getDuration().toString());
        assertEquals("mov/aac", audioSource.getFormat());
        assertEquals(44100, audioSource.getSampleRate());
        assertEquals("Epistle of Philemon", audioSource.getAlbum());
        assertEquals("American Standard Version", audioSource.getArtist());
        assertEquals("1", audioSource.getTitle());
        assertEquals("Speech", audioSource.getGenre());

        final AudioFile audioFile = AudioFileIO.read(path.toFile());
        assertNotNull(audioFile);
        assertNotNull(audioFile.getAudioHeader());
        assertNotNull(audioFile.getTag());
        assertEquals(16L, audioFile.getAudioHeader().getBitRateAsNumber());
        assertEquals("1", audioFile.getAudioHeader().getChannels());
        assertEquals(186, audioFile.getAudioHeader().getTrackLength());
        assertEquals("Aac", audioFile.getAudioHeader().getFormat());
        assertEquals(44100, audioFile.getAudioHeader().getSampleRateAsNumber());
        assertEquals("Epistle of Philemon", audioFile.getTag().getFirst(FieldKey.ALBUM));
        assertEquals("American Standard Version", audioFile.getTag().getFirst(FieldKey.ARTIST));
        assertEquals("1", audioFile.getTag().getFirst(FieldKey.TITLE));
        assertEquals("Speech", audioFile.getTag().getFirst(FieldKey.GENRE));
    }

    @Test
    void testMetaDataMp3() throws Exception {
        final Path path = Path.of("..", "samples", "sample.mp3");

        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(path.toUri());
        assertNotNull(audioSource);
        assertEquals(195, audioSource.getBitRate());
        assertEquals(2, audioSource.getChannels());
        assertEquals("PT43.08S", audioSource.getDuration().toString());
        assertEquals("mp3", audioSource.getFormat());
        assertEquals(44100, audioSource.getSampleRate());

        final AudioFile audioFile = AudioFileIO.read(path.toFile());
        assertNotNull(audioFile);
        assertNotNull(audioFile.getAudioHeader());
        assertEquals(195L, audioFile.getAudioHeader().getBitRateAsNumber());
        assertEquals("Joint Stereo", audioFile.getAudioHeader().getChannels());
        assertEquals(43, audioFile.getAudioHeader().getTrackLength());
        assertEquals("Mp3", audioFile.getAudioHeader().getFormat());
        assertEquals(44100, audioFile.getAudioHeader().getSampleRateAsNumber());
    }

    @Test
    void testMetaDataOgg() throws Exception {
        final Path path = Path.of("..", "samples", "sample.ogg");

        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(path.toUri());
        assertNotNull(audioSource);
        assertEquals(160, audioSource.getBitRate());
        assertEquals(2, audioSource.getChannels());
        assertEquals("PT26.29S", audioSource.getDuration().toString());
        assertEquals("ogg/vorbis", audioSource.getFormat());
        assertEquals(44100, audioSource.getSampleRate());
        assertEquals("Virgin Radio", audioSource.getArtist());

        final AudioFile audioFile = AudioFileIO.read(path.toFile());
        assertNotNull(audioFile);
        assertNotNull(audioFile.getAudioHeader());
        assertNotNull(audioFile.getTag());
        assertEquals(160L, audioFile.getAudioHeader().getBitRateAsNumber());
        assertEquals("2", audioFile.getAudioHeader().getChannels());
        assertEquals(11, audioFile.getAudioHeader().getTrackLength());
        assertEquals("Ogg", audioFile.getAudioHeader().getFormat());
        assertEquals(44100, audioFile.getAudioHeader().getSampleRateAsNumber());
        assertEquals("Virgin Radio", audioFile.getTag().getFirst(FieldKey.ARTIST));
    }

    @Test
    void testMetaDataWav() throws Exception {
        final Path path = Path.of("..", "samples", "sample.wav");

        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(path.toUri());
        assertNotNull(audioSource);
        assertEquals(1411, audioSource.getBitRate());
        assertEquals(2, audioSource.getChannels());
        assertEquals("PT5.94S", audioSource.getDuration().toString());
        assertEquals("wav/pcm_s16le", audioSource.getFormat());
        assertEquals(44100, audioSource.getSampleRate());

        final AudioFile audioFile = AudioFileIO.read(path.toFile());
        assertNotNull(audioFile);
        assertNotNull(audioFile.getAudioHeader());
        assertNotNull(audioFile.getTag());
        assertEquals(1411L, audioFile.getAudioHeader().getBitRateAsNumber());
        assertEquals("2", audioFile.getAudioHeader().getChannels());
        assertEquals(6, audioFile.getAudioHeader().getTrackLength());
        assertEquals("Wav", audioFile.getAudioHeader().getFormat());
        assertEquals(44100, audioFile.getAudioHeader().getSampleRateAsNumber());
    }

    @Test
    void testMetaDataWma() throws Exception {
        final Path path = Path.of("..", "samples", "sample.wma");

        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(path.toUri());
        assertNotNull(audioSource);
        assertEquals(128, audioSource.getBitRate());
        assertEquals(2, audioSource.getChannels());
        assertEquals("PT1M45.79S", audioSource.getDuration().toString());
        assertEquals("asf/wmav2", audioSource.getFormat());
        assertEquals(44100, audioSource.getSampleRate());

        final AudioFile audioFile = AudioFileIO.read(path.toFile());
        assertNotNull(audioFile);
        assertNotNull(audioFile.getAudioHeader());
        assertNotNull(audioFile.getTag());
        assertEquals(128L, audioFile.getAudioHeader().getBitRateAsNumber());
        assertEquals("2", audioFile.getAudioHeader().getChannels());
        assertEquals(106, audioFile.getAudioHeader().getTrackLength());
        assertEquals("Wma", audioFile.getAudioHeader().getFormat());
        assertEquals(44100, audioFile.getAudioHeader().getSampleRateAsNumber());
    }
}
