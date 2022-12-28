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
    private Service service;
    private View view;

    public Service getService()
    {
        return service;
    }

    public View getView()
    {
        return view;
    }

    @Override
    public void link(final Service service, final View view)
    {
        this.service = service;
        this.view = view;

        this.service.link(this);
        this.view.link(this);
    }

    protected Logger getLogger()
    {
        return logger;
    }
}
