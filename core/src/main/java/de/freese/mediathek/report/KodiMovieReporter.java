// Created: 05.04.2020
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
 */
public class KodiMovieReporter extends AbstractMediaReporter {
    @Override
    public void updateDbFromReport(final DataSource dataSource, final Path path) throws Exception {
        final StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append("select files.playcount, files.lastPlayed, files.idfile");
        sqlSelect.append(" from files");
        sqlSelect.append(" INNER JOIN movie ON movie.idfile = files.idfile");
        sqlSelect.append(" where movie.c00 = ?");

        final StringBuilder sqlUpdate = new StringBuilder();
        // mysql
        // sqlUpdate.append("UPDATE files");
        // sqlUpdate.append(" INNER JOIN movie ON movie.idfile = files.idfile");
        // sqlUpdate.append(" set files.playcount = ?, files.lastplayed = ?");
        // sqlUpdate.append(" where movie.c00 = ?");

        // sqlite unterstützt keine joins in updates.
        sqlUpdate.append("UPDATE files");
        sqlUpdate.append(" set playcount = ?, lastplayed = ?");
        sqlUpdate.append(" where idfile = ?");

        final List<Map<String, String>> seenMovies = readSeenMovies(path);

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement stmtUpdate = connection.prepareStatement(sqlUpdate.toString());
                 PreparedStatement stmtSelect = connection.prepareStatement(sqlSelect.toString())) {
                for (Map<String, String> map : seenMovies) {
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
            catch (Exception ex) {
                connection.rollback();

                getLogger().error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void writeReport(final DataSource dataSource, final Path path) throws Exception {
        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT c00 AS movie, playcount, lastplayed");
        sql.append(" FROM movie_view");
        sql.append(" WHERE playcount > 0");
        sql.append(" ORDER BY movie asc");

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql.toString())) {
            writeResultSet(resultSet, path);
        }
    }
}
