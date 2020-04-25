/**
 * Created: 05.04.2020
 */

package de.freese.mediathek.kodi.report;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import de.freese.mediathek.kodi.spring.AbstractAppConfig;
import de.freese.mediathek.kodi.spring.AppConfigSQLite;
import de.freese.mediathek.report.AbstractMediaReporter;

/**
 * @author Thomas Freese
 */
public class KodiVideoReporter extends AbstractMediaReporter
{
    /**
     * Erstellt ein neues {@link KodiVideoReporter} Object.
     */
    public KodiVideoReporter()
    {
        super();
    }

    /**
     * @see de.freese.mediathek.report.MediaReporter#createDataSource(boolean)
     */
    @Override
    public DataSource createDataSource(final boolean readonly) throws Exception
    {
        ConfigurableEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addLast(new KodiPropertySource());

        AbstractAppConfig appConfig = new AppConfigSQLite();
        appConfig.setEnvironment(environment);

        DataSource dataSource = appConfig.dataSourceVideo();

        return dataSource;
    }

    /**
     * Erzeugt eine CSV-Datei bereits gesehener Filme.<br>
     * Siehe auch movieview.
     *
     * @param dataSource {@link DataSource}
     * @param path {@link Path}
     * @throws Exception Falls was schief geht.
     */
    protected void reportMovies(final DataSource dataSource, final Path path) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        // sql.append("SELECT movie.c00 AS film, files.playcount, files.lastplayed");
        // sql.append(" FROM movie");
        // sql.append(" INNER JOIN files ON files.idfile = movie.idfile AND files.playcount > 0");
        // sql.append(" ORDER BY film asc");
        sql.append("SELECT c00 AS movie, playcount, lastplayed");
        sql.append(" FROM movie_view");
        sql.append(" WHERE playcount > 0");
        sql.append(" ORDER BY movie asc");

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql.toString()))
        {
            writeResultSet(resultSet, path);
        }
    }

    /**
     * Erzeugt eine CSV-Datei bereits gesehener Serien/Episoden.<br>
     * Siehe auch episodeview.
     *
     * @param dataSource {@link DataSource}
     * @param path {@link Path}
     * @throws Exception Falls was schief geht.
     */
    protected void reportTVShows(final DataSource dataSource, final Path path) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        // sql.append("SELECT tvshow.c00 AS serie, episode.c12 AS season, episode.c00 AS folge, episode.c13 AS nummer, files.playcount, files.lastplayed");
        // sql.append(" FROM episode");
        // sql.append(" INNER JOIN tvshow ON tvshow.idshow = episode.idshow");
        // sql.append(" INNER JOIN files ON files.idfile = episode.idfile AND files.playcount > 0");
        // sql.append(" ORDER BY serie asc, season asc, CAST(nummer AS UNSIGNED) asc");
        sql.append("SELECT strTitle AS tvshow, c12 AS season, c13 AS episode, c00 AS title, playcount, lastplayed");
        sql.append(" FROM episode_view");
        sql.append(" WHERE playcount > 0");
        sql.append(" ORDER BY tvshow asc, CAST(season AS UNSIGNED) asc, CAST(episode AS UNSIGNED) asc");

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql.toString()))
        {
            writeResultSet(resultSet, path);
        }
    }

    /**
     * @see de.freese.mediathek.report.MediaReporter#updateDbFromReport(javax.sql.DataSource, java.nio.file.Path)
     */
    @Override
    public void updateDbFromReport(final DataSource dataSource, final Path path) throws Exception
    {
        updateMovies(dataSource, path.resolve("playcount-report-filme.csv"));
        updateTVShows(dataSource, path.resolve("playcount-report-serien.csv"));
    }

    /**
     * Auslesen der CSV-Datei bereits gesehener Filme und aktualisieren der Datenbank.<br>
     *
     * @param dataSource {@link DataSource}
     * @param path {@link Path}
     * @throws Exception Falls was schief geht.
     */
    protected void updateMovies(final DataSource dataSource, final Path path) throws Exception
    {
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append("select files.playcount, files.lastPlayed, files.idfile");
        sqlSelect.append(" from files");
        sqlSelect.append(" INNER JOIN movie ON movie.idfile = files.idfile");
        sqlSelect.append(" where movie.c00 = ?");

        StringBuilder sqlUpdate = new StringBuilder();
        // mysql
        // sqlUpdate.append("UPDATE files");
        // sqlUpdate.append(" INNER JOIN movie ON movie.idfile = files.idfile");
        // sqlUpdate.append(" set files.playcount = ?, files.lastplayed = ?");
        // sqlUpdate.append(" where movie.c00 = ?");

        // sqlite unterstützt keine joins in updates.
        sqlUpdate.append("UPDATE files");
        sqlUpdate.append(" set playcount = ?, lastplayed = ?");
        sqlUpdate.append(" where idfile = ?");

        List<Map<String, Object>> watchedMovies = readMovies(path);

        try (Connection connection = dataSource.getConnection())
        {
            connection.setAutoCommit(false);

            try (PreparedStatement stmtUpdate = connection.prepareStatement(sqlUpdate.toString());
                 PreparedStatement stmtSelect = connection.prepareStatement(sqlSelect.toString()))
            {
                for (Map<String, Object> map : watchedMovies)
                {
                    String movie = (String) map.get("MOVIE");
                    int playcount = Integer.parseInt((String) map.get("PLAYCOUNT"));
                    String lastplayed = (String) map.get("LASTPLAYED");

                    stmtSelect.setString(1, movie);

                    try (ResultSet resultSet = stmtSelect.executeQuery())
                    {
                        if (resultSet.next())
                        {
                            // Eintrag gefunden -> Update
                            if ((playcount != resultSet.getInt("PLAYCOUNT")) || !lastplayed.equals(resultSet.getString("LASTPLAYED")))
                            {
                                int idFile = resultSet.getInt("IDFILE");

                                System.out.printf("Update Movie: IDFile=%d, %s%n", idFile, movie);

                                stmtUpdate.setInt(1, playcount);
                                stmtUpdate.setString(2, lastplayed);
                                stmtUpdate.setInt(3, idFile);
                                // stmtUpdate.setString(3, movie);
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
                throw ex;
            }
        }
    }

    /**
     * Auslesen der CSV-Datei bereits gesehener Serien und aktualisieren der Datenbank.<br>
     *
     * @param dataSource {@link DataSource}
     * @param path {@link Path}
     * @throws Exception Falls was schief geht.
     */
    protected void updateTVShows(final DataSource dataSource, final Path path) throws Exception
    {
        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append("select files.playcount, files.lastPlayed, files.idfile");
        sqlSelect.append(" from files");
        sqlSelect.append(" INNER JOIN episode ON episode.idfile = files.idfile");
        sqlSelect.append(" INNER JOIN tvshow ON tvshow.idshow = episode.idshow");
        sqlSelect.append(" where tvshow.c00 = ? and episode.c12 = ? and episode.c13 = ?");

        StringBuilder sqlUpdate = new StringBuilder();
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

        List<Map<String, Object>> watchedShows = readTVShows(path);

        try (Connection connection = dataSource.getConnection())
        {
            connection.setAutoCommit(false);

            try (PreparedStatement stmtUpdate = connection.prepareStatement(sqlUpdate.toString());
                 PreparedStatement stmtSelect = connection.prepareStatement(sqlSelect.toString()))
            {
                for (Map<String, Object> map : watchedShows)
                {
                    String tvshow = (String) map.get("TVSHOW");
                    String season = (String) map.get("SEASON");
                    String episode = (String) map.get("EPISODE");
                    String title = (String) map.get("TITLE");
                    int playcount = Integer.parseInt((String) map.get("PLAYCOUNT"));
                    String lastplayed = (String) map.get("LASTPLAYED");

                    stmtSelect.setString(1, tvshow);
                    stmtSelect.setString(2, season);
                    stmtSelect.setString(3, episode);

                    try (ResultSet resultSet = stmtSelect.executeQuery())
                    {
                        if (resultSet.next())
                        {
                            // Eintrag gefunden -> Update
                            if ((playcount != resultSet.getInt("PLAYCOUNT")) || !lastplayed.equals(resultSet.getString("LASTPLAYED")))
                            {
                                int idFile = resultSet.getInt("IDFILE");

                                System.out.printf("Update TvShow: IDFile=%d, %s - S%02dE%02d - %s%n", idFile, tvshow, Integer.parseInt(season),
                                        Integer.parseInt(episode), title);

                                stmtUpdate.setInt(1, playcount);
                                stmtUpdate.setString(2, lastplayed);
                                stmtUpdate.setInt(3, playcount);
                                // stmtUpdate.setString(3, tvshow);
                                // stmtUpdate.setString(4, season);
                                // stmtUpdate.setString(5, episode);
                                // statement.setString(6, title);
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
                throw ex;
            }
        }
    }

    /**
     * @see de.freese.mediathek.report.MediaReporter#writeReport(javax.sql.DataSource, java.nio.file.Path)
     */
    @Override
    public void writeReport(final DataSource dataSource, final Path path) throws Exception
    {
        reportMovies(dataSource, path.resolve("playcount-report-filme.csv"));
        reportTVShows(dataSource, path.resolve("playcount-report-serien.csv"));
    }
}
