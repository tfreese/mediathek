package de.freese.mediathek.services.themoviedb.impl;

import java.util.Objects;

import org.springframework.web.client.RestClient;

import de.freese.mediathek.services.AbstractService;

/**
 * Abstracter Basisservice.
 *
 * @author Thomas Freese
 * @since 26.04.2014
 */
public abstract class AbstractMovieDbService extends AbstractService {
    private final RestClient restClient;

    protected AbstractMovieDbService(final RestClient restClient, final String apiKey) {
        super(apiKey);

        this.restClient = Objects.requireNonNull(restClient, "restClient required");
    }

    protected RestClient getRestClient() {
        return restClient;
    }

    /**
     * Liefert <a href="https://api.themoviedb.org/3/">themoviedb</a>.
     */
    protected StringBuilder url() {
        return new StringBuilder("https://api.themoviedb.org/3/");
    }
}
