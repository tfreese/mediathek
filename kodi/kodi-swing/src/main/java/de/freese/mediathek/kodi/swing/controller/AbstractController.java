// Created: 26.12.22
package de.freese.mediathek.kodi.swing.controller;

import de.freese.mediathek.utils.cache.ResourceCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractController
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ResourceCache resourceCache;

    protected AbstractController(ResourceCache resourceCache)
    {
        super();

        this.resourceCache = resourceCache;
    }

    public ResourceCache getResourceCache()
    {
        return resourceCache;
    }

    protected Logger getLogger()
    {
        return logger;
    }
}
