// Created: 29 Jan. 2025
package de.freese.player.core.player;

import java.time.Duration;

import de.freese.player.core.model.Window;

/**
 * @author Thomas Freese
 */
public interface AudioPlayerSource {
    void close();

    void jumpTo(final Duration duration);

    Window nextWindow();
}
