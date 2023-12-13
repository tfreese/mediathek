// Created: 05.04.2020
package de.freese.mediathek.report;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

/**
 * @author Thomas Freese
 */
public class BansheeAudioReporter extends AbstractMediaReporter {
    @Override
    public void updateDbFromReport(final DataSource dataSource, final Path path) throws Exception {
        throw new UnsupportedOperationException("updateDbFromReport not implemented");
    }

    @Override
    public void writeReport(final DataSource dataSource, final Path path) throws Exception {
        final StringBuilder sql = new StringBuilder();
        sql.append("select car.name as artist, ct.title as song, ct.playcount");
        sql.append(" from coretracks ct");
        sql.append(" inner join coreartists car on car.artistid = ct.artistid");
        sql.append(" where ct.playcount > 0");
        sql.append(" order by artist asc, song asc");

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql.toString())) {
            writeResultSet(resultSet, path);
        }

        //        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        //
        //        jdbcTemplate.query(sql.toString(), resultSet -> {
        //            writeResultSet(resultSet, path);
        //
        //            return null;
        //        });
    }
}
