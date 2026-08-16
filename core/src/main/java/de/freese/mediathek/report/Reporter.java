package de.freese.mediathek.report;

import java.io.Closeable;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import de.freese.mediathek.utils.StopWatch;

/**
 * @author Thomas Freese
 * @since 05.04.2020
 */
public final class Reporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Reporter.class);
    private static final StopWatch STOP_WATCH = new StopWatch();

    /**
     * @author Thomas Freese
     */
    static final class DataSources {
        static DataSource bansheeSqLite(final boolean readOnly) {
            return createSqLite(readOnly, "jdbc:sqlite:/home/tommy/.config/banshee-1/banshee.db");
        }

        static DataSource kodiMusicSqLite(final boolean readOnly) {
            return createSqLite(readOnly, "jdbc:sqlite:/home/tommy/.kodi/userdata/Database/MyMusic82.db");
        }

        static DataSource plexSqlite(final boolean readOnly) {
            return createSqLite(readOnly, "jdbc:sqlite:/home/tommy/com.plexapp.plugins.library.db");
        }

        static DataSource strawberrySqLite(final boolean readOnly) {
            return createSqLite(readOnly, "jdbc:sqlite:/home/tommy/.local/share/strawberry/strawberry/strawberry.db");
        }

        private static DataSource createSqLite(final boolean readOnly, final String url) {
            // Native Libraries deaktivieren für den Zugriff auf die Dateien.
            System.setProperty("sqlite.purejava", "true");

            // Pfade für native Libraries.
            // System.setProperty("org.sqlite.lib.path", "/home/tommy");
            // System.setProperty("org.sqlite.lib.name", "sqlite-libsqlitejdbc.so");

            // DriverManager.setLogWriter(new PrintWriter(System.out, true));

            final SQLiteConfig config = new SQLiteConfig();
            config.setReadOnly(readOnly);
            config.setReadUncommitted(true);

            // final SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
            // dataSource.setDriverClassName("org.sqlite.JDBC");
            // dataSource.setUrl(url);
            // dataSource.setSuppressClose(true);
            // dataSource.setConnectionProperties(config.toProperties())

            final SQLiteDataSource dataSource = new SQLiteConnectionPoolDataSource(config);
            dataSource.setUrl(url);

            // Export View-Status: echo ".dump metadata_item_settings" | sqlite3 com.plexapp.plugins.library.db | grep -v TABLE | grep -v INDEX > settings.sql
            // Import View-Status: cat settings.sql | sqlite3 com.plexapp.plugins.library.db

            return dataSource;
        }

        private DataSources() {
            super();
        }
    }

    static void main() throws Exception {
        STOP_WATCH.start("connect");
        DataSource dataSource = DataSources.strawberrySqLite(true);
        STOP_WATCH.stop();

        MediaReporter mediaReporter = new StrawberryAudioReporter(dataSource);
        // final MediaReporter mediaReporter = new BansheeAudioReporter(dataSource);
        // final MediaReporter mediaReporter = new KodiAudioReporter(dataSource);
        // final MediaReporter mediaReporter = new PlexAudioReporter(dataSource);

        try {
            STOP_WATCH.start("writeReport");

            final Path path = Paths.get("/home/tommy/dokumente/linux");
            // final Path path = Paths.get("/tmp");

            LOGGER.info("Path: {}", path);

            mediaReporter.writeReport(path.resolve("musik-report-strawberry.csv"));
            // mediaReporter.updateDbFromReport(path.resolve("musik-report-strawberry.csv"));

            STOP_WATCH.stop();
        }
        catch (final Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            System.exit(-1);
        }
        finally {
            STOP_WATCH.start("disconnect");

            if (dataSource instanceof final Closeable c) {
                c.close();
            }
            else if (dataSource instanceof final AutoCloseable ac) {
                ac.close();
            }

            STOP_WATCH.stop();
        }

        STOP_WATCH.prettyPrint(System.out);
        System.exit(0);
    }

    private Reporter() {
        super();
    }
}
