/**
 * Created: 05.04.2020
 */

package de.freese.mediathek.musik;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.sql.DriverManager;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;
import de.freese.mediathek.report.AbstractMediaReporter;
import de.freese.mediathek.utils.MediaDBUtils;

/**
 * @author Thomas Freese
 */
public class KodiMediaReporter extends AbstractMediaReporter
{
    /**
     * Erstellt ein neues {@link KodiMediaReporter} Object.
     */
    public KodiMediaReporter()
    {
        super();
    }

    /**
     * @see de.freese.mediathek.report.MediaReporter#createDataSource(boolean)
     */
    @Override
    public DataSource createDataSource(final boolean readonly) throws Exception
    {
        // Native Libraries deaktivieren für den Zugriff auf die Dateien.
        System.setProperty("sqlite.purejava", "true");
        // System.setProperty("org.sqlite.lib.path", "/home/tommy");
        // System.setProperty("org.sqlite.lib.name", "sqlite-libsqlitejdbc.so");

        DriverManager.setLogWriter(new PrintWriter(System.out, true));

        SQLiteConfig config = new SQLiteConfig();
        config.setReadOnly(readonly);
        config.setReadUncommited(true);

        SQLiteDataSource dataSource = new SQLiteConnectionPoolDataSource(config);
        dataSource.setUrl("jdbc:sqlite:/home/tommy/.kodi/userdata/Database/MyMusic72.db");

        return dataSource;
    }

    /**
     * @see de.freese.mediathek.report.MediaReporter#updateDbFromReport(javax.sql.DataSource, java.nio.file.Path)
     */
    @Override
    public void updateDbFromReport(final DataSource dataSource, final Path path) throws Exception
    {
        throw new UnsupportedOperationException("updateDbFromReport not implemented");
    }

    /**
     * @see de.freese.mediathek.report.MediaReporter#writeReport(javax.sql.DataSource, java.nio.file.Path)
     */
    @Override
    public void writeReport(final DataSource dataSource, final Path path) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT strArtists AS artist, strTitle AS song, iTimesPlayed AS playcount");
        sql.append(" FROM songView");
        sql.append(" WHERE iTimesPlayed > 0");
        sql.append(" ORDER BY artist asc, song asc");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.query(sql.toString(), resultSet -> {
            // writeCSV(resultSet, new File(fileName));
            MediaDBUtils.writeCSV(resultSet, System.out);

            return null;
        });
    }
}
