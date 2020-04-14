/**
 * Created on 03.11.2015 18:45:49
 */
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
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;

/**
 * Utils für Mediatheken.
 *
 * @author Thomas Freese
 */
public abstract class MediaDBUtils
{
    /**
     * Die Spaltenbreite der Elemente wird auf den breitesten Wert durch das Padding aufgefüllt.<br>
     * Ist das Padding null oder leer wird nichts gemacht.<br>
     * Beim Padding werden die CharSequences durch Strings ersetzt.
     *
     * @param <T> Konkreter Typ
     * @param rows {@link List}
     * @param padding String
     * @see #write(List, PrintStream, String)
     */
    @SuppressWarnings("unchecked")
    public static <T extends CharSequence> void padding(final List<T[]> rows, final String padding)
    {
        if ((rows == null) || rows.isEmpty() || StringUtils.isEmpty(padding))
        {
            return;
        }

        int columnCount = rows.get(0).length;

        // Breite pro Spalte rausfinden.
        int[] columnWidth = new int[columnCount];

        // @formatter:off
        IntStream.range(0, columnCount).forEach(column
                ->
                {
                    columnWidth[column] = rows.stream()
                            .parallel()
                            .map(r -> r[column])
                            .mapToInt(CharSequence::length)
                            .max()
                            .orElse(0);
        });
        // @formatter:on

        // Strings pro Spalte formatieren und schreiben.
        rows.stream().parallel().forEach(r -> {
            for (int column = 0; column < columnCount; column++)
            {
                String value = StringUtils.rightPad(r[column].toString(), columnWidth[column], padding);

                r[column] = (T) value;
            }
        });
    }

    /**
     * Benennt die bestehende Datei in *.last um.
     *
     * @param path {@link Path}
     * @throws IOException Falls was schief geht.
     */
    public static void rename(final Path path) throws IOException
    {
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

        Files.move(path, last);
    }

    /**
     * Erzeugt aus dem {@link ResultSet} eine Liste mit den Column-Namen in der ersten Zeile und den Daten.<br>
     * Wenn das ResultSet einen Typ != ResultSet.TYPE_FORWARD_ONLY besitzt, wird {@link ResultSet#first()} aufgerufen und kann weiter verwendet werden.
     *
     * @param resultSet {@link ResultSet}
     * @return {@link List}
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    public static List<String[]> toList(final ResultSet resultSet) throws SQLException
    {
        Objects.requireNonNull(resultSet, "resultSet required");

        List<String[]> rows = new ArrayList<>();

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Spaltennamen / Header
        String[] header = new String[columnCount];
        rows.add(header);

        for (int column = 1; column <= columnCount; column++)
        {
            header[column - 1] = metaData.getColumnLabel(column).toUpperCase();
        }

        // Daten
        while (resultSet.next())
        {
            String[] row = new String[columnCount];
            rows.add(row);

            for (int column = 1; column <= columnCount; column++)
            {
                Object obj = resultSet.getObject(column);
                String value = null;

                if (obj == null)
                {
                    value = "";
                }
                else if (obj instanceof byte[])
                {
                    value = new String((byte[]) obj);
                }
                else
                {
                    value = obj.toString();
                }

                row[column - 1] = value;
            }
        }

        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY)
        {
            resultSet.first();
        }

        return rows;
    }

    /**
     * Schreibt die Liste in den PrintStream.<br>
     * Der Stream wird nicht geschlossen.
     *
     * @param <T> Konkreter Typ von CharSequence
     * @param rows {@link List}
     * @param ps {@link PrintStream}
     * @param delimiter String
     * @see #padding(List, String)
     */
    @SuppressWarnings("resource")
    public static <T extends CharSequence> void write(final List<T[]> rows, final PrintStream ps, final String delimiter)
    {
        Objects.requireNonNull(rows, "rows required");
        Objects.requireNonNull(ps, "printStream required");

        if (rows.isEmpty())
        {
            return;
        }

        int columnCount = rows.get(0).length;

        // Strings pro Spalte schreiben, .parallel() verfälscht die Reihenfolge.
        rows.forEach(r -> {
            for (int column = 0; column < columnCount; column++)
            {
                ps.print(r[column]);

                if ((column < (columnCount - 1)) && StringUtils.isNotBlank(delimiter))
                {
                    ps.print(delimiter);
                }
            }

            ps.println();
        });

        ps.flush();
    }

    /**
     * Schreibt das ResultSet als CSV-Datei.<br>
     * Wenn das ResultSet einen Typ != ResultSet.TYPE_FORWARD_ONLY besitzt, wird {@link ResultSet#first()} aufgerufen und kann weiter verwendet werden.
     *
     * @param resultSet {@link ResultSet}
     * @param path {@link Path}
     * @throws SQLException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    public static void writeCSV(final ResultSet resultSet, final Path path) throws SQLException, IOException
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
     * @throws SQLException Falls was schief geht.
     */
    public static void writeCSV(final ResultSet resultSet, final PrintStream ps) throws SQLException
    {
        List<String[]> rows = toList(resultSet);
        padding(rows, "");

        // Values escapen.
        int columnCount = rows.get(0).length;

        rows.stream().parallel().forEach(r -> {
            for (int column = 0; column < columnCount; column++)
            {
                r[column] = "\"" + r[column] + "\"";
            }
        });

        write(rows, ps, ";");

        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY)
        {
            resultSet.first();
        }
    }
}
