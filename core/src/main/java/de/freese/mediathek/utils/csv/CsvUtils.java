// Created: 09 Nov. 2025
package de.freese.mediathek.utils.csv;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public interface CsvUtils {

    List<Map<String, String>> readCsv(Path path) throws Exception;

    /**
     * Stream is not closed.<br>
     * If the ResultSet is != ResultSet.TYPE_FORWARD_ONLY, {@link ResultSet#first()} is called and the {@link ResultSet} can still used.
     */
    default void writeCsv(final ResultSet resultSet, final Path file) throws Exception {
        try (PrintStream ps = new PrintStream(new BufferedOutputStream(Files.newOutputStream(file)), true, StandardCharsets.UTF_8)) {
            writeCsv(resultSet, ps);
        }
    }

    /**
     * Stream is not closed.<br>
     * If the ResultSet is != ResultSet.TYPE_FORWARD_ONLY, {@link ResultSet#first()} is called and the {@link ResultSet} can still used.
     */
    void writeCsv(ResultSet resultSet, PrintStream ps) throws Exception;
}
