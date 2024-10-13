// Created: 09 Okt. 2024
package de.freese.player.ui.swing.event;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Thomas Freese
 */
public abstract class AbstractEventProvider<T> implements EventProvider<T> {
    private Consumer<T> eventConsumer;

    @Override
    public void onEvent(final Consumer<T> consumer) {
        eventConsumer = Objects.requireNonNull(consumer, "consumer required");
    }

    protected void fireConsumer(final T value) {
        eventConsumer.accept(value);
    }
}
