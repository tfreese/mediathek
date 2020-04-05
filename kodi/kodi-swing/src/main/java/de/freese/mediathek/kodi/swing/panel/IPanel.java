/**
 * Created: 28.09.2014
 */

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
public interface IPanel
{
	/**
	 * Aufbau der GUI.
	 * 
	 * @return {@link JComponent}
	 */
	public JComponent buildPanel();

	/**
	 * Liefert den ApplicationContext.
	 * 
	 * @return {@link ApplicationContext}
	 */
	public ApplicationContext getApplicationContext();

	/**
	 * Liefert eine Bean des ApplicationContextes.
	 * 
	 * @param <T> Konkreter Typ
	 * @param requiredType Class
	 * @return Object
	 * @throws BeansException Falls was schief geht.
	 */
	public <T> T getBean(final Class<T> requiredType) throws BeansException;

	/**
	 * Liefert den Logger.
	 * 
	 * @return {@link Logger}
	 */
	public Logger getLogger();

	/**
	 * Neuladen der Daten.
	 */
	public void reload();
}