// Created: 08 Sept. 2024
package de.freese.player;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.library.LibraryRepository;
import de.freese.player.player.DefaultDspPlayer;
import de.freese.player.player.DspPlayer;
import de.freese.player.player.PlayList;
import de.freese.player.swing.component.table.TablePlayList;

/**
 * @author Thomas Freese
 */
public final class ApplicationContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    private static DataSource dataSource;
    private static ExecutorService executorService;
    private static LibraryRepository libraryRepository;
    private static PlayList playList;
    private static DspPlayer player;
    private static Path tempDir;

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static LibraryRepository getLibraryRepository() {
        return libraryRepository;
    }

    public static PlayList getPlayList() {
        return playList;
    }

    public static DspPlayer getPlayer() {
        return player;
    }

    public static void start() {
        tempDir = Path.of(System.getProperty("java.io.tmpdir"), "musicPlayer");

        if (!Files.exists(tempDir)) {
            try {
                Files.createDirectories(tempDir);
            }
            catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        executorService = Executors.newFixedThreadPool(8, Thread.ofPlatform().daemon().name("player-", 1).factory());

        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.h2.Driver");
        hikariConfig.setJdbcUrl("jdbc:h2:file:/opt/jvmapps/musicplayer/h2");
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword(null);
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setMaximumPoolSize(3);
        hikariConfig.setConnectionTimeout(3 * 1000L); // Seconds

        dataSource = new HikariDataSource(hikariConfig);

        try {
            LibraryRepository.createTableIfNotExist(dataSource);

            libraryRepository = new LibraryRepository(dataSource);
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

        playList = new TablePlayList();
        player = new DefaultDspPlayer(executorService, tempDir);
    }

    public static void stop() {
        if (player.isPlaying()) {
            player.stop();
        }

        executorService.close();

        if (dataSource instanceof Closeable closeable) {
            try {
                closeable.close();
            }
            catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    private ApplicationContext() {
        super();
    }
}