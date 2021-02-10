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
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;
import de.freese.mediathek.report.AbstractMediaReporter;

/**
 * @author Thomas Freese
 */
public class PlexMediaReporter extends AbstractMediaReporter
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
        // dataSource.setUrl("jdbc:sqlite:/var/lib/plex/Plex\\ Media\\ Server/Plug-in\\ Support/Databases/com.plexapp.plugins.library.db");
        // dataSource.setUrl("jdbc:sqlite:/opt/plexmediaserver/Resources/com.plexapp.plugins.library.db");
        // dataSource.setUrl("jdbc:sqlite:/home/tommy/.config/plex/com.plexapp.plugins.library.db");
        dataSource.setUrl("jdbc:sqlite:/home/tommy/com.plexapp.plugins.library.db");

        // Export View-Status: echo ".dump metadata_item_settings" | sqlite3 com.plexapp.plugins.library.db | grep -v TABLE | grep -v INDEX > settings.sql
        // Import View-Status: cat settings.sql | sqlite3 com.plexapp.plugins.library.db

        // select guid from metadata_items where title = '<SONG>' and original_title = '<ARTIST>';
        // select * from metadata_item_settings where guid = 'local://17120';

        return dataSource;
    }

    /**
     * @see de.freese.mediathek.report.MediaReporter#updateDbFromReport(javax.sql.DataSource, java.nio.file.Path)
     */
    @Override
    public void updateDbFromReport(final DataSource dataSource, final Path path) throws Exception
    {
        // ZoneId zoneId = ZoneId.of("Europe/Berlin");
        // ZoneOffset zoneOffset = ZoneOffset.ofHours(+1);

        StringBuilder sql = new StringBuilder();
        sql.append("update metadata_item_settings set view_count = ?"); // , last_viewed_at = ?
        sql.append(" where guid = (select guid from metadata_items where original_title = ? and title = ?)");

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
                    pstmt.setString(3, artist);
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
    }

    /**
     * @see de.freese.mediathek.report.MediaReporter#writeReport(javax.sql.DataSource, java.nio.file.Path)
     */
    @Override
    public void writeReport(final DataSource dataSource, final Path path) throws Exception
    {
        throw new UnsupportedOperationException("writeReport not implemented");
    }
}
