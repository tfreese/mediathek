// Created: 08.11.2014
package de.freese.mediathek.utils.cache;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;

/**
 * Interface für einen Resource-Cache.<br>
 * Wenn diese nicht vorhanden ist, wird über die {@link URL} nachgeladen.
 *
 * @author Thomas Freese
 */
public interface ResourceCache extends Function<URL, Optional<InputStream>>
{
    /**
     * @see Function#apply(Object)
     */
    @Override
    default Optional<InputStream> apply(final URL url)
    {
        return getResource(url);
    }

    /**
     * Leert den Cache.
     */
    void clear();

    /**
     * Laden der Resource, wenn nicht vorhanden.
     *
     * @param uri {@link URI}; file://...; http://...
     *
     * @return {@link Optional}
     */
    Optional<InputStream> getResource(final URI uri);

    /**
     * Laden der Resource, wenn nicht vorhanden.
     *
     * @param url {@link URL}; file://...; http://...
     *
     * @return {@link Optional}
     */
    default Optional<InputStream> getResource(final URL url)
    {
        try
        {
            return getResource(url.toURI());
        }
        catch (URISyntaxException ex)
        {
            throw new RuntimeException(ex);
        }
    }
}