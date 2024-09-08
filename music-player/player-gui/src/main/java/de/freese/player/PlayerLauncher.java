// Created: 08 Sept. 2024
package de.freese.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.swing.SwingUtilities;

import javafx.application.Application;

import de.freese.player.swing.PlayerApplication;

/**
 * @author Thomas Freese
 */
public final class PlayerLauncher {
    public static void main(final String[] args) {
        final Map<String, String> arguments = parseArguments(args);

        if ("swing".equals(arguments.get("-gui"))) {
            ApplicationContext.start();

            SwingUtilities.invokeLater(() -> {
                try {
                    PlayerApplication.init();
                    PlayerApplication.start();
                }
                catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        else if ("javafx".equals(arguments.get("-gui"))) {
            ApplicationContext.start();

            // Avoid Warnings at JavaFx-Bindings.
            Application.launch(de.freese.player.javafx.PlayerApplication.class);
        }
        else {
            throw new IllegalArgumentException("required arguments: -gui [swing,javafx]");
        }
    }

    /**
     * Required Format: -KEY1 VALUE1 -KEY2 VALUE2 ...
     */
    private static Map<String, String> parseArguments(final String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("arguments are null or empty");
        }

        final List<String> arguments = new ArrayList<>(List.of(args));
        final Map<String, String> map = new HashMap<>();

        try {
            while (!arguments.isEmpty()) {
                if ("-gui".equals(arguments.getFirst())) {
                    map.put(arguments.removeFirst(), arguments.removeFirst());
                }
                else {
                    // Unknown Argument
                    arguments.removeFirst();
                }
            }
        }
        catch (NoSuchElementException ex) {
            throw new IllegalArgumentException("required arguments: -gui [swing,javafx]");
        }

        return map;
    }

    private PlayerLauncher() {
        super();
    }
}
