// Created: 14 Juli 2024
package de.freese.player.player;

import de.freese.player.exception.PlayerException;

/**
 * @author Thomas Freese
 */
public interface ClipPlayer extends AutoCloseable {
    void close() throws PlayerException;

    void pause();

    void play();

    void resume();

    void stop();
}
