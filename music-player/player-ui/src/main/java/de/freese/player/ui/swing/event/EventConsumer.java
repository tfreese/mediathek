// Created: 13 Okt. 2024
package de.freese.player.ui.swing.event;

import java.util.function.Consumer;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface EventConsumer<T> extends Consumer<T> {
    @Override
    default void accept(final T t) {
        onEvent(t);
    }

    void onEvent(T event);
}
