// Created: 13 Okt. 2024
package de.freese.player.ui.swing.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public final class EventLink {
    private final List<EventConsumer<Object>> eventConsumers = new ArrayList<>();

    // private EventProvider<T> provider;

    @SuppressWarnings("unchecked")
    public void addConsumer(final EventConsumer<?> consumer) {
        eventConsumers.add((EventConsumer<Object>) Objects.requireNonNull(consumer, "consumer required"));
    }

    public void setProvider(final EventProvider<?> eventProvider) {
        Objects.requireNonNull(eventProvider, "eventProvider required");

        // eventProvider.onEvent(this::fireConsumers);
        eventProvider.onEvent(event -> eventConsumers.forEach(ec -> ec.onEvent(event)));
    }

    // private <T> void fireConsumers(final T event) {
    //     eventConsumers.forEach(ec -> ec.onEvent(event));
    // }
}
