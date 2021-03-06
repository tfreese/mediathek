/**
 * Created: 05.04.2020
 */

package de.freese.mediathek.musik;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;
import de.freese.mediathek.report.AbstractMediaReporter;

/**
 * @author Thomas Freese
 */
public class ClementineMediaReporter extends AbstractMediaReporter
{
    /**
     * @see de.freese.mediathek.report.MediaReporter#createDataSource(boolean)
     */
    @Override
    public DataSource createDataSource(final boolean readonly) throws Exception
    {
        // Native Libraries deaktivieren für den Zugriff auf die Dateien.
        System.setProperty("sqlite.purejava", "true");

        // Pfade für native Libraries.
        // System.setProperty("org.sqlite.lib.path", "/home/tommy");
        // System.setProperty("org.sqlite.lib.name", "sqlite-libsqlitejdbc.so");

        // DriverManager.setLogWriter(new PrintWriter(System.out, true));

        SQLiteConfig config = new SQLiteConfig();
        config.setReadOnly(readonly);
        config.setReadUncommited(true);

        SQLiteDataSource dataSource = new SQLiteConnectionPoolDataSource(config);
        dataSource.setUrl("jdbc:sqlite:/home/tommy/.config/Clementine/clementine.db");

        return dataSource;
    }

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

        List<Map<String, Object>> hearedMusic = readMusik(path);

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
        // select distinct filetype from songs;
        // select count(*), filetype from songs group by filetype;
        // select filename from songs where filetype = 5;
        // select ARTIST, TITLE as SONG, PLAYCOUNT, FILETYPE from songs where PLAYCOUNT = 0 order by ARTIST asc, SONG asc;

        StringBuilder sql = new StringBuilder();
        sql.append("select ARTIST, TITLE as SONG, PLAYCOUNT from songs");
        sql.append(" where PLAYCOUNT > 0");
        sql.append(" order by ARTIST asc, SONG asc");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.query(sql.toString(), resultSet -> {
            writeResultSet(resultSet, path);

            return null;
        });
    }
}
