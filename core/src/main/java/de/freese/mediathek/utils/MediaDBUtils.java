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
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * Utils für Mediatheken.
 *
 * @author Thomas Freese
 */
public final class MediaDBUtils
{
    /**
     * Benennt die bestehende Datei in *.last um.
     *
     * @param path {@link Path}
     *
     * @throws IOException Falls was schief geht.
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
     * Wenn das ResultSet einen Typ != ResultSet.TYPE_FORWARD_ONLY besitzt, wird {@link ResultSet#first()} aufgerufen und kann weiter verwendet werden.
     *
     * @param resultSet {@link ResultSet}
     * @param path {@link Path}
     *
     * @throws Exception Falls was schief geht.
     */
    public static void writeCSV(final ResultSet resultSet, final Path path) throws Exception
    {
        rename(path);

        try (PrintStream ps = new PrintStream(Files.newOutputStream(path), true, StandardCharsets.UTF_8))
        {
            writeCSV(resultSet, ps);
        }
    }

    /**
     * Schreibt das ResultSet als CSV-Datei.<br>
     * Der Stream wird nicht geschlossen.<br>
     * Wenn das ResultSet einen Typ != ResultSet.TYPE_FORWARD_ONLY besitzt, wird {@link ResultSet#first()} aufgerufen und kann weiter verwendet werden.
     *
     * @param resultSet {@link ResultSet}
     * @param ps {@link PrintWriter}
     *
     * @throws SQLException Falls was schief geht.
     */
    public static void writeCSV(final ResultSet resultSet, final PrintStream ps) throws SQLException
    {
        // Enthaltene Anführungszeichen escapen und den Wert selbst in Anführungszeichen setzen.
        Function<String, String> valueFunction = value ->
        {
            String v = value;

            if (v.contains("\""))
            {
                v = v.replace("\"", "\"\"");
            }

            return "\"" + v + "\"";
        };

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        StringJoiner stringJoiner = new StringJoiner(";");

        // Header
        for (int column = 1; column <= columnCount; column++)
        {
            stringJoiner.add(valueFunction.apply(metaData.getColumnLabel(column).toUpperCase()));
        }

        ps.println(stringJoiner);

        // Daten
        while (resultSet.next())
        {
            stringJoiner = new StringJoiner(";");

            for (int column = 1; column <= columnCount; column++)
            {
                Object obj = resultSet.getObject(column);
                String value;

                if (obj == null)
                {
                    value = "";
                }
                else if (obj instanceof byte[] bytes)
                {
                    value = new String(bytes, StandardCharsets.UTF_8);
                }
                else
                {
                    value = obj.toString();
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
     * Erstellt ein neues {@link MediaDBUtils} Object.
     */
    private MediaDBUtils()
    {
        super();
    }
}
