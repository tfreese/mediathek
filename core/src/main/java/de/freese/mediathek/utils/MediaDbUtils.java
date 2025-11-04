// Created: 03.11.2015
package de.freese.mediathek.utils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.UnaryOperator;

/**
 * @author Thomas Freese
 */
public final class MediaDbUtils {
    public static List<String[]> readCsv(final Path path) throws IOException {
        return Files.readAllLines(path).stream()
                .filter(Objects::nonNull)
                .filter(line -> !line.strip().isBlank())
                .map(MediaDbUtils::parseCsvRow)
                .toList()
                ;
    }

    public static void rename(final Path path) throws IOException {
        Objects.requireNonNull(path, "path required");

        final Path parent = path.getParent();
        final String fileName = path.getFileName().toString();
        final Path last = parent.resolve(fileName + ".last");

        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        if (Files.exists(last)) {
            Files.delete(last);
        }

        if (Files.exists(path)) {
            Files.move(path, last); // StandardCopyOption
        }
    }

    public static String subStringBetween(final String open, final String close, final String str) {
        final int start = str.indexOf(open);

        if (start != -1) {
            final int end = str.indexOf(close, start + open.length());

            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }

        return null;
    }

    /**
     * Stream is not closed.<br>
     * If the ResultSet is != ResultSet.TYPE_FORWARD_ONLY, {@link ResultSet#first()} is called and the {@link ResultSet} can still used.
     */
    public static void writeCsv(final ResultSet resultSet, final PrintStream ps) throws SQLException {
        final UnaryOperator<String> valueFunction = value -> {
            if (value == null || value.strip().isBlank()) {
                return "";
            }

            String v = value;

            // Escape quotes.
            if (v.contains("\"")) {
                v = v.replace("\"", "\\\"\"");
            }

            // Escape comma.
            if (v.contains(",")) {
                v = v.replace(",", "\\,");
            }

            // Value in quotes.
            return "\"" + v + "\"";
        };

        final ResultSetMetaData metaData = resultSet.getMetaData();
        final int columnCount = metaData.getColumnCount();

        StringJoiner stringJoiner = new StringJoiner(",");

        // Header
        for (int column = 1; column <= columnCount; column++) {
            stringJoiner.add(valueFunction.apply(metaData.getColumnLabel(column).toUpperCase()));
        }

        ps.println(stringJoiner);

        // Daten
        while (resultSet.next()) {
            stringJoiner = new StringJoiner(",");

            for (int column = 1; column <= columnCount; column++) {
                final Object obj = resultSet.getObject(column);
                final String value;

                if (obj instanceof byte[] bytes) {
                    value = new String(bytes, StandardCharsets.UTF_8);
                }
                else {
                    value = Objects.toString(obj, "");
                }

                stringJoiner.add(valueFunction.apply(value));
            }

            ps.println(stringJoiner);
        }

        ps.flush();

        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY) {
            resultSet.first();
        }
    }

    public static void writeCsv(final ResultSet resultSet, final Path path) throws Exception {
        rename(path);

        try (PrintStream ps = new PrintStream(new BufferedOutputStream(Files.newOutputStream(path)), true, StandardCharsets.UTF_8)) {
            writeCsv(resultSet, ps);
        }
    }

    private static String[] parseCsvRow(final String csvRow) {
        String row = csvRow;
        final List<String> token = new ArrayList<>();

        while (!row.isBlank()) {
            if (row.startsWith(",")) {
                // Empty Value
                token.add("");
                row = row.substring(1);
                continue;
            }

            final int endIndex = row.indexOf("\",");

            if (endIndex < 0) {
                // Last Value -> End
                token.add(row);
                break;
            }

            token.add(row.substring(0, endIndex + 1));
            row = row.substring(endIndex + 2);
        }

        return token.stream().map(t -> t.replaceAll("^\"|\"$", "")) // Remove first and last quote.
                .map(l -> l.replace("\\\"\"", "\"")) // Unescape quotes.
                .map(l -> l.replace("\\,", ",")) // Unescape comma.
                .map(String::strip).toArray(String[]::new);
    }

    private MediaDbUtils() {
        super();
    }
}
