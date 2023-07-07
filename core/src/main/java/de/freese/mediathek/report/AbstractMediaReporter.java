// Created: 05.04.2020
package de.freese.mediathek.report;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.mediathek.utils.MediaDbUtils;

/**
 * @author Thomas Freese
 */
public abstract class AbstractMediaReporter implements MediaReporter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected Logger getLogger() {
        return logger;
    }

    /**
     * Map-Keys:
     * <ul>
     * <li>ARTIST
     * <li>SONG
     * <li>PLAYCOUNT
     * </ul>
     */
    protected List<Map<String, String>> readHeardMusik(final Path path) throws IOException {
        return MediaDbUtils.parseCsv(path).stream().skip(1)// Header überspringen
                .map(row -> {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("ARTIST", row[0]);
                    map.put("SONG", row[1]);
                    map.put("PLAYCOUNT", row[2]);

                    return map;
                }).toList();
    }

    /**
     * Map-Keys:
     * <ul>
     * <li>MOVIE
     * <li>PLAYCOUNT
     * <li>LASTPLAYED
     * </ul>
     */
    protected List<Map<String, String>> readSeenMovies(final Path path) throws IOException {
        return MediaDbUtils.parseCsv(path).stream().skip(1)// Header überspringen
                .map(row -> {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("MOVIE", row[0]);
                    map.put("PLAYCOUNT", row[1]);
                    map.put("LASTPLAYED", row[2]);

                    return map;
                }).toList();
    }

    /**
     * Map-Keys:
     * <ul>
     * <li>TVSHOW
     * <li>SEASON
     * <li>EPISODE
     * <li>TITLE
     * <li>PLAYCOUNT
     * <li>LASTPLAYED
     * </ul>
     */
    protected List<Map<String, String>> readSeenTvShows(final Path path) throws IOException {
        return MediaDbUtils.parseCsv(path).stream().skip(1)// Header überspringen
                .map(row -> {
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

    protected void writeResultSet(final ResultSet resultSet, final Path path) {
        try {
            MediaDbUtils.writeCsv(resultSet, path);
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
