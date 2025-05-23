// Created: 16.09.2014
package de.freese.mediathek.kodi.spring;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * @author Thomas Freese
 */
@Configuration
@Profile("hsqldb")
public class AppConfigHsqlDb extends AbstractAppConfig {
    private static SingleConnectionDataSource createSingleConnectionDataSource() {
        final SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setAutoCommit(false);
        dataSource.setSuppressClose(true);

        return dataSource;
    }

    @Override
    @Bean
    public DataSource dataSourceAudio() {
        final SingleConnectionDataSource dataSource = createSingleConnectionDataSource();
        dataSource.setUrl(getEnvironment().getProperty("hsqldb.audio.db.url"));

        return dataSource;
    }

    @Override
    @Bean
    @Primary
    public DataSource dataSourceVideo() {
        final SingleConnectionDataSource dataSource = createSingleConnectionDataSource();
        dataSource.setUrl("jdbc:hsqldb:mem:kodi_video");

        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("kodi_video_hsqldb_schema.sql"));
        populator.addScript(new ClassPathResource("kodi_video_hsqldb_data.sql"));
        populator.execute(dataSource);

        return dataSource;
    }
}
