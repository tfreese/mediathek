// Created: 22.05.2016
package de.freese.mediathek;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.freese.mediathek.utils.MediaDBUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled
class TestParseCSV
{
    /**
     * @throws IOException Falls was schief geht
     */
    @Test
    void testParseClementine() throws IOException
    {
        Path path = Paths.get("/home/tommy/dokumente/linux/musik-report-clementine.csv");

        List<String> list = MediaDBUtils.parseCsv(path).stream().limit(3).map(Arrays::toString).toList();

        assertFalse(list.isEmpty());
        assertEquals(3, list.size());

        list.forEach(System.out::println);
    }

    /**
     *
     */
    @Test
    void testParseCsvRow()
    {
        String line = "\"\"\"A\"\" b\",\"c\",\"d\",,\"e\",\"\",\"f\"";

        String row = line;
        List<String> token = new ArrayList<>();

        while (!row.isBlank())
        {
            // Leerer Wert
            if (row.startsWith(","))
            {
                token.add("");
                row = row.substring(1);
                continue;
            }

            int endIndex = row.indexOf("\",");

            if (endIndex < 0)
            {
                // Letzter Wert -> Ende
                token.add(row);
                break;
            }

            token.add(row.substring(0, endIndex + 1));
            row = row.substring(endIndex + 2);
        }

        assertFalse(token.isEmpty());
        assertEquals(7, token.size());

        token = token.stream()
                .map(t -> t.replaceAll("^\"|\"$", "")) // Erstes und letztes '"' entfernen
                .map(l -> l.replace("\"\"", "\"")) // Escapte Anführungszeichen ersetzen: "" -> "
                .map(String::strip)
                .toList()
        ;
        System.out.println(token);
    }
}
