// Created: 03.11.2015
package de.freese.mediathek.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.freese.mediathek.utils.csv.CsvUtils;
import de.freese.mediathek.utils.csv.DefaultCsvUtils;

/**
 * @author Thomas Freese
 */
public final class MediaDbUtils {
    private static final CsvUtils CSV_UTILS = new DefaultCsvUtils();

    public static List<Map<String, String>> readCsv(final Path path) throws Exception {
        return CSV_UTILS.readCsv(path);
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

    public static void writeCsv(final ResultSet resultSet, final Path path) throws Exception {
        CSV_UTILS.writeCsv(resultSet, path);
    }

    private MediaDbUtils() {
        super();
    }
}
