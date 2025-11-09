// Created: 05.04.2020
package de.freese.mediathek.report;

import java.nio.file.Path;
import java.sql.ResultSet;
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
     * <li>ARTIST</li>
     * <li>SONG</li>
     * <li>PLAYCOUNT</li>
     * </ul>
     */
    protected List<Map<String, String>> readHeardMusik(final Path path) throws Exception {
        return MediaDbUtils.readCsv(path);
    }

    /**
     * Map-Keys:
     * <ul>
     * <li>MOVIE</li>
     * <li>PLAYCOUNT</li>
     * <li>LASTPLAYED</li>
     * </ul>
     */
    protected List<Map<String, String>> readSeenMovies(final Path path) throws Exception {
        return MediaDbUtils.readCsv(path);
    }

    /**
     * Map-Keys:
     * <ul>
     * <li>TVSHOW</li>
     * <li>SEASON</li>
     * <li>EPISODE</li>
     * <li>TITLE</li>
     * <li>PLAYCOUNT</li>
     * <li>LASTPLAYED</li>
     * </ul>
     */
    protected List<Map<String, String>> readSeenTvShows(final Path path) throws Exception {
        return MediaDbUtils.readCsv(path);
    }

    protected void writeResultSet(final ResultSet resultSet, final Path path) {
        try {
            MediaDbUtils.rename(path);
            MediaDbUtils.writeCsv(resultSet, path);
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
