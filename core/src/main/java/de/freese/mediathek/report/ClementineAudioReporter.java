// Created: 05.04.2020
package de.freese.mediathek.report;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Thomas Freese
 */
public class ClementineAudioReporter extends AbstractMediaReporter
{
    /**
     * @see de.freese.mediathek.report.MediaReporter#updateDbFromReport(javax.sql.DataSource, java.nio.file.Path)
     */
    @Override
    public void updateDbFromReport(final DataSource dataSource, final Path path) throws Exception
    {
        // TODO
        // TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        // TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        // ZoneId zoneId = ZoneId.of("Europe/Berlin");
        // ZoneOffset zoneOffset = ZoneOffset.ofHours(+1);

        StringBuilder sql = new StringBuilder();
        sql.append("update songs set playcount = ?"); // , lastplayed = ?
        sql.append(" where artist = ? and title = ?");

        List<Map<String, Object>> hearedMusic = readMusik(path.resolve("musik-report-clementine.csv"));

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql.toString()))
        {
            con.setAutoCommit(false);

            try
            {
                for (Map<String, Object> map : hearedMusic)
                {
                    String artist = (String) map.get("ARTIST");
                    String song = (String) map.get("SONG");
                    int playcount = Integer.parseInt((String) map.get("PLAYCOUNT"));
                    // LocalDateTime lastPlayed = LocalDateTime.parse((String) map.get("LASTPLAYED"));

                    System.out.printf("Update Song: %s - %s%n", artist, song);

                    // pstmt.clearParameters();
                    pstmt.setInt(1, playcount);
                    // pstmt.setInt(2, Long.valueOf(lastPlayed.toInstant(zoneOffset).toEpochMilli()).intValue());
                    // pstmt.setTimestamp(2, Timestamp.from(lastPlayed));
                    pstmt.setString(2, artist);
                    pstmt.setString(3, song);
                    pstmt.addBatch();
                }

                int[] affectedRows = pstmt.executeBatch();
                System.out.printf("%nAffected Rows: %d%n", affectedRows.length);

                con.commit();
            }
            catch (Exception ex)
            {
                con.rollback();
                ex.printStackTrace();
            }
        }

        // transactionManager.commit(transactionStatus);
        // transactionManager.rollback(transactionStatus);
    }

    /**
     * @see de.freese.mediathek.report.MediaReporter#writeReport(javax.sql.DataSource, java.nio.file.Path)
     */
    @Override
    public void writeReport(final DataSource dataSource, final Path path) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select ARTIST, TITLE as SONG, PLAYCOUNT");
        sql.append(" from songs");
        sql.append(" where PLAYCOUNT > 0");
        sql.append(" order by ARTIST asc, SONG asc");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.query(sql.toString(), resultSet -> {
            writeResultSet(resultSet, path.resolve("musik-report-clementine.csv"));

            return null;
        });
    }
}