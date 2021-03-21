/**
 * Created: 16.09.2014
 */
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
public class AppConfigSQLite extends AbstractAppConfig
{
    /**
     * @return {@link SQLiteConfig}
     */
    private SQLiteConfig createSQLiteConfig()
    {
        // Native Libraries deaktivieren für den Zugriff auf die Dateien.
        System.setProperty("sqlite.purejava", "true");
        // System.setProperty("org.sqlite.lib.path", "/home/tommy");
        // System.setProperty("org.sqlite.lib.name", "sqlite-libsqlitejdbc.so");

        SQLiteConfig config = new SQLiteConfig();
        config.setReadOnly(true);
        config.setReadUncommited(true);

        return config;
    }

    /**
     * @see de.freese.mediathek.kodi.spring.AbstractAppConfig#dataSourceAudio()
     */
    @Override
    @Bean
    public DataSource dataSourceAudio()
    {
        // @Value("${sqlite.audio.db.url}") final String url
        SQLiteConfig config = createSQLiteConfig();

        SQLiteDataSource dataSource = new SQLiteDataSource(config);
        dataSource.setUrl(getEnvironment().getProperty("sqlite.audio.db.url"));

        return dataSource;
    }

    /**
     * @see de.freese.mediathek.kodi.spring.AbstractAppConfig#dataSourceVideo()
     */
    @Override
    @Bean
    @Primary
    public DataSource dataSourceVideo()
    {
        SQLiteConfig config = createSQLiteConfig();

        SQLiteDataSource dataSource = new SQLiteDataSource(config);
        dataSource.setUrl(getEnvironment().getProperty("sqlite.video.db.url"));

        return dataSource;
    }
}
