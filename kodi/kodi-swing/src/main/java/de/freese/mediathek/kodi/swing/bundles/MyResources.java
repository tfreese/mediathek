// Created: 28.12.22
package de.freese.mediathek.kodi.swing.bundles;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Thomas Freese
 */
public class MyResources extends ResourceBundle
{
    private final Map<String, Object> map = new TreeMap<>();

    public MyResources()
    {
        super();

        initMap(map);
    }

    @Override
    public Enumeration<String> getKeys()
    {
        return Collections.enumeration(keySet());
    }

    @Override
    protected Object handleGetObject(final String key)
    {
        return map.getOrDefault(key, "_" + key + "_");
    }

    @Override
    protected Set<String> handleKeySet()
    {
        return new TreeSet<>(map.keySet());
    }

    protected void initMap(Map<String, Object> map)
    {
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
    }
}
