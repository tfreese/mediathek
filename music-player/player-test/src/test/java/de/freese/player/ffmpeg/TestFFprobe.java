// Created: 18 Juli 2024
package de.freese.player.ffmpeg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import de.freese.player.input.AudioFile;

/**
 * @author Thomas Freese
 */
class TestFFprobe {
    @Test
    void testMetaDataAif() throws Exception {
        final AudioFile audioFile = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.aif").toUri());

        assertNotNull(audioFile);
        assertEquals(384, audioFile.getBitRate());
        assertEquals(2, audioFile.getChannels());
        assertEquals("PT1M0.48S", audioFile.getDuration().toString());
        assertEquals("aiff/adpcm_ima_qt", audioFile.getFormat());
        assertEquals(48000, audioFile.getSamplingRate());
    }

    @Test
    void testMetaDataAu() throws Exception {
        final AudioFile audioFile = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.au").toUri());

        assertNotNull(audioFile);
        assertEquals(352, audioFile.getBitRate());
        assertEquals(1, audioFile.getChannels());
        assertEquals("PT2.42S", audioFile.getDuration().toString());
        assertEquals("au/pcm_s8", audioFile.getFormat());
        assertEquals(44100, audioFile.getSamplingRate());
    }

    @Test
    void testMetaDataFlac() throws Exception {
        final AudioFile audioFile = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.flac").toUri());

        assertNotNull(audioFile);
        assertEquals(809, audioFile.getBitRate());
        assertEquals(2, audioFile.getChannels());
        assertEquals("PT2M2.09S", audioFile.getDuration().toString());
        assertEquals("flac", audioFile.getFormat());
        assertEquals(44100, audioFile.getSamplingRate());
    }

    @Test
    void testMetaDataM4a() throws Exception {
        final AudioFile audioFile = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.m4a").toUri());

        assertNotNull(audioFile);
        assertEquals(198, audioFile.getBitRate());
        assertEquals(2, audioFile.getChannels());
        assertEquals("PT39.94S", audioFile.getDuration().toString());
        assertEquals("mov/aac", audioFile.getFormat());
        assertEquals(48000, audioFile.getSamplingRate());
        assertEquals("Bee Moved", audioFile.getAlbum());
        assertEquals("Blue Monday FM", audioFile.getArtist());
        assertEquals("Bee Moved", audioFile.getTitle());
        assertNull(audioFile.getGenre());
    }

    @Test
    void testMetaDataM4b() throws Exception {
        final AudioFile audioFile = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.m4b").toUri());

        assertNotNull(audioFile);
        assertEquals(16, audioFile.getBitRate());
        assertEquals(1, audioFile.getChannels());
        assertEquals("PT3M8.04S", audioFile.getDuration().toString());
        assertEquals("mov/aac", audioFile.getFormat());
        assertEquals(44100, audioFile.getSamplingRate());
        assertEquals("Epistle of Philemon", audioFile.getAlbum());
        assertEquals("American Standard Version", audioFile.getArtist());
        assertEquals("1", audioFile.getTitle());
        assertEquals("Speech", audioFile.getGenre());
    }

    @Test
    void testMetaDataMp3() throws Exception {
        final AudioFile audioFile = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.mp3").toUri());

        assertNotNull(audioFile);
        assertEquals(195, audioFile.getBitRate());
        assertEquals(2, audioFile.getChannels());
        assertEquals("PT43.08S", audioFile.getDuration().toString());
        assertEquals("mp3", audioFile.getFormat());
        assertEquals(44100, audioFile.getSamplingRate());
    }

    @Test
    void testMetaDataOgg() throws Exception {
        final AudioFile audioFile = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.ogg").toUri());

        assertNotNull(audioFile);
        assertEquals(160, audioFile.getBitRate());
        assertEquals(2, audioFile.getChannels());
        assertEquals("PT26.29S", audioFile.getDuration().toString());
        assertEquals("ogg/vorbis", audioFile.getFormat());
        assertEquals(44100, audioFile.getSamplingRate());
        assertEquals("Virgin Radio", audioFile.getArtist());
    }

    @Test
    void testMetaDataWav() throws Exception {
        final AudioFile audioFile = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.wav").toUri());

        assertNotNull(audioFile);
        assertEquals(1411, audioFile.getBitRate());
        assertEquals(2, audioFile.getChannels());
        assertEquals("PT5.94S", audioFile.getDuration().toString());
        assertEquals("wav/pcm_s16le", audioFile.getFormat());
        assertEquals(44100, audioFile.getSamplingRate());
    }

    @Test
    void testMetaDataWma() throws Exception {
        final AudioFile audioFile = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.wma").toUri());

        assertNotNull(audioFile);
        assertEquals(128, audioFile.getBitRate());
        assertEquals(2, audioFile.getChannels());
        assertEquals("PT1M45.79S", audioFile.getDuration().toString());
        assertEquals("asf/wmav2", audioFile.getFormat());
        assertEquals(44100, audioFile.getSamplingRate());
    }
}
