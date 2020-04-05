/**
 * Created: 22.05.2016
 */
package de.freese.mediathek.cache;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.core.io.Resource;

import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestCache
{
    /**
     *
     */
    private static Cache CACHE = null;

    /**
     *
     */
    private static String URL_FILE = null;

    /**
     *
     */
    private static String URL_IMAGE = null;

    /**
     *
     */
    @AfterAll
    public static void afterAll()
    {
        CACHE.clear();
    }

    /**
     *
     */
    @BeforeAll
    public static void beforeAll()
    {
        // CACHE = new FileCache();
        CACHE = new MemoryCache();
        URL_FILE = Paths.get("src/test/java/de/freese/mediathek/cache/TestCache.java").toUri().toString();
        URL_IMAGE = "http://www.freese-home.de/s/img/emotionheader.jpg";
    }

    /**
     * Erstellt ein neues {@link TestCache} Object.
     */
    public TestCache()
    {
        super();
    }

    /**
     *
     */
    @AfterEach
    public void afterEach()
    {
        // Empty
    }

    /**
     *
     */
    @BeforeEach
    public void beforeEach()
    {
        // Empty
    }

    /**
     *
     */
    @Test
    public void test0010FileCache()
    {
        Optional<Resource> optional = CACHE.getResource(URL_FILE);
        assertNotNull(optional);
        assertNotNull(optional.get());

        // Reload
        optional = CACHE.getResource(URL_FILE);
        assertNotNull(optional);
        assertNotNull(optional.get());
    }

    /**
     *
     */
    @Test
    public void test0020URLCache()
    {
        Optional<Resource> optional = CACHE.getResource(URL_IMAGE);
        assertNotNull(optional);
        assertNotNull(optional.get());

        // Reload
        optional = CACHE.getResource(URL_IMAGE);
        assertNotNull(optional);
        assertNotNull(optional.get());
    }
}
