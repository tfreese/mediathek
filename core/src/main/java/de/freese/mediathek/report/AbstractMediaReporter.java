// Created: 05.04.2020
package de.freese.mediathek.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import de.freese.mediathek.utils.MediaDBUtils;

/**
 * @author Thomas Freese
 */
public abstract class AbstractMediaReporter implements MediaReporter
{
    /**
     * Auslesen der bereits geschauter Filme-Liste.<br>
     * Map-Keys:
     * <ul>
     * <li>MOVIE
     * <li>PLAYCOUNT
     * <li>LASTPLAYED
     * </ul>
     *
     * @param path {@link Path}
     *
     * @return {@link List}
     *
     * @throws IOException Falls was schief geht.
     */
    protected List<Map<String, Object>> readMovies(final Path path) throws IOException
    {
        List<Map<String, Object>> list = null;

        try (Stream<String> stream = Files.lines(path))
        {
            // @formatter:off
            list = stream
                    .skip(1) // Header überspringen
                    .map(l -> l.replaceAll("^\"|\"$", "")) // Erstes und letztes " entfernen
                    .map(l -> l.replaceAll("\";\"", ";")) // ";"  durch ; ersetzen
                    .map(l -> l.replace("\"\"", "\"")) // Escapte Anführungszeichen ersetzen: "" -> "
                    .map(l -> l.split("[;]"))
                    .map(array -> {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("MOVIE", array[0]);
                        map.put("PLAYCOUNT", array[1]);
                        map.put("LASTPLAYED", array[2]);

                        return map;
                    })
                    .toList()
                    ;
            // @formatter:on
        }

        return list;
    }

    /**
     * Auslesen der bereits gehörter Musik-Liste.<br>
     * Map-Keys:
     * <ul>
     * <li>ARTIST
     * <li>SONG
     * <li>PLAYCOUNT
     * </ul>
     *
     * @param path {@link Path}
     *
     * @return {@link List}
     *
     * @throws IOException Falls was schief geht.
     */
    protected List<Map<String, Object>> readMusik(final Path path) throws IOException
    {
        List<Map<String, Object>> list = null;

        try (Stream<String> stream = Files.lines(path))
        {
            // @formatter:off
            list = stream
                    .skip(1) // Header überspringen
                    .map(l -> l.replaceAll("^\"|\"$", "")) // Erstes und letztes " entfernen
                    .map(l -> l.replaceAll("\";\"", ";")) // ";"  durch ; ersetzen
                    .map(l -> l.replace("\"\"", "\"")) // Escapte Anführungszeichen ersetzen: "" -> "
                    .map(l -> l.split("[;]"))
                    .map(array -> {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("ARTIST", array[0]);
                        map.put("SONG", array[1]);
                        map.put("PLAYCOUNT", array[2]);

                        return map;
                    })
                    .toList()
                    ;
            // @formatter:on
        }

        return list;
    }

    /**
     * Auslesen der bereits geschauten Serien-Liste.<br>
     * Map-Keys:
     * <ul>
     * <li>TVSHOW
     * <li>SEASON
     * <li>EPISODE
     * <li>TITLE
     * <li>PLAYCOUNT
     * <li>LASTPLAYED
     * </ul>
     *
     * @param path {@link Path}
     *
     * @return {@link List}
     *
     * @throws IOException Falls was schief geht.
     */
    protected List<Map<String, Object>> readTVShows(final Path path) throws IOException
    {
        List<Map<String, Object>> list = null;

        try (Stream<String> stream = Files.lines(path))
        {
            // @formatter:off
            list = stream
                    //.peek(System.out::println)
                    .skip(1) // Header überspringen
                    .map(l -> l.replaceAll("^\"|\"$", "")) // Erstes und letztes " entfernen
                    .map(l -> l.replaceAll("\";\"", ";")) // ";"  durch ; ersetzen
                    .map(l -> l.replace("\"\"", "\"")) // Escapte Anführungszeichen ersetzen: "" -> "
                    .map(l -> l.split("[;]"))
                    .map(array -> {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("TVSHOW", array[0]);
                        map.put("SEASON", array[1]);
                        map.put("EPISODE", array[2]);
                        map.put("TITLE", array[3]);
                        map.put("PLAYCOUNT", array[4]);
                        map.put("LASTPLAYED", array[5]);

                        return map;
                    })
                    .toList()
                    ;
            // @formatter:on
        }

        return list;
    }

    /**
     * Schreibt das ResultSet als CSV-Datei.<br>
     * Wenn das ResultSet einen Typ != ResultSet.TYPE_FORWARD_ONLY besitzt, wird {@link ResultSet#first()} aufgerufen und kann weiter verwendet werden.
     *
     * @param resultSet {@link ResultSet}
     * @param path {@link Path}
     */
    protected void writeResultSet(final ResultSet resultSet, final Path path)
    {
        try
        {
            MediaDBUtils.writeCSV(resultSet, path);
        }
        catch (Exception ex)
        {
            System.err.println(ex);
        }
    }
}
