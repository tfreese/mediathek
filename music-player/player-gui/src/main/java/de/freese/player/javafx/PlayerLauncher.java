// Created: 18 Aug. 2024
package de.freese.player.javafx;

import javafx.application.Application;

/**
 * @author Thomas Freese
 */
public final class PlayerLauncher {
    public static void main(final String[] args) {
        // Avoid Warnings at JavaFx-Bindings.
        Application.launch(PlayerApplication.class);
    }

    private PlayerLauncher() {
        super();
    }
}
