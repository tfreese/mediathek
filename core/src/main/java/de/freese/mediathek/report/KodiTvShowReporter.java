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
public class KodiTvShowReporter extends AbstractMediaReporter {
    @Override
    public void updateDbFromReport(final DataSource dataSource, final Path path) throws Exception {
        final StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append("select files.playcount, files.lastPlayed, files.idfile");
        sqlSelect.append(" from files");
        sqlSelect.append(" INNER JOIN episode ON episode.idfile = files.idfile");
        sqlSelect.append(" INNER JOIN tvshow ON tvshow.idshow = episode.idshow");
        sqlSelect.append(" where tvshow.c00 = ? and episode.c12 = ? and episode.c13 = ?");

        final StringBuilder sqlUpdate = new StringBuilder();
        // mysql
        // sqlUpdate.append("UPDATE files");
        // sqlUpdate.append(" INNER JOIN episode ON episode.idfile = files.idfile");
        // sqlUpdate.append(" INNER JOIN tvshow ON tvshow.idshow = episode.idshow");
        // sqlUpdate.append(" set files.playcount = ?, files.lastplayed = ?");
        // sqlUpdate.append(" where tvshow.c00 = ? and episode.c12 = ? and episode.c13 = ?"); // show, season, episode

        // sqlite unterstützt keine joins in updates.
        sqlUpdate.append("UPDATE files");
        sqlUpdate.append(" set playcount = ?, lastplayed = ?");
        sqlUpdate.append(" where idfile = ?");

        final List<Map<String, String>> seenTvShows = readSeenTvShows(path);

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement stmtUpdate = connection.prepareStatement(sqlUpdate.toString());
                 PreparedStatement stmtSelect = connection.prepareStatement(sqlSelect.toString())) {
                for (Map<String, String> map : seenTvShows) {
                    final String tvshow = map.get("TVSHOW");
                    final String season = map.get("SEASON");
                    final String episode = map.get("EPISODE");
                    final String title = map.get("TITLE");
                    final int playCount = Integer.parseInt(map.get("PLAYCOUNT"));
                    final String lastPlayed = map.get("LASTPLAYED");

                    stmtSelect.setString(1, tvshow);
                    stmtSelect.setString(2, season);
                    stmtSelect.setString(3, episode);

                    try (ResultSet resultSet = stmtSelect.executeQuery()) {
                        if (resultSet.next()) {
                            // Eintrag gefunden -> Update
                            if ((playCount != resultSet.getInt("PLAYCOUNT")) || !lastPlayed.equals(resultSet.getString("LASTPLAYED"))) {
                                final int idFile = resultSet.getInt("IDFILE");

                                getLogger().info(
                                        String.format("Update TvShow: IDFile=%d, %s - S%02dE%02d - %s%n", idFile, tvshow, Integer.parseInt(season), Integer.parseInt(episode),
                                                title));

                                stmtUpdate.setInt(1, playCount);
                                stmtUpdate.setString(2, lastPlayed);
                                stmtUpdate.setInt(3, playCount);

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
        sql.append("SELECT strTitle AS tvshow, c12 AS season, c13 AS episode, c00 AS title, playcount, lastplayed");
        sql.append(" FROM episode_view");
        sql.append(" WHERE playcount > 0");
        sql.append(" ORDER BY tvshow asc, CAST(season AS UNSIGNED) asc, CAST(episode AS UNSIGNED) asc");

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql.toString())) {
            writeResultSet(resultSet, path);
        }
    }
}
