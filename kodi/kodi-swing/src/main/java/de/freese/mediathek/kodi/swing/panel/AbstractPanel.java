// Created: 16.09.2014
package de.freese.mediathek.kodi.swing.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import de.freese.mediathek.kodi.swing.action.ReloadAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public abstract class AbstractPanel implements Panel
{
    /**
     *
     */
    public final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     *
     */
    private final ApplicationContext applicationContext;
    /**
     *
     */
    private JComponent component;

    /**
     * Erstellt ein neues {@link AbstractPanel} Object.
     *
     * @param applicationContext {@link ApplicationContext}
     */
    protected AbstractPanel(final ApplicationContext applicationContext)
    {
        super();

        this.applicationContext = Objects.requireNonNull(applicationContext, "applicationContext required");
    }

    /**
     * @see Panel#buildPanel()
     */
    @Override
    public JComponent buildPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JButton(new ReloadAction(this)), BorderLayout.NORTH);

        buildPanel(panel);

        return panel;
    }

    /**
     * @see Panel#getApplicationContext()
     */
    @Override
    public ApplicationContext getApplicationContext()
    {
        return this.applicationContext;
    }

    /**
     * @see Panel#getBean(java.lang.Class)
     */
    @Override
    public <T> T getBean(final Class<T> requiredType) throws BeansException
    {
        return getApplicationContext().getBean(requiredType);
    }

    /**
     * @see Panel#getLogger()
     */
    @Override
    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Aufbau der GUI.
     *
     * @param component {@link JComponent}
     */
    protected abstract void buildPanel(JComponent component);

    /**
     * Liefert die {@link Component} des Panels.
     *
     * @return {@link JComponent}
     */
    protected JComponent getComponent()
    {
        return this.component;
    }
}
