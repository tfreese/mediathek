// Created: 08 Sept. 2024
package de.freese.player.ui;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.player.DefaultPlayer;
import de.freese.player.core.player.Player;
import de.freese.player.core.player.SongCollection;
import de.freese.player.ui.equalizer.EqualizerDspProcessor;
import de.freese.player.ui.swing.component.table.TableModelSongCollection;

/**
 * @author Thomas Freese
 */
public final class ApplicationContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    private static DataSource dataSource;
    private static EqualizerDspProcessor equalizerDspProcessor;
    private static ExecutorService executorService;
    private static Player player;
    private static PlayerRepository repository;
    private static SongCollection songCollection;
    private static Path tempDir;
    private static Path workingDir;

    public static EqualizerDspProcessor getEqualizerDspProcessor() {
        return equalizerDspProcessor;
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static Player getPlayer() {
        return player;
    }

    public static PlayerRepository getRepository() {
        return repository;
    }

    public static SongCollection getSongCollection() {
        return songCollection;
    }

    public static Path getWorkingDir() {
        return workingDir;
    }

    public static void setEqualizerDspProcessor(final EqualizerDspProcessor equalizerDspProcessor) {
        ApplicationContext.equalizerDspProcessor = equalizerDspProcessor;
    }

    public static void start() {
        tempDir = Path.of(System.getProperty("java.io.tmpdir"), ".music-player");
        workingDir = Path.of(System.getProperty("user.home"), ".music-player");

        if (!Files.exists(tempDir)) {
            try {
                Files.createDirectories(tempDir);
            }
            catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        if (!Files.exists(workingDir)) {
            try {
                Files.createDirectories(workingDir);
            }
            catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        executorService = Executors.newFixedThreadPool(8, Thread.ofPlatform().daemon().name("player-", 1).factory());

        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.h2.Driver");
        // hikariConfig.setJdbcUrl("jdbc:h2:file:" + workingDir.resolve("h2") + ";DB_CLOSE_ON_EXIT=true");
        // hikariConfig.setJdbcUrl("jdbc:h2:file:" + workingDir.resolve("h2"));

        // Mixed Mode
        hikariConfig.setJdbcUrl("jdbc:h2:file:" + workingDir.resolve("h2") + ";DB_CLOSE_ON_EXIT=true;AUTO_SERVER=true");
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword(null);
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setMaximumPoolSize(3);
        hikariConfig.setConnectionTimeout(3 * 1000L); // Seconds

        dataSource = new HikariDataSource(hikariConfig);

        try {
            // Check
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("select 1")) {
                resultSet.next();
            }

            PlayerRepository.createDatabaseIfNotExist(dataSource);

            repository = new PlayerRepository(dataSource);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);

            if (dataSource instanceof Closeable closeable) {
                try {
                    closeable.close();
                }
                catch (IOException ex1) {
                    LOGGER.error(ex1.getMessage(), ex1);
                }
            }
        }

        songCollection = new TableModelSongCollection();
        player = new DefaultPlayer(executorService, tempDir);
    }

    public static void stop() {
        if (player.isPlaying()) {
            player.stop();
        }

        executorService.close();

        // Done by ;DB_CLOSE_ON_EXIT=true
        // try (Connection connection = dataSource.getConnection();
        //      Statement statement = connection.createStatement()) {
        //     statement.execute("SHUTDOWN COMPACT");
        // }
        // catch (Exception ex) {
        //     LOGGER.error(ex.getMessage());
        // }

        if (dataSource instanceof Closeable closeable) {
            try {
                closeable.close();
            }
            catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }

        try (Stream<Path> stream = Files.find(tempDir, 1, (path, attr) -> attr.isRegularFile())) {
            final List<Path> list = stream.toList();

            for (Path path : list) {
                Files.delete(path);
            }
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private ApplicationContext() {
        super();
    }
}
