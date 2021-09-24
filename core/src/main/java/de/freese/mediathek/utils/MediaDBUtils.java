// Created: 03.11.2015
package de.freese.mediathek.utils;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.freese.base.utils.FileUtils;
import de.freese.base.utils.JdbcUtils;

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
        FileUtils.rename(path);
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
        JdbcUtils.writeCSV(resultSet, ps);
    }

    /**
     * Erstellt ein neues {@link MediaDBUtils} Object.
     */
    private MediaDBUtils()
    {
        super();
    }
}
