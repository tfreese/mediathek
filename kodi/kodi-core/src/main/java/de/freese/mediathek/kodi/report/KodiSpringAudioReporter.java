// Created: 05.04.2020
package de.freese.mediathek.kodi.report;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

import de.freese.mediathek.kodi.spring.AbstractAppConfig;
import de.freese.mediathek.kodi.spring.AppConfigSqLite;
import de.freese.mediathek.report.KodiAudioReporter;

/**
 * @author Thomas Freese
 */
public class KodiSpringAudioReporter extends KodiAudioReporter {
    private static DataSource createDataSource() throws IOException {
        final ConfigurableEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addLast(new KodiPropertySource());

        final AbstractAppConfig appConfig = new AppConfigSqLite();
        appConfig.setEnvironment(environment);

        return appConfig.dataSourceAudio();
    }

    public KodiSpringAudioReporter() throws IOException {
        super(createDataSource());
    }
}
