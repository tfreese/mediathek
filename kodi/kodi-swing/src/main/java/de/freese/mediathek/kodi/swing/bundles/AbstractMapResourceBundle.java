package de.freese.mediathek.kodi.swing.bundles;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.jspecify.annotations.NonNull;

/**
 * @author Thomas Freese
 * @since 03.01.2023
 */
public abstract class AbstractMapResourceBundle extends ResourceBundle {
    private Map<String, Object> lookup;

    @Override
    public @NonNull Enumeration<String> getKeys() {
        if (lookup == null) {
            loadLookup();
        }

        final Set<String> keys = new HashSet<>(lookup.keySet());

        if (parent != null) {
            parent.getKeys().asIterator().forEachRemaining(keys::add);
        }

        return Collections.enumeration(keys);
    }

    protected abstract Map<String, Object> getContents();

    @Override
    protected Object handleGetObject(final @NonNull String key) {
        if (lookup == null) {
            loadLookup();
        }

        if (parent != null) {
            return lookup.get(key);
        }

        return lookup.getOrDefault(key, "_" + key + "_");
    }

    @Override
    protected @NonNull Set<String> handleKeySet() {
        if (lookup == null) {
            loadLookup();
        }

        return lookup.keySet();
    }

    private synchronized void loadLookup() {
        if (lookup != null) {
            return;
        }

        lookup = getContents();
    }
}
