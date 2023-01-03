// Created: 28.12.22
package de.freese.mediathek.kodi.swing.bundles;

import java.util.Map;

/**
 * @author Thomas Freese
 */
public class MyResources_de extends AbstractMapResourceBundle
{
    @Override
    protected Map<String, Object> getContents()
    {
        return Map.of("shows", "Serien", "movies", "Filme");
    }
}
