// Created: 18.01.2021
package de.freese.mediathek.kodi.javafx;

import javafx.application.Application;

/**
 * @author Thomas Freese
 */
public final class KodiJavaFxClientLauncher {
    public static void main(final String[] args) {
        // Avoid Warnings at JavaFx-Bindings.
        // Logging.getLogger().setLevel(Level.SEVERE);
        System.setProperty("org.slf4j.simpleLogger.log.de.freese.mediathek.kodi.javafx", "DEBUG");

        Application.launch(KodiJavaFxClient.class);
    }

    private KodiJavaFxClientLauncher() {
        super();
    }
}
