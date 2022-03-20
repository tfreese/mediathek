// Created: 05.04.2020
package de.freese.mediathek.report;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * @author Thomas Freese
 */
public class PlexAudioReporter extends AbstractMediaReporter
{
    /**
     * @see de.freese.mediathek.report.MediaReporter#updateDbFromReport(javax.sql.DataSource, java.nio.file.Path)
     */
    @Override
    public void updateDbFromReport(final DataSource dataSource, final Path path) throws Exception
    {
        // ZoneId zoneId = ZoneId.of("Europe/Berlin");
        // ZoneOffset zoneOffset = ZoneOffset.ofHours(+1);

        StringBuilder sql = new StringBuilder();
        sql.append("update metadata_item_settings");
        sql.append(" set view_count = ?");
        sql.append(" where guid = (select guid from metadata_items where original_title = ? and title = ?)");

        List<Map<String, String>> hearedMusic = readMusik(path.resolve("musik-report-plex.csv"));

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql.toString()))
        {
            con.setAutoCommit(false);

            try
            {
                for (Map<String, String> map : hearedMusic)
                {
                    String artist = map.get("ARTIST");
                    String song = map.get("SONG");
                    int playcount = Integer.parseInt(map.get("PLAYCOUNT"));

                    System.out.printf("Update Song: %s - %s%n", artist, song);

                    // pstmt.clearParameters();
                    pstmt.setInt(1, playcount);
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
    }

    /**
     * @see de.freese.mediathek.report.MediaReporter#writeReport(javax.sql.DataSource, java.nio.file.Path)
     */
    @Override
    public void writeReport(final DataSource dataSource, final Path path) throws Exception
    {
        // path.resolve("musik-report-plex.csv")
        throw new UnsupportedOperationException("writeReport not implemented");
    }
}
