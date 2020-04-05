/**
 * Created: 16.09.2014
 */
package de.freese.mediathek.kodi.swing.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import de.freese.mediathek.kodi.swing.action.ReloadAction;

/**
 * @author Thomas Freese
 */
public abstract class AbstractPanel implements IPanel
{
    /**
     *
     */
    private ApplicationContext applicationContext = null;

    /**
     *
     */
    private JComponent component = null;

    /**
     *
     */
    public final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erstellt ein neues {@link AbstractPanel} Object.
     *
     * @param applicationContext {@link ApplicationContext}
     */
    public AbstractPanel(final ApplicationContext applicationContext)
    {
        super();

        this.applicationContext = applicationContext;
    }

    /**
     * @see de.freese.mediathek.kodi.swing.panel.IPanel#buildPanel()
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
     * Aufbau der GUI.
     *
     * @param component {@link JComponent}
     */
    protected abstract void buildPanel(JComponent component);

    /**
     * @see de.freese.mediathek.kodi.swing.panel.IPanel#getApplicationContext()
     */
    @Override
    public ApplicationContext getApplicationContext()
    {
        return this.applicationContext;
    }

    /**
     * @see de.freese.mediathek.kodi.swing.panel.IPanel#getBean(java.lang.Class)
     */
    @Override
    public <T> T getBean(final Class<T> requiredType) throws BeansException
    {
        return getApplicationContext().getBean(requiredType);
    }

    /**
     * Liefert die {@link Component} des Panels.
     *
     * @return {@link JComponent}
     */
    protected JComponent getComponent()
    {
        return this.component;
    }

    /**
     * @see de.freese.mediathek.kodi.swing.panel.IPanel#getLogger()
     */
    @Override
    public Logger getLogger()
    {
        return this.logger;
    }
}
