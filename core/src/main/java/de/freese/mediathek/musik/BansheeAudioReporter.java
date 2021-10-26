// Created: 05.04.2020
package de.freese.mediathek.musik;

import java.nio.file.Path;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import de.freese.mediathek.report.AbstractMediaReporter;

/**
 * @author Thomas Freese
 */
public class BansheeAudioReporter extends AbstractMediaReporter
{
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
            writeResultSet(resultSet, path.resolve("musik-report-banshee.csv"));

            return null;
        });
    }
}
