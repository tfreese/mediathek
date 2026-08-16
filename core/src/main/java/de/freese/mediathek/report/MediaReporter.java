package de.freese.mediathek.report;

import java.nio.file.Path;

/**
 * Interface for reporting Media and update their Databases.
 *
 * @author Thomas Freese
 * @since 05.04.2020
 */
public interface MediaReporter {
    void updateDbFromReport(Path path) throws Exception;

    void writeReport(Path path) throws Exception;
}
