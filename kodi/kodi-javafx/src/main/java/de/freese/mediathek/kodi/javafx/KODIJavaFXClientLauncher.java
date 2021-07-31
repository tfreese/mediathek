// Created: 18.01.2021
package de.freese.mediathek.kodi.javafx;

import javafx.application.Application;

/**
 * @author Thomas Freese
 */
public class KODIJavaFXClientLauncher
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        // Warn-Meldungen bei Bindings verhindern.
        // Logging.getLogger().setLevel(Level.SEVERE);
        System.setProperty("org.slf4j.simpleLogger.log.de.freese.mediathek.kodi.javafx", "DEBUG");

        Application.launch(KODIJavaFXClient.class);
    }
}
