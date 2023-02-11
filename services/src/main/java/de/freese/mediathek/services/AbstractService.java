// Created: 08.11.2014
package de.freese.mediathek.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import de.freese.mediathek.utils.cache.FileResourceCache;
import de.freese.mediathek.utils.cache.ResourceCache;

/**
 * Basisimplementierung für den Zugriff auf eine Online-Media-Datenbank (www.themoviedb.org, www.thetvdb.com).
 *
 * @author Thomas Freese
 */
public abstract class AbstractService implements InitializingBean {
    private final String apiKey;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ResourceCache cache;

    private Locale locale = Locale.GERMANY;

    protected AbstractService(final String apiKey) {
        super();

        this.apiKey = apiKey;
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.apiKey, "API-Key is missing");

        if (this.cache == null) {
            this.cache = new FileResourceCache(Paths.get(System.getProperty("java.io.tmpdir"), ".javacache"));
        }
    }

    public void setCache(final ResourceCache cache) {
        this.cache = cache;
    }

    public void setLocale(final Locale locale) {
        this.locale = locale;
    }

    protected String getApiKey() {
        return this.apiKey;
    }

    protected ResourceCache getCache() {
        return this.cache;
    }

    protected Locale getLocale() {
        return this.locale;
    }

    protected Logger getLogger() {
        return this.logger;
    }

    protected String urlEncode(final String value) {
        try {
            return URLEncoder.encode(value.strip().toLowerCase(), StandardCharsets.UTF_8);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
