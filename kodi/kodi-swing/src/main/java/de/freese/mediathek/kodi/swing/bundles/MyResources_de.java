// Created: 28.12.22
package de.freese.mediathek.kodi.swing.bundles;

import java.util.Map;

/**
 * @author Thomas Freese
 */
public class MyResources_de extends MyResources
{
    @Override
    protected void initMap(final Map<String, Object> map)
    {
        super.initMap(map);

        map.put("shows", "Serien");
        map.put("movies", "Filme");
    }
}
