// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.panel;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public interface Panel
{
    JComponent buildPanel();

    ApplicationContext getApplicationContext();

    <T> T getBean(final Class<T> requiredType) throws BeansException;

    Logger getLogger();

    void reload();
}
