// Created: 05.04.2020
package de.freese.mediathek.musik;

import java.nio.file.Path;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import de.freese.mediathek.report.AbstractMediaReporter;

/**
 * @author Thomas Freese
 */
public class BansheeMusikReporter extends AbstractMediaReporter
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

        // SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        // dataSource.setDriverClassName("org.sqlite.JDBC");
        // dataSource.setUrl("jdbc:sqlite:/home/tommy/.config/banshee-1/banshee.db");
        // dataSource.setSuppressClose(true);
        // dataSource.setConnectionProperties(config.toProperties());

        SQLiteDataSource dataSource = new SQLiteConnectionPoolDataSource(config);
        dataSource.setUrl("jdbc:sqlite:/home/tommy/.config/banshee-1/banshee.db");

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
        sql.append("select car.name as artist, ct.title as song, ct.playcount");
        sql.append(" from coretracks ct");
        sql.append(" inner join coreartists car on car.artistid = ct.artistid");
        sql.append(" where ct.playcount > 0");
        sql.append(" order by artist asc, song asc");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.query(sql.toString(), resultSet -> {
            writeResultSet(resultSet, path);

            return null;
        });
    }
}
