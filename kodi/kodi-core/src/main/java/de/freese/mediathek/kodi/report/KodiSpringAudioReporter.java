// Created: 05.04.2020
package de.freese.mediathek.kodi.report;

import javax.sql.DataSource;

import de.freese.mediathek.kodi.spring.AbstractAppConfig;
import de.freese.mediathek.kodi.spring.AppConfigSQLite;
import de.freese.mediathek.report.KodiAudioReporter;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

/**
 * @author Thomas Freese
 */
public class KodiSpringAudioReporter extends KodiAudioReporter
{
    /**
     * @param readonly boolean
     *
     * @return {@link DataSource}
     *
     * @throws Exception Falls was schiefgeht.
     */
    public DataSource createDataSource(final boolean readonly) throws Exception
    {
        ConfigurableEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addLast(new KodiPropertySource());

        AbstractAppConfig appConfig = new AppConfigSQLite();
        appConfig.setEnvironment(environment);

        return appConfig.dataSourceAudio();
    }
}
