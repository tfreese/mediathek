package de.freese.mediathek.report;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * @author Thomas Freese
 * @since 05.04.2020
 */
public class KodiMovieReporter extends AbstractMediaReporter {
    public KodiMovieReporter(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void updateDbFromReport(final Path path) throws Exception {
        final String sqlSelect = """
                select
                    files.playcount,
                    files.lastPlayed,
                    files.idfile
                from
                    files
                INNER JOIN movie ON movie.idfile = files.idfile
                where
                    movie.c00 = ?
                """;

        // sqlite does not support joins in updates.
        final String sqlUpdate = """
                UPDATE
                    files
                set
                    playcount = ?,
                    lastplayed = ?
                where
                    idfile = ?
                """;

        // mysql:
        // UPDATE files
        // INNER JOIN movie ON movie.idfile = files.idfile
        // set files.playcount = ?, files.lastplayed = ?
        // where movie.c00 = ?

        final List<Map<String, String>> seenMovies = readSeenMovies(path);

        try (Connection connection = getDataSource().getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement stmtUpdate = connection.prepareStatement(sqlUpdate);
                 PreparedStatement stmtSelect = connection.prepareStatement(sqlSelect)) {
                for (final Map<String, String> map : seenMovies) {
                    final String movie = map.get("MOVIE");
                    final int playCount = Integer.parseInt(map.get("PLAYCOUNT"));
                    final String lastPlayed = map.get("LASTPLAYED");

                    stmtSelect.setString(1, movie);

                    try (ResultSet resultSet = stmtSelect.executeQuery()) {
                        if (resultSet.next()) {
                            // Eintrag gefunden -> Update
                            if (playCount != resultSet.getInt("PLAYCOUNT") || !lastPlayed.equals(resultSet.getString("LASTPLAYED"))) {
                                final int idFile = resultSet.getInt("IDFILE");

                                getLogger().info("Update Movie: IDFile={}, {}", idFile, movie);

                                stmtUpdate.setInt(1, playCount);
                                stmtUpdate.setString(2, lastPlayed);
                                stmtUpdate.setInt(3, idFile);

                                stmtUpdate.executeUpdate();
                            }
                        }
                    }
                }

                connection.commit();
            }
            catch (final Exception ex) {
                connection.rollback();

                getLogger().error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void writeReport(final Path path) throws Exception {
        final String sql = """
                SELECT
                    c00 AS movie,
                    playcount,
                    lastplayed
                FROM
                    movie_view
                WHERE
                    playcount > 0
                ORDER BY movie asc
                """;

        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            writeResultSet(resultSet, path);
        }
    }
}
