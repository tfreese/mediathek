// Created: 13 Okt. 2024
package de.freese.player.ui.swing.event;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public final class EventRegistry {
    private static final EventRegistry INSTANCE = new EventRegistry();

    public static EventRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, EventLink> map = new HashMap<>();

    private EventRegistry() {
        super();
    }

    public void registerConsumer(final String name, final EventConsumer<?> consumer) {
        map.computeIfAbsent(name, key -> new EventLink()).addConsumer(consumer);
    }

    public void registerProvider(final String name, final EventProvider<?> provider) {
        map.computeIfAbsent(name, key -> new EventLink()).setProvider(provider);
    }
}
