// Created: 17 Juli 2024
package de.freese.player.core.exception;

import java.io.Serial;

/**
 * @author Thomas Freese
 */
public class PlayerException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -1802911351540138700L;

    public PlayerException() {
        super();
    }

    public PlayerException(final Throwable cause) {
        super(cause);
    }

    public PlayerException(final String message) {
        super(message);
    }

    public PlayerException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
