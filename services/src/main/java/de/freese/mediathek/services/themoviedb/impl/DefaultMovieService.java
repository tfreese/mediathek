// Created: 26.04.2014
package de.freese.mediathek.services.themoviedb.impl;

import de.freese.mediathek.services.themoviedb.api.MovieService;
import de.freese.mediathek.services.themoviedb.model.Casts;
import de.freese.mediathek.services.themoviedb.model.Images;
import de.freese.mediathek.services.themoviedb.model.MovieDetails;
import de.freese.mediathek.services.themoviedb.model.Search;

/**
 * Service für den Zugriff auf die Movie-API.<br>
 * <a href="http://docs.themoviedb.apiary.io/#movies" target="_blank">http://docs.themoviedb.apiary.io/#movies</a>
 *
 * @author Thomas Freese
 */
public class DefaultMovieService extends AbstractMovieDbService implements MovieService {
    public DefaultMovieService(final String apiKey) {
        super(apiKey);
    }

    @Override
    public Casts casts(final int id) {
        final Appendable url = url().append("movie/{movieID}/casts?api_key={api_key}&language=de");

        return getRestTemplate().getForObject(url.toString(), Casts.class, id, getApiKey());
    }

    @Override
    public MovieDetails details(final int id) {
        final Appendable url = url().append("movie/{movieID}?api_key={api_key}&language=de");

        return getRestTemplate().getForObject(url.toString(), MovieDetails.class, id, getApiKey());
    }

    @Override
    public Images images(final int id) {
        final Appendable url = url().append("movie/{movieID}/images?api_key={api_key}"); // &language=de

        return getRestTemplate().getForObject(url.toString(), Images.class, id, getApiKey());
    }

    @Override
    public Search search(final String movie) {
        final Appendable url = url().append("search/movie?api_key={api_key}&language=de&query={query}");

        return getRestTemplate().getForObject(url.toString(), Search.class, getApiKey(), urlEncode(movie));
    }

    @Override
    public Search search(final String movie, final int year) {
        final Appendable url = url().append("search/movie?api_key={api_key}&language=de&query={query}&year={year}");

        return getRestTemplate().getForObject(url.toString(), Search.class, getApiKey(), urlEncode(movie), year);
    }
}
