/**
 * Created: 22.05.2016
 */
package de.freese.mediathek;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
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
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testReadClementineMusik() throws Exception
    {
        // @formatter:off
        Files.lines(Paths.get("/home/tommy/dokumente/linux/musik-playcount.csv"))
            .peek(System.out::println)
            .map(l -> l.replaceAll("^\"|\"$", "")) // Erstes und letztes " entfernen
            .peek(System.out::println)
            .map(l -> l.replaceAll("\";\"", ";")) // ";"  durch ; ersetzen
            .peek(System.out::println)
            .map(l -> l.split("[;]"))
            .forEach(ar -> System.out.println(Arrays.toString(ar)))
            ;
        // @formatter:on

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testReadKodiFilme() throws Exception
    {
        // @formatter:off
        Files.lines(Paths.get("/home/tommy/dokumente/kodi/playcount-report-filme.csv"))
            .peek(System.out::println)
            .map(l -> l.replaceAll("^\"|\"$", "")) // Erstes und letztes " entfernen
            .peek(System.out::println)
            .map(l -> l.replaceAll("\";\"", ";")) // ";"  durch ; ersetzen
            .peek(System.out::println)
            .map(l -> l.split("[;]"))
            .forEach(ar -> System.out.println(Arrays.toString(ar)))
            ;
        // @formatter:on

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testReadKodiMusik() throws Exception
    {
        // @formatter:off
        Files.lines(Paths.get("/home/tommy/dokumente/kodi/playcount-report-musik.csv"))
            .peek(System.out::println)
            .map(l -> l.replaceAll("^\"|\"$", "")) // Erstes und letztes " entfernen
            .peek(System.out::println)
            .map(l -> l.replaceAll("\";\"", ";")) // ";"  durch ; ersetzen
            .peek(System.out::println)
            .map(l -> l.split("[;]"))
            .forEach(ar -> System.out.println(Arrays.toString(ar)))
            ;
        // @formatter:on

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testReadKodiSerien() throws Exception
    {
        // @formatter:off
        Files.lines(Paths.get("/home/tommy/dokumente/kodi/playcount-report-serien.csv"))
            .peek(System.out::println)
            .skip(1) // Header überspringen
            .map(l -> l.replaceAll("^\"|\"$", "")) // Erstes und letztes " entfernen
            .peek(System.out::println)
            .map(l -> l.replaceAll("\";\"", ";")) // ";"  durch ; ersetzen
            .peek(System.out::println)
            .map(l -> l.split("[;]"))
            .peek(array -> System.out.println(Arrays.toString(array)))
            .map(array -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("TVSHOW", array[0]);
                map.put("SEASON", Integer.parseInt(array[1]));
                map.put("EPISODE", Integer.parseInt(array[2]));
                map.put("TITLE", array[3]);
                map.put("PLAYCOUNT", Integer.parseInt(array[4]));
                map.put("LASTPLAYED", array[5]);

                return map;
            })
            .forEach(System.out::println)
            ;
        // @formatter:on

        assertTrue(true);
    }
}
