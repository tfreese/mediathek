// Created: 14 Juli 2024
package de.freese.player.test.ui.javafx;

import javafx.application.Application;

/**
 * @author Thomas Freese
 */
public final class MusicPlayerFxLauncher {
    static void main() {
        // Avoid Warnings at JavaFx-Bindings.
        // Logging.getLogger().setLevel(Level.SEVERE);
        System.setProperty("org.slf4j.simpleLogger.log.de.freese.player", "DEBUG");

        Application.launch(MusicPlayerFx.class);
    }

    private MusicPlayerFxLauncher() {
        super();
    }
}
