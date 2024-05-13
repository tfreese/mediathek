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
public class PlexAudioReporter extends AbstractMediaReporter {
    @Override
    public void updateDbFromReport(final DataSource dataSource, final Path path) throws Exception {
        // ZoneId zoneId = ZoneId.of("Europe/Berlin");
        // ZoneOffset zoneOffset = ZoneOffset.ofHours(+1);

        final String sql = """
                update
                    metadata_item_settings
                set
                    view_count = ?
                where
                    guid = (select guid from metadata_items where original_title = ? and title = ?)
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

                    getLogger().info("Update Song: {} - {}", artist, song);

                    // pstmt.clearParameters();
                    pstmt.setInt(1, playCount);
                    pstmt.setString(2, artist);
                    pstmt.setString(3, song);
                    pstmt.addBatch();
                }

                final int[] affectedRows = pstmt.executeBatch();
                getLogger().info("Affected Rows: {}", affectedRows.length);

                con.commit();
            }
            catch (Exception ex) {
                con.rollback();

                getLogger().error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void writeReport(final DataSource dataSource, final Path path) throws Exception {
        throw new UnsupportedOperationException("writeReport not implemented");
    }
}
