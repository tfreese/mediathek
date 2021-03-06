/**
 * Created: 16.09.2014
 */
package de.freese.mediathek.kodi.spring;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author Thomas Freese
 */
@Configuration
@Profile("mysql")
public class AppConfigMySQL extends AbstractAppConfig
{
    /**
     * @return {@link HikariConfig}
     */
    private HikariConfig createHikariConfig()
    {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.mariadb.jdbc.Driver");

        config.setUsername(getEnvironment().getProperty("mysql.db.user"));
        config.setPassword(getEnvironment().getProperty("mysql.db.password"));

        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);

        config.setAutoCommit(false);
        config.setReadOnly(false);

        return config;
    }

    /**
     * @see de.freese.mediathek.kodi.spring.AbstractAppConfig#dataSourceAudio()
     */
    @Override
    @Bean(destroyMethod = "close")
    public DataSource dataSourceAudio()
    {
        // @Value("${mysql.audio.db.url}") final String url
        HikariConfig config = createHikariConfig();
        config.setJdbcUrl(getEnvironment().getProperty("mysql.audio.db.url"));
        config.setPoolName("dataSourceMusik");

        DataSource dataSource = new HikariDataSource(config);

        return dataSource;
    }

    /**
     * @see de.freese.mediathek.kodi.spring.AbstractAppConfig#dataSourceVideo()
     */
    @Override
    @Bean(destroyMethod = "close")
    @Primary
    public DataSource dataSourceVideo()
    {
        HikariConfig config = createHikariConfig();
        config.setJdbcUrl(getEnvironment().getProperty("mysql.video.db.url"));
        config.setPoolName("dataSourceVideo");

        DataSource dataSource = new HikariDataSource(config);

        return dataSource;
    }
}
