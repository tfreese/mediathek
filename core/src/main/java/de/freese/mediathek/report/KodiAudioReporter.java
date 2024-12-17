// Created: 05.04.2020
package de.freese.mediathek.report;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import de.freese.mediathek.utils.MediaDbUtils;

/**
 * @author Thomas Freese
 */
public class KodiAudioReporter extends AbstractMediaReporter {
    @Override
    public void updateDbFromReport(final DataSource dataSource, final Path path) throws Exception {
        final String sqlSelect = """
                select
                    iTimesPlayed as playcount
                from
                    song
                where
                    strArtistDisp = ?
                    and strTitle = ?
                """;

        // sqlite does not support joins in updates.
        final String sqlUpdate = """
                UPDATE
                    song
                set
                    iTimesPlayed = ?
                WHERE
                    strArtistDisp = ?
                    AND strTitle = ?
                """;

        final List<Map<String, String>> heardMusic = readHeardMusik(path);

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement stmtUpdate = connection.prepareStatement(sqlUpdate);
                 PreparedStatement stmtSelect = connection.prepareStatement(sqlSelect)) {
                for (Map<String, String> map : heardMusic) {
                    final String artist = map.get("ARTIST");
                    final String song = map.get("SONG");
                    final int playCount = Integer.parseInt(map.get("PLAYCOUNT"));

                    stmtSelect.setString(1, artist);
                    stmtSelect.setString(2, song);

                    try (ResultSet resultSet = stmtSelect.executeQuery()) {
                        if (resultSet.next() && playCount != resultSet.getInt("PLAYCOUNT")) {
                            // Entry found -> Update
                            getLogger().info("Update Song: {} - {}", artist, song);

                            stmtUpdate.setInt(1, playCount);
                            stmtUpdate.setString(2, artist);
                            stmtUpdate.setString(3, song);

                            stmtUpdate.executeUpdate();
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
        writeMusic(dataSource, path);

        // Nur mit expliziter Tabelle möglich: tommy.playlist_music_artist
        // writeMusicPlaylistM3U(dataSource, path.resolve("Musik.m3u"));
        // writeMusicPlaylistXSP(dataSource, path.resolve("Musik.xsp"));
    }

    protected void writeMusic(final DataSource dataSource, final Path path) throws Exception {
        final String sql = """
                SELECT
                    strArtists AS artist,
                    strTitle AS song,
                    iTimesPlayed AS playcount
                FROM
                    songView
                WHERE
                    iTimesPlayed > 0
                ORDER BY artist asc, song asc
                """;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            writeResultSet(resultSet, path);
        }
    }

    /**
     * Erzeugt eine allgemeine M3U-Playlist.<br>
     */
    protected void writeMusicPlaylistM3U(final DataSource dataSource, final Path path) throws Exception {
        MediaDbUtils.rename(path);

        final String sql = """
                SELECT DISTINCT
                    sw.strArtists AS artist,
                    sw.strAlbum AS album,
                    sw.strTitle AS song,
                    sw.strFileName AS filename,
                    sw.strPath AS path,
                    sw.iDuration AS duration
                FROM
                    songview sw
                inner join tommy.playlist_music_artist pl on
                    (sw.strArtists = pl.artist and pl.operator = 'is') or (sw.strArtists like concat('%',pl.artist,'%') and pl.operator = 'contains')
                ORDER BY artist, album, song, duration
                """;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql);
             PrintWriter pw = new PrintWriter(Files.newOutputStream(path), true, StandardCharsets.UTF_8)) {
            pw.println("#EXTM3U");

            // #EXTINF:0,AC/DC - Baby, Please Don't Go
            // /mnt/mediathek/musik/AC_DC/Unbekanntes Album/AC_DC (Unbekanntes Album) - Baby, Please Don't Go.mp3
            while (resultSet.next()) {
                // pw.printf("#EXTINF:-1,%s - %s%n", resultSet.getInt("duration"), resultSet.getString("artist"), resultSet.getString("song"));
                pw.printf("#EXTINF:%d,%s - %s%n", resultSet.getInt("duration"), resultSet.getString("artist"), resultSet.getString("song"));
                pw.printf("%s%s%n", resultSet.getString("path"), resultSet.getString("filename"));
            }
        }
    }

    /**
     * Erzeugt eine Smart-Playlist.<br>
     */
    protected void writeMusicPlaylistXSP(final DataSource dataSource, final Path path) throws Exception {
        MediaDbUtils.rename(path);

        final String sql = """
                SELECT
                    artist,
                    operator
                FROM
                    tommy.playlist_music_artist
                ORDER BY operator, artist
                """;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql);
             PrintWriter pw = new PrintWriter(Files.newOutputStream(path), true, StandardCharsets.UTF_8)) {
            pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>");
            pw.println("<smartplaylist type=\"songs\">");
            pw.println("    <name>Mix</name>");
            pw.println("    <match>one</match>");

            while (resultSet.next()) {
                pw.printf("    <rule field=\"artist\" operator=\"%s\">%n", resultSet.getString("operator"));
                pw.printf("        <value>%s</value>%n", resultSet.getString("artist"));
                pw.println("    </rule>");
            }

            pw.println("    <limit>250</limit>");
            pw.println("    <order direction=\"ascending\">random</order>");
            pw.println("</smartplaylist>");
        }
    }
}
