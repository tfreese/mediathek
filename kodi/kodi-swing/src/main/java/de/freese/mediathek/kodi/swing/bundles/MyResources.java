// Created: 28.12.22
package de.freese.mediathek.kodi.swing.bundles;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class MyResources extends AbstractMapResourceBundle {
    @Override
    protected Map<String, Object> getContents() {
        final Map<String, Object> map = new HashMap<>();

        map.put("reload", "Reload");
        map.put("id.label.show", "TvDb Id");
        map.put("id.label.movie", "ImDb Id");
        map.put("shows", "Shows");
        map.put("movies", "Movies");
        map.put("filter", "Filter");
        map.put("details", "Details");
        map.put("genres", "Genres");
        map.put("genres.edit", "Edit Genres");
        map.put("frame.title", "KODI-Client");

        return map;
    }
}
