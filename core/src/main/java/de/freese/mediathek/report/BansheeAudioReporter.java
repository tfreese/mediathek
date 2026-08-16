package de.freese.mediathek.report;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

/**
 * @author Thomas Freese
 * @since 05.04.2020
 */
public class BansheeAudioReporter extends AbstractMediaReporter {
    public BansheeAudioReporter(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void updateDbFromReport(final Path path) throws Exception {
        throw new UnsupportedOperationException("updateDbFromReport not implemented");
    }

    @Override
    public void writeReport(final Path path) throws Exception {
        final String sql = """
                select
                    car.name as artist,
                    ct.title as song,
                    ct.playcount
                from
                    coretracks ct
                inner join coreartists car on car.artistid = ct.artistid
                where
                    ct.playcount > 0
                order by artist asc, song asc
                """;

        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            writeResultSet(resultSet, path);
        }
    }
}
