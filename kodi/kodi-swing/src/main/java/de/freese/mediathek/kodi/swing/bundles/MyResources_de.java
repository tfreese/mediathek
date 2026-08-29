package de.freese.mediathek.kodi.swing.bundles;

import java.util.Map;

/**
 * @author Thomas Freese
 * @since 28.12.2022
 */
public class MyResources_de extends AbstractMapResourceBundle {
    @Override
    protected Map<String, Object> getContents() {
        return Map.of("shows", "Serien", "movies", "Filme");
    }
}
