// Created: 09 Nov. 2025
package de.freese.mediathek.utils.csv;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author Thomas Freese
 */
public final class DefaultCsvUtils implements CsvUtils {
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
            row = row.substring(endIndex + 2).strip();
        }

        return token.stream().map(t -> t.replaceAll("^\"|\"$", "")) // Remove first and last quote.
                .map(l -> l.replace("\\\"\"", "\"")) // Unescape quotes.
                .map(l -> l.replace("\\,", ",")) // Unescape comma.
                .map(String::strip).toArray(String[]::new);
    }

    private static String toCsvValue(final String value) {
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
    }

    @Override
    public List<Map<String, String>> readCsv(final Path path) throws Exception {
        final List<String> lines = new ArrayList<>(Files.readAllLines(path));

        final String[] header = parseCsvRow(lines.removeFirst());

        return lines.stream()
                .filter(Objects::nonNull)
                .filter(line -> !line.strip().isBlank())
                .map(l -> {
                    final String[] row = parseCsvRow(l);

                    final Map<String, String> map = new LinkedHashMap<>();

                    for (int c = 0; c < header.length; c++) {
                        map.put(header[c], row[c]);
                    }

                    return map;
                })
                .toList()
                ;
    }

    @Override
    public void writeCsv(final ResultSet resultSet, final PrintStream ps) throws Exception {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        final int columnCount = metaData.getColumnCount();

        StringJoiner stringJoiner = new StringJoiner(",");

        // Header
        for (int column = 1; column <= columnCount; column++) {
            stringJoiner.add(toCsvValue(metaData.getColumnLabel(column).toUpperCase()));
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

                stringJoiner.add(toCsvValue(value));
            }

            ps.println(stringJoiner);
        }

        ps.flush();

        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY) {
            resultSet.first();
        }
    }
}
