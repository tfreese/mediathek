// Created: 26.04.2014
package de.freese.mediathek.services.themoviedb.impl;

import de.freese.mediathek.services.AbstractService;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * Abstracter Basisservice.
 *
 * @author Thomas Freese
 */
public abstract class AbstractMovieDBService extends AbstractService
{
    /**
     *
     */
    private RestTemplate restTemplate;

    /**
     * Erstellt ein neues {@link AbstractMovieDBService} Object.
     *
     * @param apiKey String
     */
    protected AbstractMovieDBService(final String apiKey)
    {
        super(apiKey);
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(getApiKey(), "API-Key is missing");

        if (this.restTemplate == null)
        {
            this.restTemplate = new RestTemplate();
        }
    }

    /**
     * @return {@link RestTemplate}
     */
    protected RestTemplate getRestTemplate()
    {
        return this.restTemplate;
    }

    /**
     * @param restTemplate {@link RestTemplate}
     */
    public void setRestTemplate(final RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    /**
     * Liefert <a href="https://api.themoviedb.org/3/">themoviedb</a>.
     *
     * @return {@link StringBuilder}
     */
    protected StringBuilder url()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("https://api.themoviedb.org/3/");

        return sb;
    }
}
