// Created: 28.12.22
package de.freese.mediathek.kodi.swing.controller;

import de.freese.mediathek.kodi.swing.service.Service;
import de.freese.mediathek.kodi.swing.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractController implements Controller
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Service service;
    private final View view;

    protected AbstractController(final Service service, View view)
    {
        super();

        this.service = service;
        this.view = view;

        this.view.link(this);
    }

    @Override
    public Service getService()
    {
        return service;
    }

    @Override
    public View getView()
    {
        return view;
    }

    protected Logger getLogger()
    {
        return logger;
    }
}
