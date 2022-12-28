// Created: 28.12.22
package de.freese.mediathek.kodi.swing.view;

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
    private final ResourceBundle resourceBundle;
    private Controller controller;

    protected AbstractView(ResourceBundle resourceBundle)
    {
        super();

        this.resourceBundle = resourceBundle;
    }

    public Controller getController()
    {
        return controller;
    }

    public String getTranslation(String key)
    {
        return resourceBundle.getString(key);
    }

    @Override
    public void link(final Controller controller)
    {
        this.controller = controller;
    }

    protected Logger getLogger()
    {
        return logger;
    }
}
