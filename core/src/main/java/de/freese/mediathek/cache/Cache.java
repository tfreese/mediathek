/**
 * Created: 08.11.2014
 */
package de.freese.mediathek.cache;

import org.springframework.core.io.Resource;

import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;

/**
 * Interface für den Cache.
 *
 * @author Thomas Freese
 */
public interface Cache extends Function<String, Optional<Resource>>
{
    /**
     * @see java.util.function.Function#apply(java.lang.Object)
     */
    @Override
    public default Optional<Resource> apply(final String t)
    {
        return getResource(t);
    }

    /**
     * Leert den Cache.
     */
    public void clear();

    /**
     * Cached die Resourcen im Verzeichnis java.io.tmpdir/.mediacache.
     *
     * @param uri String; file://...; http://...
     *
     * @return {@link Optional}
     */
    public Optional<Resource> getResource(final String uri);

    /**
     * Cached die Resourcen im Verzeichnis java.io.tmpdir/.mediacache.
     *
     * @param uri {@link URI}
     *
     * @return {@link Optional}
     */
    public default Optional<Resource> getResource(final URI uri)
    {
        return getResource(uri.toString());
    }

    /**
     * Cached die Resourcen im Verzeichnis java.io.tmpdir/.mediacache.
     *
     * @param url {@link URL}
     *
     * @return {@link Optional}
     */
    public default Optional<Resource> getResource(final URL url)
    {
        return getResource(url.toString());
    }
}
