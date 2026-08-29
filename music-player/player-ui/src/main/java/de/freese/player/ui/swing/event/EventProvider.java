package de.freese.player.ui.swing.event;

import java.util.function.Consumer;

/**
 * @author Thomas Freese
 * @since 12.10.2024
 */
@FunctionalInterface
public interface EventProvider<T> extends Consumer<Consumer<T>> {
    @Override
    default void accept(final Consumer<T> consumer) {
        onEvent(consumer);
    }

    void onEvent(Consumer<T> consumer);
}
