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
public class DefaultAccountService extends AbstractMovieDBService implements AccountService
{
    /**
     * Erstellt ein neues {@link DefaultAccountService} Object.
     *
     * @param apiKey String
     */
    public DefaultAccountService(final String apiKey)
    {
        super(apiKey);
    }

    /**
     * @see de.freese.mediathek.services.themoviedb.api.AccountService#getConfiguration()
     */
    @Override
    public Configuration getConfiguration()
    {
        StringBuilder url = url().append("configuration?api_key={api_key}");

        Configuration configuration = getRestTemplate().getForObject(url.toString(), Configuration.class, getApiKey());

        return configuration;
    }
}
