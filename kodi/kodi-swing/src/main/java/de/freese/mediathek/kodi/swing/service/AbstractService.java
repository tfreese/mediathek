// Created: 28.12.22
package de.freese.mediathek.kodi.swing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public abstract class AbstractService implements Service
{
    private final ApplicationContext applicationContext;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected AbstractService(final ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    @Override
    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    protected Logger getLogger()
    {
        return logger;
    }
}
