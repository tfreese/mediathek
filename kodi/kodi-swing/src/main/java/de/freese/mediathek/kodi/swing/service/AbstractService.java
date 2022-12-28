// Created: 28.12.22
package de.freese.mediathek.kodi.swing.service;

import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.swing.controller.Controller;
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
    private Controller controller;

    protected AbstractService(final ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    public Controller getController()
    {
        return controller;
    }

    @Override
    public void link(final Controller controller)
    {
        this.controller = controller;
    }

    protected ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    protected Logger getLogger()
    {
        return logger;
    }

    protected MediaService getMediaService()
    {
        return getApplicationContext().getBean(MediaService.class);
    }
}
