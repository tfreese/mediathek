package de.freese.mediathek.services.themoviedb.impl;

import org.springframework.web.client.RestClient;

import de.freese.mediathek.services.themoviedb.api.AccountService;
import de.freese.mediathek.services.themoviedb.model.Configuration;

/**
 * Service für den allgemeinen Zugriff auf die API.<br>
 * <a href="http://docs.themoviedb.apiary.io/#configuration" target="_blank">http://docs.themoviedb.apiary.io/#configuration</a>
 *
 * @author Thomas Freese
 * @since 26.04.2014
 */
public class DefaultAccountService extends AbstractMovieDbService implements AccountService {
    public DefaultAccountService(final RestClient restClient, final String apiKey) {
        super(restClient, apiKey);
    }

    @Override
    public Configuration getConfiguration() {
        final StringBuilder url = url().append("configuration?api_key={api_key}");

        return getRestClient()
                .get()
                .uri(url.toString(), getApiKey())
                .retrieve()
                .toEntity(Configuration.class)
                .getBody();
    }
}
