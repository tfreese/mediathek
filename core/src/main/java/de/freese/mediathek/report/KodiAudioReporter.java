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
public class KodiAudioReporter extends AbstractMediaReporter
{
    /**
     * @see de.freese.mediathek.report.MediaReporter#updateDbFromReport(javax.sql.DataSource, java.nio.file.Path)
     */
    @Override
    public void updateDbFromReport(final DataSource dataSource, final Path path) throws Exception
    {
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append("select iTimesPlayed as playcount");
        sqlSelect.append(" from song");
        sqlSelect.append(" where strArtistDisp = ? and strTitle = ?");

        StringBuilder sqlUpdate = new StringBuilder();
        sqlUpdate.append("UPDATE song");
        sqlUpdate.append(" set iTimesPlayed = ?");
        sqlUpdate.append(" WHERE strArtistDisp = ? AND strTitle = ?");

        List<Map<String, String>> hearedMusic = readMusik(path.resolve("musik-report-kodi.csv"));

        try (Connection connection = dataSource.getConnection())
        {
            connection.setAutoCommit(false);

            try (PreparedStatement stmtUpdate = connection.prepareStatement(sqlUpdate.toString());
                 PreparedStatement stmtSelect = connection.prepareStatement(sqlSelect.toString()))
            {
                for (Map<String, String> map : hearedMusic)
                {
                    String artist = map.get("ARTIST");
                    String song = map.get("SONG");
                    int playCount = Integer.parseInt(map.get("PLAYCOUNT"));

                    stmtSelect.setString(1, artist);
                    stmtSelect.setString(2, song);

                    try (ResultSet resultSet = stmtSelect.executeQuery())
                    {
                        if (resultSet.next())
                        {
                            // Eintrag gefunden -> Update
                            if (playCount != resultSet.getInt("PLAYCOUNT"))
                            {
                                getLogger().info("Update Song: {} - {}", artist, song);

                                stmtUpdate.setInt(1, playCount);
                                stmtUpdate.setString(2, artist);
                                stmtUpdate.setString(3, song);

                                stmtUpdate.executeUpdate();
                            }
                        }
                    }
                }

                connection.commit();
            }
            catch (Exception ex)
            {
                connection.rollback();

                getLogger().error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * @see de.freese.mediathek.report.MediaReporter#writeReport(javax.sql.DataSource, java.nio.file.Path)
     */
    @Override
    public void writeReport(final DataSource dataSource, final Path path) throws Exception
    {
        writeMusic(dataSource, path.resolve("musik-report-kodi.csv"));

        // Playlisten
        // Nur mit expliziter Tabelle möglich: tommy.playlist_music_artist
        // writeMusicPlaylistM3U(dataSource, path.resolve("Musik.m3u"));
        // writeMusicPlaylistXSP(dataSource, path.resolve("Musik.xsp"));
    }

    protected void writeMusic(final DataSource dataSource, final Path path) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT strArtists AS artist, strTitle AS song, iTimesPlayed AS playcount");
        sql.append(" FROM songView");
        sql.append(" WHERE iTimesPlayed > 0");
        sql.append(" ORDER BY artist asc, song asc");

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql.toString()))
        {
            writeResultSet(resultSet, path);
        }
    }

    /**
     * Erzeugt eine allgemeine M3U-Playlist.<br>
     */
    protected void writeMusicPlaylistM3U(final DataSource dataSource, final Path path) throws Exception
    {
        MediaDbUtils.rename(path);

        StringBuilder sql = new StringBuilder();
        sql.append(
                "SELECT DISTINCT sw.strArtists AS artist, sw.strAlbum AS album, sw.strTitle AS song, sw.strFileName AS filename, sw.strPath AS path, sw.iDuration AS duration");
        sql.append(" FROM songview sw");
        sql.append(" inner join tommy.playlist_music_artist pl on");
        sql.append(" (sw.strArtists = pl.artist and pl.operator = 'is') or (sw.strArtists like concat('%',pl.artist,'%') and pl.operator = 'contains')");
        sql.append(" ORDER BY artist, album, song, duration");

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql.toString());
             PrintWriter pw = new PrintWriter(Files.newOutputStream(path), true, StandardCharsets.UTF_8))
        {
            pw.println("#EXTM3U");

            // #EXTINF:0,AC/DC - Baby, Please Don't Go
            // /mnt/mediathek/musik/AC_DC/Unbekanntes Album/AC_DC (Unbekanntes Album) - Baby, Please Don't Go.mp3
            while (resultSet.next())
            {
                // pw.printf("#EXTINF:-1,%s - %s%n", resultSet.getInt("duration"), resultSet.getString("artist"), resultSet.getString("song"));
                pw.printf("#EXTINF:%d,%s - %s%n", resultSet.getInt("duration"), resultSet.getString("artist"), resultSet.getString("song"));
                pw.printf("%s%s%n", resultSet.getString("path"), resultSet.getString("filename"));
            }
        }
    }

    /**
     * Erzeugt eine Smart-Playlist.<br>
     */
    protected void writeMusicPlaylistXSP(final DataSource dataSource, final Path path) throws Exception
    {
        MediaDbUtils.rename(path);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT artist, operator");
        sql.append(" FROM tommy.playlist_music_artist");
        sql.append(" ORDER BY operator, artist");

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql.toString());
             PrintWriter pw = new PrintWriter(Files.newOutputStream(path), true, StandardCharsets.UTF_8))
        {
            pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>");
            pw.println("<smartplaylist type=\"songs\">");
            pw.println("    <name>Mix</name>");
            pw.println("    <match>one</match>");

            while (resultSet.next())
            {
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
