// Created: 16.09.2014
package de.freese.mediathek.kodi.spring;

import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.freese.mediathek.kodi.api.MediaDao;
import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.impl.MediaDaoImpl;
import de.freese.mediathek.kodi.impl.MediaServiceImpl;
import de.freese.mediathek.utils.cache.FileResourceCache;
import de.freese.mediathek.utils.cache.ResourceCache;

/**
 * @author Thomas Freese
 */
@Configuration
@PropertySource("classpath:kodi.properties")
@EnableTransactionManagement
public abstract class AbstractAppConfig implements EnvironmentAware {
    private Environment environment;

    public abstract DataSource dataSourceAudio();

    public abstract DataSource dataSourceVideo();

    // @Bean(destroyMethod = "shutdownNow")
    // public Executor executor()
    // {
    // ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 10, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());
    //
    // return executor;
    // }

    @Bean
    @ConditionalOnMissingBean({Executor.class, ExecutorService.class})
    @Primary
    public ThreadPoolExecutorFactoryBean executorService() {
        final int coreSize = Math.max(8, Runtime.getRuntime().availableProcessors());
        final int maxSize = coreSize * 2;
        final int queueSize = maxSize * 2;
        final int keepAliveSeconds = 60;

        final ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
        bean.setCorePoolSize(coreSize);
        bean.setMaxPoolSize(maxSize);
        bean.setQueueCapacity(queueSize);
        bean.setKeepAliveSeconds(keepAliveSeconds);
        bean.setThreadPriority(Thread.NORM_PRIORITY);
        bean.setThreadNamePrefix("kodi-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        bean.setAllowCoreThreadTimeOut(false);
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }

    @Bean
    public MediaDao mediaDAO(@Qualifier("dataSourceVideo") final DataSource dataSourceVideo, @Qualifier("dataSourceAudio") final DataSource dataSourceAudio) {
        final MediaDaoImpl dao = new MediaDaoImpl();
        dao.setDataSource(dataSourceVideo);

        return dao;
    }

    @Bean
    public MediaService mediaService(final MediaDao mediaDAO) {
        return new MediaServiceImpl(mediaDAO);
    }

    @Bean(destroyMethod = "clear")
    public ResourceCache resourceCache() {
        return new FileResourceCache(Paths.get(System.getProperty("java.io.tmpdir"), ".javaCache"));
    }

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    @Bean
    @Qualifier("txManagerAudio")
    public PlatformTransactionManager txManagerAudio(@Qualifier("dataSourceAudio") final DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @Qualifier("txManagerVideo")
    @Primary
    public PlatformTransactionManager txManagerVideo(@Qualifier("dataSourceVideo") final DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    protected Environment getEnvironment() {
        return this.environment;
    }
}
