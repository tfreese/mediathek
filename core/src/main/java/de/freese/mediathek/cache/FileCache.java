/**
 * Created: 18.09.2014
 */
package de.freese.mediathek.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/**
 * Cache Implementierung, die Daten auf der Festplatte ablegt.
 *
 * @author Thomas Freese
 */
public class FileCache extends AbstractCache
{
    /**
     *
     */
    private final Path cacheDirectory;

    /**
     * Erstellt ein neues {@link FileCache} Object im Ordner "java.io.tmpdir/.mediacache".
     */
    public FileCache()
    {
        // System.getProperty("java.io.tmpdir") + File.separator + ".mediacache" + File.separator;
        this(Paths.get(System.getProperty("java.io.tmpdir"), ".mediacache"));
    }

    /**
     * Erstellt ein neues {@link FileCache} Object.
     *
     * @param cacheDirectory {@link File}
     */
    public FileCache(final File cacheDirectory)
    {
        this(cacheDirectory.toPath());
    }

    /**
     * Erstellt ein neues {@link FileCache} Object.
     *
     * @param cacheDirectory {@link Path}
     */
    public FileCache(final Path cacheDirectory)
    {
        super();

        this.cacheDirectory = cacheDirectory;

        try
        {
            // Falls Verzeichnis nicht vorhanden -> erzeugen.

            // final File file = new File(CACHE_DIR);
            //
            // if (!file.exists())
            // {
            // file.mkdirs();
            // }

            // Files.deleteIfExists(directory); // Funktioniert nur, wenn das Verzeichniss leer ist.
            if (!Files.exists(cacheDirectory))
            {
                Files.createDirectories(cacheDirectory);
            }
        }
        catch (final Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see Cache#clear()
     */
    @Override
    public void clear()
    {
        try
        {
            Files.walkFileTree(this.cacheDirectory, new SimpleFileVisitor<>()
            {
                /**
                 * @see java.nio.file.SimpleFileVisitor#postVisitDirectory(java.lang.Object, java.io.IOException)
                 */
                @Override
                public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException
                {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

                /**
                 * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
                 */
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException
                {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);

            throw new RuntimeException(ex);
        }
    }

    /**
     * @see Cache#getResource(java.lang.String)
     */
    @Override
    public Optional<Resource> getResource(final String uri)
    {
        //@formatter:off
        return Stream.of(uri)
                .map(u -> generateKey(u))
                .map(key -> this.cacheDirectory.resolve(key))
                .map(path ->
                        {
                            if (!Files.isReadable(path))
                            {
                                try
                                {
                                    try (InputStream inputStream = new UrlResource(uri).getInputStream())
                                    {
                                        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
                                    }
                                }
                                catch (final Exception ex)
                                {
                                    getLogger().error(null, ex);
                                }
                            }

                            return (Resource) new FileSystemResource(path);
                })
                .findFirst();
        //@formatter:on
    }
}
