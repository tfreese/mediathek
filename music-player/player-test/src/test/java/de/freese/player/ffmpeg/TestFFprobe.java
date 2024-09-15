// Created: 18 Juli 2024
package de.freese.player.ffmpeg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import de.freese.player.input.AudioSource;

/**
 * @author Thomas Freese
 */
class TestFFprobe {
    @Test
    void testMetaData() throws Exception {
        final Path path = Path.of("/mnt/mediathek/musik/Bravo Hits/Bravo Hits 40/Faith Hill (Bravo Hits 40) - 1 - 07 - When the Lights Go Down (album version).mp3");
        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(path.toUri());

        assertNotNull(audioSource);
    }

    @Test
    void testMetaDataAif() throws Exception {
        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.aif").toUri());

        assertNotNull(audioSource);
        assertEquals(384, audioSource.getBitRate());
        assertEquals(2, audioSource.getChannels());
        assertEquals("PT1M0.48S", audioSource.getDuration().toString());
        assertEquals("aiff/adpcm_ima_qt", audioSource.getFormat());
        assertEquals(48000, audioSource.getSamplingRate());
    }

    @Test
    void testMetaDataAu() throws Exception {
        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.au").toUri());

        assertNotNull(audioSource);
        assertEquals(352, audioSource.getBitRate());
        assertEquals(1, audioSource.getChannels());
        assertEquals("PT2.42S", audioSource.getDuration().toString());
        assertEquals("au/pcm_s8", audioSource.getFormat());
        assertEquals(44100, audioSource.getSamplingRate());
    }

    @Test
    void testMetaDataFlac() throws Exception {
        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.flac").toUri());

        assertNotNull(audioSource);
        assertEquals(809, audioSource.getBitRate());
        assertEquals(2, audioSource.getChannels());
        assertEquals("PT2M2.09S", audioSource.getDuration().toString());
        assertEquals("flac", audioSource.getFormat());
        assertEquals(44100, audioSource.getSamplingRate());
    }

    @Test
    void testMetaDataM4a() throws Exception {
        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.m4a").toUri());

        assertNotNull(audioSource);
        assertEquals(198, audioSource.getBitRate());
        assertEquals(2, audioSource.getChannels());
        assertEquals("PT39.94S", audioSource.getDuration().toString());
        assertEquals("mov/aac", audioSource.getFormat());
        assertEquals(48000, audioSource.getSamplingRate());
        assertEquals("Bee Moved", audioSource.getAlbum());
        assertEquals("Blue Monday FM", audioSource.getArtist());
        assertEquals("Bee Moved", audioSource.getTitle());
        assertNull(audioSource.getGenre());
    }

    @Test
    void testMetaDataM4b() throws Exception {
        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.m4b").toUri());

        assertNotNull(audioSource);
        assertEquals(16, audioSource.getBitRate());
        assertEquals(1, audioSource.getChannels());
        assertEquals("PT3M8.04S", audioSource.getDuration().toString());
        assertEquals("mov/aac", audioSource.getFormat());
        assertEquals(44100, audioSource.getSamplingRate());
        assertEquals("Epistle of Philemon", audioSource.getAlbum());
        assertEquals("American Standard Version", audioSource.getArtist());
        assertEquals("1", audioSource.getTitle());
        assertEquals("Speech", audioSource.getGenre());
    }

    @Test
    void testMetaDataMp3() throws Exception {
        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.mp3").toUri());

        assertNotNull(audioSource);
        assertEquals(195, audioSource.getBitRate());
        assertEquals(2, audioSource.getChannels());
        assertEquals("PT43.08S", audioSource.getDuration().toString());
        assertEquals("mp3", audioSource.getFormat());
        assertEquals(44100, audioSource.getSamplingRate());
    }

    @Test
    void testMetaDataOgg() throws Exception {
        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.ogg").toUri());

        assertNotNull(audioSource);
        assertEquals(160, audioSource.getBitRate());
        assertEquals(2, audioSource.getChannels());
        assertEquals("PT26.29S", audioSource.getDuration().toString());
        assertEquals("ogg/vorbis", audioSource.getFormat());
        assertEquals(44100, audioSource.getSamplingRate());
        assertEquals("Virgin Radio", audioSource.getArtist());
    }

    @Test
    void testMetaDataWav() throws Exception {
        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.wav").toUri());

        assertNotNull(audioSource);
        assertEquals(1411, audioSource.getBitRate());
        assertEquals(2, audioSource.getChannels());
        assertEquals("PT5.94S", audioSource.getDuration().toString());
        assertEquals("wav/pcm_s16le", audioSource.getFormat());
        assertEquals(44100, audioSource.getSamplingRate());
    }

    @Test
    void testMetaDataWma() throws Exception {
        final AudioSource audioSource = FFLocator.createFFprobe().getMetaData(Path.of("..", "samples", "sample.wma").toUri());

        assertNotNull(audioSource);
        assertEquals(128, audioSource.getBitRate());
        assertEquals(2, audioSource.getChannels());
        assertEquals("PT1M45.79S", audioSource.getDuration().toString());
        assertEquals("asf/wmav2", audioSource.getFormat());
        assertEquals(44100, audioSource.getSamplingRate());
    }
}
