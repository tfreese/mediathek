// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.panel;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * Interface für ein Panel.
 *
 * @author Thomas Freese
 */
public interface Panel
{
    /**
     * Aufbau der GUI.
     *
     * @return {@link JComponent}
     */
    JComponent buildPanel();

    /**
     * Liefert den ApplicationContext.
     *
     * @return {@link ApplicationContext}
     */
    ApplicationContext getApplicationContext();

    /**
     * Liefert eine Bean aus dem ApplicationContext.
     *
     * @param <T> Konkreter Typ
     * @param requiredType Class
     *
     * @return Object
     *
     * @throws BeansException Falls was schiefgeht.
     */
    <T> T getBean(final Class<T> requiredType) throws BeansException;

    /**
     * Liefert den Logger.
     *
     * @return {@link Logger}
     */
    Logger getLogger();

    /**
     * Neuladen der Daten.
     */
    void reload();
}
