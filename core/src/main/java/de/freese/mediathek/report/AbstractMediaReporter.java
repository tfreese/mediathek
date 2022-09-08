// Created: 05.04.2020
package de.freese.mediathek.report;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.freese.mediathek.utils.MediaDBUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractMediaReporter implements MediaReporter
{
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return logger;
    }

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
     * @throws IOException Falls was schiefgeht.
     */
    protected List<Map<String, String>> readMovies(final Path path) throws IOException
    {
        return MediaDBUtils.parseCsv(path).stream()
                .skip(1)// Header überspringen
                .map(row ->
                {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("MOVIE", row[0]);
                    map.put("PLAYCOUNT", row[1]);
                    map.put("LASTPLAYED", row[2]);

                    return map;
                }).toList();
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
     * @throws IOException Falls was schiefgeht.
     */
    protected List<Map<String, String>> readMusik(final Path path) throws IOException
    {
        return MediaDBUtils.parseCsv(path).stream()
                .skip(1)// Header überspringen
                .map(row ->
                {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("ARTIST", row[0]);
                    map.put("SONG", row[1]);
                    map.put("PLAYCOUNT", row[2]);

                    return map;
                }).toList();
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
     * @throws IOException Falls was schiefgeht.
     */
    protected List<Map<String, String>> readTVShows(final Path path) throws IOException
    {
        return MediaDBUtils.parseCsv(path).stream()
                .skip(1)// Header überspringen
                .map(row ->
                {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("TVSHOW", row[0]);
                    map.put("SEASON", row[1]);
                    map.put("EPISODE", row[2]);
                    map.put("TITLE", row[3]);
                    map.put("PLAYCOUNT", row[4]);
                    map.put("LASTPLAYED", row[5]);

                    return map;
                }).toList();
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
            MediaDBUtils.writeCsv(resultSet, path);
        }
        catch (Exception ex)
        {
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
