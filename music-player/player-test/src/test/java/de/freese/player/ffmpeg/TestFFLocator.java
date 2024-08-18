// Created: 15 Juli 2024
package de.freese.player.ffmpeg;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * @author Thomas Freese
 */
class TestFFLocator {
    @Test
    void testFFmepgVersion() throws Exception {
        final String version = FFLocator.createFFmpeg().getVersion();

        assertNotNull(version);
        assertFalse(version.isBlank());
    }

    @Test
    void testFFprobeVersion() throws Exception {
        final String version = FFLocator.createFFprobe().getVersion();

        assertNotNull(version);
        assertFalse(version.isBlank());
    }
}
