// Created: 28.12.22
package de.freese.mediathek.kodi.swing.controller;

import java.awt.Component;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.mediathek.kodi.swing.service.Service;
import de.freese.mediathek.kodi.swing.view.View;

/**
 * @author Thomas Freese
 */
public abstract class AbstractController implements Controller {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Service service;

    private final View view;

    protected AbstractController(final Service service, View view) {
        super();

        this.service = service;
        this.view = view;
    }

    @Override
    public Component init(final ResourceBundle resourceBundle) {
        return getView().init(this, resourceBundle);
    }

    protected Logger getLogger() {
        return logger;
    }

    protected Service getService() {
        return service;
    }

    protected View getView() {
        return view;
    }
}
