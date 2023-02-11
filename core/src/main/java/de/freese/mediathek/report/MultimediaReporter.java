// Created: 05.04.2020
package de.freese.mediathek.report;

import java.io.Closeable;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import de.freese.mediathek.utils.StopWatch;

/**
 * @author Thomas Freese
 */
public final class MultimediaReporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultimediaReporter.class);

    private static final StopWatch STOP_WATCH = new StopWatch();

    /**
     * @author Thomas Freese
     */
    static final class DataSources {
        static DataSource bansheeSqLite(final boolean readonly) throws Exception {
            return createSqLite(readonly, "jdbc:sqlite:/home/tommy/.config/banshee-1/banshee.db");
        }

        static DataSource clementineSqLite(final boolean readonly) throws Exception {
            return createSqLite(readonly, "jdbc:sqlite:/home/tommy/.config/Clementine/clementine.db");
        }

        static DataSource kodiMusikSqLite(final boolean readonly) throws Exception {
            return createSqLite(readonly, "jdbc:sqlite:/home/tommy/.kodi/userdata/Database/MyMusic82.db");
        }

        static DataSource plexSqlite(final boolean readonly) throws Exception {
            // jdbc:sqlite:/var/lib/plex/Plex\\ Media\\ Server/Plug-in\\ Support/Databases/com.plexapp.plugins.library.db
            // jdbc:sqlite:/opt/plexmediaserver/Resources/com.plexapp.plugins.library.db
            // jdbc:sqlite:/home/tommy/.config/plex/com.plexapp.plugins.library.db
            return createSqLite(readonly, "jdbc:sqlite:/home/tommy/com.plexapp.plugins.library.db");
        }

        private static DataSource createSqLite(final boolean readonly, final String url) throws Exception {
            // Native Libraries deaktivieren für den Zugriff auf die Dateien.
            System.setProperty("sqlite.purejava", "true");

            // Pfade für native Libraries.
            // System.setProperty("org.sqlite.lib.path", "/home/tommy");
            // System.setProperty("org.sqlite.lib.name", "sqlite-libsqlitejdbc.so");

            // DriverManager.setLogWriter(new PrintWriter(System.out, true));

            SQLiteConfig config = new SQLiteConfig();
            config.setReadOnly(readonly);
            config.setReadUncommited(true);

            // SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
            // dataSource.setDriverClassName("org.sqlite.JDBC");
            // dataSource.setUrl("jdbc:sqlite:/home/tommy/com.plexapp.plugins.library.db");
            // dataSource.setSuppressClose(true);
            // dataSource.setConnectionProperties(config.toProperties())

            SQLiteDataSource dataSource = new SQLiteConnectionPoolDataSource(config);
            // dataSource.setUrl("jdbc:sqlite:/var/lib/plex/Plex\\ Media\\ Server/Plug-in\\ Support/Databases/com.plexapp.plugins.library.db");
            // dataSource.setUrl("jdbc:sqlite:/opt/plexmediaserver/Resources/com.plexapp.plugins.library.db");
            // dataSource.setUrl("jdbc:sqlite:/home/tommy/.config/plex/com.plexapp.plugins.library.db");
            dataSource.setUrl(url);

            // Export View-Status: echo ".dump metadata_item_settings" | sqlite3 com.plexapp.plugins.library.db | grep -v TABLE | grep -v INDEX > settings.sql
            // Import View-Status: cat settings.sql | sqlite3 com.plexapp.plugins.library.db

            return dataSource;
        }

        private DataSources() {
            super();
        }
    }

    public static void main(final String[] args) throws Exception {
        // MediaReporter mediaReporter = new BansheeAudioReporter();
        MediaReporter mediaReporter = new ClementineAudioReporter();
        //MediaReporter mediaReporter = new KodiAudioReporter();
        // MediaReporter mediaReporter = new PlexAudioReporter();

        STOP_WATCH.start("connect");
        DataSource dataSource = DataSources.clementineSqLite(true);
        //DataSource dataSource = DataSources.kodiMusikSqLite(true);
        STOP_WATCH.stop();

        try {
            STOP_WATCH.start("writeReport");

            Path path = Paths.get("/home/tommy/dokumente/linux");
            // Path path = Paths.get("/tmp");

            LOGGER.info("Path: {}", path);

            mediaReporter.writeReport(dataSource, path);
            //mediaReporter.updateDbFromReport(dataSource, path);

            STOP_WATCH.stop();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            System.exit(-1);
        }
        finally {
            STOP_WATCH.start("disconnect");

            if (dataSource instanceof SingleConnectionDataSource ds) {
                ds.destroy();
            }
            else if (dataSource instanceof Closeable c) {
                c.close();
            }
            else if (dataSource instanceof AutoCloseable ac) {
                ac.close();
            }

            STOP_WATCH.stop();
        }

        STOP_WATCH.prettyPrint(System.out);
        System.exit(0);
    }

    private MultimediaReporter() {
        super();
    }
}
