/**
 * Created: 16.09.2014
 */
package de.freese.mediathek.kodi.spring;

import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import de.freese.mediathek.kodi.api.MediaDAO;
import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.impl.MediaDAOImpl;
import de.freese.mediathek.kodi.impl.MediaServiceImpl;

/**
 * @author Thomas Freese
 */
@Configuration
@PropertySource("classpath:kodi.properties")
@EnableTransactionManagement
public abstract class AbstractAppConfig implements EnvironmentAware
{
    /**
     *
     */
    private Environment environment;

    /**
     * Erstellt ein neues {@link AbstractAppConfig} Object.
     */
    protected AbstractAppConfig()
    {
        super();
    }

    /**
     * @return {@link DataSource}
     */
    public abstract DataSource dataSourceAudio();

    /**
     * @return {@link DataSource}
     */
    public abstract DataSource dataSourceVideo();

    /**
     * @return Executor
     */
    @Bean(destroyMethod = "shutdownNow")
    public Executor executor()
    {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 10, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());

        return executor;
    }

    /**
     * @return {@link Environment}
     */
    protected Environment getEnvironment()
    {
        return this.environment;
    }

    /**
     * @param dataSourceVideo {@link DataSource}
     * @param dataSourceAudio {@link DataSource}
     * @return {@link MediaDAO}
     */
    @Bean
    public MediaDAO mediaDAO(@Qualifier("dataSourceVideo") final DataSource dataSourceVideo, @Qualifier("dataSourceAudio") final DataSource dataSourceAudio)
    {
        MediaDAOImpl dao = new MediaDAOImpl();
        dao.setDataSource(dataSourceVideo);

        return dao;
    }

    /**
     * @param mediaDAO {@link MediaDAO}
     * @return {@link MediaService}
     */
    @Bean
    public MediaService mediaService(final MediaDAO mediaDAO)
    {
        MediaService service = new MediaServiceImpl(mediaDAO);

        return service;
    }

    /**
     * @see org.springframework.context.EnvironmentAware#setEnvironment(org.springframework.core.env.Environment)
     */
    @Override
    public void setEnvironment(final Environment environment)
    {
        this.environment = environment;
    }

    /**
     * @param dataSource {@link DataSource}
     * @return {@link PlatformTransactionManager}
     */
    @Bean
    @Qualifier("txManagerAudio")
    public PlatformTransactionManager txManagerMusik(@Qualifier("dataSourceAudio") final DataSource dataSource)
    {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * @param dataSource {@link DataSource}
     * @return {@link PlatformTransactionManager}
     */
    @Bean
    @Qualifier("txManagerVideo")
    @Primary
    public PlatformTransactionManager txManagerVideo(@Qualifier("dataSourceVideo") final DataSource dataSource)
    {
        return new DataSourceTransactionManager(dataSource);
    }
}
