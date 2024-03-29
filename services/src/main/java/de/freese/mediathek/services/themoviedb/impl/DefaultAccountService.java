// Created: 26.04.2014
package de.freese.mediathek.services.themoviedb.impl;

import de.freese.mediathek.services.themoviedb.api.AccountService;
import de.freese.mediathek.services.themoviedb.model.Configuration;

/**
 * Service für den allgemeinen Zugriff auf die API.<br>
 * <a href="http://docs.themoviedb.apiary.io/#configuration" target="_blank">http://docs.themoviedb.apiary.io/#configuration</a>
 *
 * @author Thomas Freese
 */
public class DefaultAccountService extends AbstractMovieDbService implements AccountService {
    public DefaultAccountService(final String apiKey) {
        super(apiKey);
    }

    @Override
    public Configuration getConfiguration() {
        final StringBuilder url = url().append("configuration?api_key={api_key}");

        return getRestTemplate().getForObject(url.toString(), Configuration.class, getApiKey());
    }
}
