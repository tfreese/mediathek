// Created: 03.01.23
package de.freese.mediathek.kodi.swing.bundles;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Thomas Freese
 */
public abstract class AbstractMapResourceBundle extends ResourceBundle {
    private volatile Map<String, Object> lookup;

    @Override
    public Enumeration<String> getKeys() {
        if (lookup == null) {
            loadLookup();
        }

        final Set<String> keys = new HashSet<>(lookup.keySet());

        if (this.parent != null) {
            this.parent.getKeys().asIterator().forEachRemaining(keys::add);
        }

        return Collections.enumeration(keys);
    }

    protected abstract Map<String, Object> getContents();

    @Override
    protected Object handleGetObject(final String key) {
        if (lookup == null) {
            loadLookup();
        }

        if (key == null) {
            throw new NullPointerException();
        }

        if (this.parent != null) {
            return lookup.get(key);
        }

        return lookup.getOrDefault(key, "_" + key + "_");
    }

    @Override
    protected Set<String> handleKeySet() {
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
