/**
 * Created: 26.04.2014
 */

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
public class DefaultMovieService extends AbstractMovieDBService implements MovieService
{
    /**
     * Erstellt ein neues {@link DefaultMovieService} Object.
     *
     * @param apiKey String
     */
    public DefaultMovieService(final String apiKey)
    {
        super(apiKey);
    }

    /**
     * @see de.freese.mediathek.services.themoviedb.api.MovieService#casts(int)
     */
    @Override
    public Casts casts(final int id)
    {
        Appendable url = url().append("movie/{movieID}/casts?api_key={api_key}&language=de");

        Casts casts = getRestTemplate().getForObject(url.toString(), Casts.class, id, getApiKey());

        return casts;
    }

    /**
     * @see de.freese.mediathek.services.themoviedb.api.MovieService#details(int)
     */
    @Override
    public MovieDetails details(final int id)
    {
        Appendable url = url().append("movie/{movieID}?api_key={api_key}&language=de");

        MovieDetails details = getRestTemplate().getForObject(url.toString(), MovieDetails.class, id, getApiKey());

        return details;
    }

    /**
     * @see de.freese.mediathek.services.themoviedb.api.MovieService#images(int)
     */
    @Override
    public Images images(final int id)
    {
        Appendable url = url().append("movie/{movieID}/images?api_key={api_key}"); // &language=de

        Images images = getRestTemplate().getForObject(url.toString(), Images.class, id, getApiKey());

        return images;
    }

    /**
     * @see de.freese.mediathek.services.themoviedb.api.MovieService#search(java.lang.String)
     */
    @Override
    public Search search(final String movie)
    {
        Appendable url = url().append("search/movie?api_key={api_key}&language=de&query={query}");

        Search search = getRestTemplate().getForObject(url.toString(), Search.class, getApiKey(), urlEncode(movie));

        return search;
    }

    /**
     * @see de.freese.mediathek.services.themoviedb.api.MovieService#search(java.lang.String, int)
     */
    @Override
    public Search search(final String movie, final int year)
    {
        Appendable url = url().append("search/movie?api_key={api_key}&language=de&query={query}&year={year}");

        Search search = getRestTemplate().getForObject(url.toString(), Search.class, getApiKey(), urlEncode(movie), year);

        return search;
    }
}
