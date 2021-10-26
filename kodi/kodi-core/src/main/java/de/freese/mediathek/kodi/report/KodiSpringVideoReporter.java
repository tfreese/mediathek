// Created: 05.04.2020
package de.freese.mediathek.kodi.report;

import javax.sql.DataSource;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

import de.freese.mediathek.kodi.spring.AbstractAppConfig;
import de.freese.mediathek.kodi.spring.AppConfigSQLite;
import de.freese.mediathek.musik.KodiVideoReporter;

/**
 * @author Thomas Freese
 */
public class KodiSpringVideoReporter extends KodiVideoReporter
{
    /**
     * @param readonly boolean
     *
     * @return {@link DataSource}
     *
     * @throws Exception Falls was schief geht.
     */
    public DataSource createDataSource(final boolean readonly) throws Exception
    {
        ConfigurableEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addLast(new KodiPropertySource());

        AbstractAppConfig appConfig = new AppConfigSQLite();
        appConfig.setEnvironment(environment);

        return appConfig.dataSourceVideo();
    }
}
