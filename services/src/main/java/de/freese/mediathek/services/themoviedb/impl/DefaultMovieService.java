package de.freese.mediathek.services.themoviedb.impl;

import org.springframework.web.client.RestClient;

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
 * @since 26.04.2014
 */
public class DefaultMovieService extends AbstractMovieDbService implements MovieService {
    public DefaultMovieService(final RestClient restClient, final String apiKey) {
        super(restClient, apiKey);
    }

    @Override
    public Casts casts(final int id) {
        final Appendable url = url().append("movie/{movieID}/casts?api_key={api_key}&language=de");

        return getRestClient()
                .get()
                .uri(url.toString(), id, getApiKey())
                .retrieve()
                .toEntity(Casts.class)
                .getBody();
    }

    @Override
    public MovieDetails details(final int id) {
        final Appendable url = url().append("movie/{movieID}?api_key={api_key}&language=de");

        return getRestClient()
                .get()
                .uri(url.toString(), id, getApiKey())
                .retrieve()
                .toEntity(MovieDetails.class)
                .getBody();
    }

    @Override
    public Images images(final int id) {
        final Appendable url = url().append("movie/{movieID}/images?api_key={api_key}"); // &language=de

        return getRestClient()
                .get()
                .uri(url.toString(), id, getApiKey())
                .retrieve()
                .toEntity(Images.class)
                .getBody();
    }

    @Override
    public Search search(final String movie) {
        final Appendable url = url().append("search/movie?api_key={api_key}&language=de&query={query}");

        return getRestClient()
                .get()
                .uri(url.toString(), getApiKey(), urlEncode(movie))
                .retrieve()
                .toEntity(Search.class)
                .getBody();
    }

    @Override
    public Search search(final String movie, final int year) {
        final Appendable url = url().append("search/movie?api_key={api_key}&language=de&query={query}&year={year}");

        return getRestClient()
                .get()
                .uri(url.toString(), getApiKey(), urlEncode(movie), year)
                .retrieve()
                .toEntity(Search.class)
                .getBody();
    }
}
