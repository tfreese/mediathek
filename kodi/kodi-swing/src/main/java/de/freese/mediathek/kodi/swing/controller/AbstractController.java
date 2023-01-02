// Created: 28.12.22
package de.freese.mediathek.kodi.swing.controller;

import de.freese.mediathek.kodi.swing.service.Service;
import de.freese.mediathek.kodi.swing.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractController<S extends Service> implements Controller
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final S service;
    private View view;

    protected AbstractController(final S service)
    {
        super();

        this.service = service;
    }

    public S getService()
    {
        return service;
    }

    public View getView()
    {
        return view;
    }

    @Override
    public void link(final View view)
    {
        this.view = view;

        this.view.link(this);
    }

    protected Logger getLogger()
    {
        return logger;
    }
}
