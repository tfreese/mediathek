// Created: 05.04.2020
package de.freese.mediathek.report;

import java.nio.file.Path;

import javax.sql.DataSource;

/**
 * Interface um Medien zu berichten und deren Datenbank zu aktualisieren.
 *
 * @author Thomas Freese
 */
public interface MediaReporter
{
    /**
     * Aktualisiert die Datenbank mit den Media-Infos aus der Datei.
     */
    void updateDbFromReport(final DataSource dataSource, final Path path) throws Exception;

    /**
     * Schreibt die Media-Infos aus der Datenbank in die Datei.
     */
    void writeReport(final DataSource dataSource, final Path path) throws Exception;
}
