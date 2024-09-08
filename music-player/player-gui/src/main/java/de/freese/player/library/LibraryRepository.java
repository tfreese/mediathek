// Created: 08 Sept. 2024
package de.freese.player.library;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.input.AudioSource;

/**
 * @author Thomas Freese
 */
public final class LibraryRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryRepository.class);

    public static void createTableIfNotExist(final DataSource dataSource) throws Exception {
        final String tableName = "LIBRARY";

        try (Connection connection = dataSource.getConnection()) {
            final DatabaseMetaData metaData = connection.getMetaData();
            boolean tableExist = false;

            try (ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
                if (tables.next()) {
                    tableExist = true;
                }
            }

            if (!tableExist) {
                LOGGER.info("Create table: {}", tableName);

                final URL url = Thread.currentThread().getContextClassLoader().getResource("library.sql");
                assert url != null;

                try (Stream<String> stream = Files.lines(Path.of(url.toURI()), StandardCharsets.UTF_8);
                     Statement statement = connection.createStatement()) {
                    final String createSql = stream.collect(Collectors.joining());
                    statement.execute(createSql);
                }
            }
        }
    }

    private final DataSource dataSource;

    public LibraryRepository(final DataSource dataSource) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    public void save(final AudioSource audioSource) {
        // TODO
    }
}
