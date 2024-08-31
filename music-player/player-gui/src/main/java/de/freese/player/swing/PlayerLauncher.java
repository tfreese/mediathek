// Created: 18 Aug. 2024
package de.freese.player.swing;

import javax.swing.SwingUtilities;

/**
 * @author Thomas Freese
 */
public final class PlayerLauncher {
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                PlayerFrame.init();
                PlayerFrame.start();
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private PlayerLauncher() {
        super();
    }
}
