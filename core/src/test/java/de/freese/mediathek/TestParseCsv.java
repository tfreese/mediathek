// Created: 22.05.2016
package de.freese.mediathek;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.freese.mediathek.utils.MediaDbUtils;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestParseCsv {
    @Test
    void testParseCsvRow() {
        String row = "\"\"\"A\"\" b\",\"c\",\"d\",,\"e\",\"\",\"f\"";

        List<String> tokens = new ArrayList<>();

        while (!row.isBlank()) {
            // Empty Value
            if (row.startsWith(",")) {
                tokens.add("");
                row = row.substring(1);
                continue;
            }

            final int endIndex = row.indexOf("\",");

            if (endIndex < 0) {
                // Last Value -> End
                tokens.add(row);
                break;
            }

            tokens.add(row.substring(0, endIndex + 1));
            row = row.substring(endIndex + 2);
        }

        assertFalse(tokens.isEmpty());
        assertEquals(7, tokens.size());

        tokens = tokens.stream().map(t -> t.replaceAll("^\"|\"$", "")) // Remove first and last '"'
                .map(l -> l.replace("\"\"", "\"")) // Replace escaped quotes: "" -> "
                .map(String::strip).toList();

        assertFalse(tokens.isEmpty());
        assertEquals(7, tokens.size());
        assertEquals("\"A\" b,c,d,,e,,f", String.join(",", tokens));
    }

    @Test
    void testParseFile() throws IOException {
        final Path path = Paths.get("/home/tommy/dokumente/linux/musik-report-strawberry.csv");

        if (!Files.exists(path)) {
            return;
        }

        final List<String> list = MediaDbUtils.parseCsv(path).stream().limit(3).map(Arrays::toString).toList();

        assertFalse(list.isEmpty());
        assertEquals(3, list.size());

        assertEquals("[ARTIST, SONG, PLAYCOUNT]", list.getFirst());
    }
}
