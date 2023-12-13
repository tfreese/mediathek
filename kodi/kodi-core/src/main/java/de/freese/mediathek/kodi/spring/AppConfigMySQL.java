// Created: 16.09.2014
package de.freese.mediathek.kodi.spring;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * @author Thomas Freese
 */
@Configuration
@Profile("mysql")
public class AppConfigMySQL extends AbstractAppConfig {
    @Override
    @Bean(destroyMethod = "close")
    public DataSource dataSourceAudio() {
        // @Value("${mysql.audio.db.url}") final String url
        final HikariConfig config = createHikariConfig();
        config.setJdbcUrl(getEnvironment().getProperty("mysql.audio.db.url"));
        config.setPoolName("dataSourceMusik");

        return new HikariDataSource(config);
    }

    @Override
    @Bean(destroyMethod = "close")
    @Primary
    public DataSource dataSourceVideo() {
        final HikariConfig config = createHikariConfig();
        config.setJdbcUrl(getEnvironment().getProperty("mysql.video.db.url"));
        config.setPoolName("dataSourceVideo");

        return new HikariDataSource(config);
    }

    private HikariConfig createHikariConfig() {
        final HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.mariadb.jdbc.Driver");

        config.setUsername(getEnvironment().getProperty("mysql.db.user"));
        config.setPassword(getEnvironment().getProperty("mysql.db.password"));

        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);

        config.setAutoCommit(false);
        config.setReadOnly(false);

        return config;
    }
}
