package de.freese.mediathek.services.themoviedb.impl;

import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import de.freese.mediathek.services.AbstractService;

/**
 * Abstracter Basisservice.
 *
 * @author Thomas Freese
 * @since 26.04.2014
 */
public abstract class AbstractMovieDbService extends AbstractService {
    private RestTemplate restTemplate;

    protected AbstractMovieDbService(final String apiKey) {
        super(apiKey);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(getApiKey(), "API-Key is missing");

        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
    }

    public void setRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    /**
     * Liefert <a href="https://api.themoviedb.org/3/">themoviedb</a>.
     */
    protected StringBuilder url() {
        return new StringBuilder("https://api.themoviedb.org/3/");
    }
}
