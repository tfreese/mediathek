// Created: 05.04.2020
package de.freese.mediathek.kodi.report;

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
    public DataSource createDataSource(final boolean readonly) throws Exception {
        ConfigurableEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addLast(new KodiPropertySource());

        AbstractAppConfig appConfig = new AppConfigSqLite();
        appConfig.setEnvironment(environment);

        return appConfig.dataSourceAudio();
    }
}
