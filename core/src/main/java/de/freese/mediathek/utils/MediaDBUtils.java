// Created: 03.11.2015
package de.freese.mediathek.utils;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
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
 * Utils für Mediatheken.
 *
 * @author Thomas Freese
 */
public final class MediaDBUtils
{
    /**
     * @param path {@link Path}
     *
     * @return List
     *
     * @throws IOException Falls was schiefgeht
     */
    public static List<String[]> parseCsv(final Path path) throws IOException
    {
        //        try (Stream<String> stream = Files.lines(path))
        //        {
        //            // @formatter:off
//            return stream
//                    .map(MediaDBUtils::splitCsvRow)
//                    .filter(Objects::nonNull)
//                    .filter(line -> !line.strip().isEmpty())
//                    .toList()
//            ;
//            // @formatter:on
        //        }

        // @formatter:off
        return Files.readAllLines(path).stream()
                .filter(Objects::nonNull)
                .filter(line -> !line.strip().isBlank())
                .map(MediaDBUtils::parseCsvRow)
                .toList()
                ;
        // @formatter:on
    }

    /**
     * Benennt die bestehende Datei in *.last um.
     *
     * @param path {@link Path}
     *
     * @throws IOException Falls was schiefgeht.
     */
    public static void rename(final Path path) throws IOException
    {
        Objects.requireNonNull(path, "path required");

        Path parent = path.getParent();
        String fileName = path.getFileName().toString();
        Path last = parent.resolve(fileName + ".last");

        if (!Files.exists(parent))
        {
            Files.createDirectories(parent);
        }

        if (Files.exists(last))
        {
            Files.delete(last);
        }

        if (Files.exists(path))
        {
            Files.move(path, last); // StandardCopyOption
        }
    }

    /**
     * Schreibt das ResultSet als CSV-Datei.<br>
     * Wenn das ResultSet vom Typ != ResultSet.TYPE_FORWARD_ONLY ist, wird {@link ResultSet#first()} aufgerufen und kann weiter verwendet werden.
     *
     * @param resultSet {@link ResultSet}
     * @param path {@link Path}
     *
     * @throws Exception Falls was schiefgeht.
     */
    public static void writeCsv(final ResultSet resultSet, final Path path) throws Exception
    {
        rename(path);

        try (PrintStream ps = new PrintStream(Files.newOutputStream(path), true, StandardCharsets.UTF_8))
        {
            writeCsv(resultSet, ps);
        }
    }

    /**
     * Schreibt das ResultSet als CSV-Datei.<br>
     * Der Stream wird nicht geschlossen.<br>
     * Wenn das ResultSet vom Typ != ResultSet.TYPE_FORWARD_ONLY ist, wird {@link ResultSet#first()} aufgerufen und kann weiter verwendet werden.
     *
     * @param resultSet {@link ResultSet}
     * @param ps {@link PrintWriter}
     *
     * @throws SQLException Falls was schiefgeht.
     */
    public static void writeCsv(final ResultSet resultSet, final PrintStream ps) throws SQLException
    {
        UnaryOperator<String> valueFunction = value ->
        {
            if (value == null || value.strip().isBlank())
            {
                return "";
            }

            String v = value;

            // Enthaltene Anführungszeichen escapen.
            if (v.contains("\""))
            {
                v = v.replace("\"", "\"\"");
            }

            // Den Wert selbst in Anführungszeichen setzen.
            return "\"" + v + "\"";
        };

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        StringJoiner stringJoiner = new StringJoiner(",");

        // Header
        for (int column = 1; column <= columnCount; column++)
        {
            stringJoiner.add(valueFunction.apply(metaData.getColumnLabel(column).toUpperCase()));
        }

        ps.println(stringJoiner);

        // Daten
        while (resultSet.next())
        {
            stringJoiner = new StringJoiner(",");

            for (int column = 1; column <= columnCount; column++)
            {
                Object obj = resultSet.getObject(column);
                String value;

                if (obj instanceof byte[] bytes)
                {
                    value = new String(bytes, StandardCharsets.UTF_8);
                }
                else
                {
                    value = Objects.toString(obj, "");
                }

                stringJoiner.add(valueFunction.apply(value));
            }

            ps.println(stringJoiner);
        }

        ps.flush();

        // ResultSet wieder zurück auf Anfang.
        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY)
        {
            resultSet.first();
        }
    }

    /**
     * @param csvRow String
     *
     * @return String[]
     */
    private static String[] parseCsvRow(String csvRow)
    {
        String row = csvRow;
        List<String> token = new ArrayList<>();

        while (!row.isBlank())
        {
            if (row.startsWith(","))
            {
                // Leerer Wert
                token.add("");
                row = row.substring(1);
                continue;
            }

            int endIndex = row.indexOf("\",");

            if (endIndex < 0)
            {
                // Letzter Wert -> Ende
                token.add(row);
                break;
            }

            token.add(row.substring(0, endIndex + 1));
            row = row.substring(endIndex + 2);
        }

        return token.stream()
                .map(t -> t.replaceAll("^\"|\"$", "")) // Erstes und letztes '"' entfernen
                .map(l -> l.replace("\"\"", "\"")) // Escapte Anführungszeichen ersetzen: "" -> "
                .map(String::strip)
                .toArray(String[]::new)
                ;
    }

    /**
     * Erstellt ein neues {@link MediaDBUtils} Object.
     */
    private MediaDBUtils()
    {
        super();
    }
}
