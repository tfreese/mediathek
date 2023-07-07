// Created: 05.04.2020
package de.freese.mediathek.report;

import java.nio.file.Path;

import javax.sql.DataSource;

/**
 * Interface for reporting Media and update their Databases.
 *
 * @author Thomas Freese
 */
public interface MediaReporter {
    void updateDbFromReport(DataSource dataSource, Path path) throws Exception;

    void writeReport(DataSource dataSource, Path path) throws Exception;
}
