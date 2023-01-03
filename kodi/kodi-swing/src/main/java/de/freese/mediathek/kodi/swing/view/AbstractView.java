// Created: 28.12.22
package de.freese.mediathek.kodi.swing.view;

import java.awt.Component;
import java.util.ResourceBundle;

import de.freese.mediathek.kodi.swing.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractView implements View
{
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Controller controller;
    private ResourceBundle resourceBundle;

    @Override
    public Component init(final Controller controller, final ResourceBundle resourceBundle)
    {
        this.controller = controller;
        this.resourceBundle = resourceBundle;

        return null;
    }

    protected Controller getController()
    {
        return controller;
    }

    protected Logger getLogger()
    {
        return logger;
    }

    protected String getTranslation(String key)
    {
        return resourceBundle.getString(key);
    }
}
