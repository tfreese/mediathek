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
public class ClementineAudioReporter extends AbstractMediaReporter {
    @Override
    public void updateDbFromReport(final DataSource dataSource, final Path path) throws Exception {
        // TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        // TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        // ZoneId zoneId = ZoneId.of("Europe/Berlin");
        // ZoneOffset zoneOffset = ZoneOffset.ofHours(+1);

        // , lastplayed = ?
        final String sql = """
                update
                    songs
                set
                    playcount = ?
                where
                    artist = ?
                    and title = ?
                """;

        final List<Map<String, String>> heardMusic = readHeardMusik(path);

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            con.setAutoCommit(false);

            try {
                for (Map<String, String> map : heardMusic) {
                    final String artist = map.get("ARTIST");
                    final String song = map.get("SONG");
                    final int playCount = Integer.parseInt(map.get("PLAYCOUNT"));
                    // final LocalDateTime lastPlayed = LocalDateTime.parse((String) map.get("LASTPLAYED"));

                    getLogger().info("Update Song: {} - {}", artist, song);

                    // pstmt.clearParameters();
                    pstmt.setInt(1, playCount);
                    // pstmt.setInt(2, Long.valueOf(lastPlayed.toInstant(zoneOffset).toEpochMilli()).intValue());
                    // pstmt.setTimestamp(2, Timestamp.from(lastPlayed));
                    pstmt.setString(2, artist);
                    pstmt.setString(3, song);
                    pstmt.addBatch();
                }

                final int[] affectedRows = pstmt.executeBatch();
                getLogger().info("Affected Rows: {}}", affectedRows.length);

                con.commit();
            }
            catch (Exception ex) {
                con.rollback();

                getLogger().error(ex.getMessage(), ex);
            }
        }

        // transactionManager.commit(transactionStatus);
        // transactionManager.rollback(transactionStatus);
    }

    @Override
    public void writeReport(final DataSource dataSource, final Path path) throws Exception {
        final String sql = """
                select
                    ARTIST,
                    TITLE as SONG,
                    PLAYCOUNT
                from
                    songs
                where
                    PLAYCOUNT > 0
                order by ARTIST asc, SONG asc
                """;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            writeResultSet(resultSet, path);
        }

        //        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        //
        //        jdbcTemplate.query(sql.toString(), resultSet -> {
        //            writeResultSet(resultSet, path);
        //
        //            return null;
        //        });
    }
}
