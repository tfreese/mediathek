// Created: 18 Aug. 2024
package de.freese.player.swing;

import javax.swing.SwingUtilities;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.LoggerFactory;

import de.freese.player.library.LibraryRepository;

/**
 * @author Thomas Freese
 */
public final class PlayerLauncher {
    public static void main(final String[] args) {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.h2.Driver");
        hikariConfig.setJdbcUrl("jdbc:h2:file:/opt/jvmapps/musicplayer/h2");
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword(null);
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setMaximumPoolSize(3);
        hikariConfig.setConnectionTimeout(3 * 1000L); // Seconds

        final HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        try {
            LibraryRepository.createTableIfNotExist(dataSource);
        }
        catch (Exception ex) {
            LoggerFactory.getLogger(PlayerLauncher.class).error(ex.getMessage(), ex);

            dataSource.close();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                PlayerFrame.init();
                PlayerFrame.start();
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private PlayerLauncher() {
        super();
    }
}
