/**
 * Created: 08.11.2014
 */
package de.freese.mediathek.services;

import java.net.URLEncoder;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import de.freese.mediathek.cache.Cache;
import de.freese.mediathek.cache.FileCache;

/**
 * Basisimplementierung für den Zugriff auf eine Online-Media-Datenbank (www.themoviedb.org, www.thetvdb.com).
 *
 * @author Thomas Freese
 */
public abstract class AbstractService implements InitializingBean
{
    /**
     *
     */
    private final String apiKey;
    /**
     *
     */
    private Cache cache = null;

    /**
     *
     */
    private Locale locale = Locale.GERMANY;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erstellt ein neues {@link AbstractService} Object.
     *
     * @param apiKey String
     */
    protected AbstractService(final String apiKey)
    {
        super();

        this.apiKey = apiKey;
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(this.apiKey, "API-Key is missing");

        if (this.cache == null)
        {
            this.cache = new FileCache();
        }
    }

    /**
     * @return String
     */
    protected String getApiKey()
    {
        return this.apiKey;
    }

    /**
     * @return {@link Cache}
     */
    protected Cache getCache()
    {
        return this.cache;
    }

    /**
     * @return {@link Locale}
     */
    protected Locale getLocale()
    {
        return this.locale;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @param cache {@link Cache}
     */
    public void setCache(final Cache cache)
    {
        this.cache = cache;
    }

    /**
     * @param locale {@link Locale}
     */
    public void setLocale(final Locale locale)
    {
        this.locale = locale;
    }

    /**
     * @param value String
     * @return String
     */
    protected String urlEncode(final String value)
    {
        try
        {
            String encoded = URLEncoder.encode(value.trim().toLowerCase(), "UTF-8");

            return encoded;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
