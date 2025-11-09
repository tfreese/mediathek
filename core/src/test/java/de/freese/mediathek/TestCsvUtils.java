// Created: 22.05.2016
package de.freese.mediathek;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.freese.mediathek.utils.MediaDbUtils;

/**
 * @author Thomas Freese
 */
class TestCsvUtils {
    @Test
    void testParseFile() throws Exception {
        final URL url = Thread.currentThread().getContextClassLoader().getResource("test.csv");
        assertNotNull(url);

        final Path path = Path.of(url.toURI());
        assertTrue(Files.exists(path));

        final List<Map<String, String>> list = MediaDbUtils.readCsv(path);
        assertFalse(list.isEmpty());
        assertEquals(2, list.size());

        // Check Header.
        assertEquals("A, B, C", String.join(", ", list.getFirst().keySet()));

        // Check Rows.
        assertEquals("\"a1\" a1", list.getFirst().get("A"));
        assertEquals("b1", list.getFirst().get("B"));
        assertEquals("c1", list.getFirst().get("C"));

        assertEquals("a2", list.get(1).get("A"));
        assertEquals("b2", list.get(1).get("B"));
        assertEquals("c2", list.get(1).get("C"));
    }
}
