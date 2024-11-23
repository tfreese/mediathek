// Created: 16.09.2014
package de.freese.mediathek.kodi.spring;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

/**
 * @author Thomas Freese
 */
@Configuration
@Profile("sqlite")
public class AppConfigSqLite extends AbstractAppConfig {
    private static SQLiteConfig createSQLiteConfig() {
        // Native Libraries deaktivieren für den Zugriff auf die Dateien.
        System.setProperty("sqlite.purejava", "true");
        // System.setProperty("org.sqlite.lib.path", "/home/tommy");
        // System.setProperty("org.sqlite.lib.name", "sqlite-libsqlitejdbc.so");

        final SQLiteConfig config = new SQLiteConfig();
        config.setReadOnly(true);
        config.setReadUncommitted(true);

        return config;
    }

    @Override
    @Bean
    public DataSource dataSourceAudio() {
        // @Value("${sqlite.audio.db.url}") final String url
        final SQLiteConfig config = createSQLiteConfig();

        final SQLiteDataSource dataSource = new SQLiteDataSource(config);
        dataSource.setUrl(getEnvironment().getProperty("sqlite.audio.db.url"));

        return dataSource;
    }

    @Override
    @Bean
    @Primary
    public DataSource dataSourceVideo() {
        final SQLiteConfig config = createSQLiteConfig();

        final SQLiteDataSource dataSource = new SQLiteDataSource(config);
        dataSource.setUrl(getEnvironment().getProperty("sqlite.video.db.url"));

        return dataSource;
    }
}
