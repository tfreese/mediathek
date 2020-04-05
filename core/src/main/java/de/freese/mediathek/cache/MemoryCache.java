/**
 * Created: 27.07.2016
 */

package de.freese.mediathek.cache;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Cache Implementierung, die Daten im Speicher ablegt.
 *
 * @author Thomas Freese
 */
public class MemoryCache extends AbstractCache
{
    /**
     *
     */
    private final Map<String, byte[]> map;

    /**
     * Erstellt ein neues {@link MemoryCache} Object.
     */
    public MemoryCache()
    {
        super();

        this.map = new TreeMap<>();
    }

    /**
     * @see Cache#clear()
     */
    @Override
    public void clear()
    {
        this.map.clear();
    }

    /**
     * @see Cache#getResource(java.lang.String)
     */
    @Override
    public Optional<Resource> getResource(final String uri)
    {
        String key = generateKey(uri);
        byte[] content = this.map.get(key);

        if (content == null)
        {
            try
            {
                Resource urlResource = new UrlResource(uri);
                int size = Math.max((int) urlResource.contentLength(), 32);

                try (InputStream inputStream = urlResource.getInputStream();
                     ByteArrayOutputStream baos = new ByteArrayOutputStream(size))
                {
                    byte[] buffer = new byte[1024];
                    // long count = 0;
                    int n = 0;

                    while ((n = inputStream.read(buffer)) != -1)
                    {
                        baos.write(buffer, 0, n);
                        // count += n;
                    }

                    content = baos.toByteArray();
                    this.map.put(key, content);
                }
            }
            catch (final Exception ex)
            {
                getLogger().error(null, ex);
            }
        }

        return Optional.ofNullable(new ByteArrayResource(content));
    }
}
