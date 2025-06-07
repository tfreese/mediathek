// Created: 24.04.2014
package de.freese.mediathek.services.themoviedb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import de.freese.mediathek.services.Settings;
import de.freese.mediathek.services.themoviedb.api.AccountService;
import de.freese.mediathek.services.themoviedb.api.MovieService;
import de.freese.mediathek.services.themoviedb.impl.DefaultAccountService;
import de.freese.mediathek.services.themoviedb.impl.DefaultMovieService;
import de.freese.mediathek.services.themoviedb.model.Casts;
import de.freese.mediathek.services.themoviedb.model.Configuration;
import de.freese.mediathek.services.themoviedb.model.Crew;
import de.freese.mediathek.services.themoviedb.model.Images;
import de.freese.mediathek.services.themoviedb.model.Movie;
import de.freese.mediathek.services.themoviedb.model.MovieDetails;
import de.freese.mediathek.services.themoviedb.model.Search;

/**
 * @author Thomas Freese
 */
class TestMovieApi {
    private static AccountService accountService;

    private static MovieService movieService;

    @BeforeAll
    static void beforeAll() {
        movieService = new DefaultMovieService(Settings.getMovieDbApiKey());
        ((DefaultMovieService) movieService).afterPropertiesSet();

        accountService = new DefaultAccountService(Settings.getMovieDbApiKey());
        ((DefaultAccountService) accountService).afterPropertiesSet();
    }

    @Test
    @EnabledIfSystemProperty(named = Settings.PROPERTY_MOVIE_DB_API_KEY, matches = ".*")
    void testCasts() {
        final Casts casts = movieService.casts(Settings.TEST_MOVIE_ID);

        assertNotNull(casts);
        assertNotNull(casts.getCast());
        assertTrue(casts.getCast().size() > 1);
        assertNotNull(casts.getCrew());
        assertTrue(casts.getCrew().size() > 1);

        final List<Crew> directors = casts.getDirectors();
        assertNotNull(directors);
        assertFalse(directors.isEmpty());
    }

    @Test
    @EnabledIfSystemProperty(named = Settings.PROPERTY_MOVIE_DB_API_KEY, matches = ".*")
    void testConfiguration() {
        final Configuration configuration = accountService.getConfiguration();

        assertNotNull(configuration);
        assertNotNull(configuration.getImageBaseURL());
        // assertNotNull(configuration.getChangeKeys());
        // assertTrue(configuration.getChangeKeys().size() > 0);
        assertNotNull(configuration.getBackdropSizes());
        assertFalse(configuration.getBackdropSizes().isEmpty());
        assertNotNull(configuration.getPosterSizes());
        assertFalse(configuration.getPosterSizes().isEmpty());
        assertNotNull(configuration.getLogoSizes());
        assertFalse(configuration.getLogoSizes().isEmpty());
        assertNotNull(configuration.getProfileSizes());
        assertFalse(configuration.getProfileSizes().isEmpty());
    }

    @Test
    @EnabledIfSystemProperty(named = Settings.PROPERTY_MOVIE_DB_API_KEY, matches = ".*")
    void testDetails() {
        final MovieDetails details = movieService.details(Settings.TEST_MOVIE_ID);

        assertNotNull(details);
        assertNotEquals(0, details.getId());
        assertNotNull(details.getTitle());
        assertNotNull(details.getOriginalTitle());
        assertNotNull(details.getReleaseDate());
        assertNotNull(details.getPoster());
        assertNotNull(details.getImdbID());
        assertNotNull(details.getTagline());
        assertTrue(details.getRuntime() > 0);
        assertTrue(details.getVoteAverage() > 0.0f);
        assertTrue(details.getVoteCount() > 0);
        assertNotNull(details.getGenres());
        assertFalse(details.getGenres().isEmpty());
        assertNotNull(details.getStudios());
        assertFalse(details.getStudios().isEmpty());
        assertNotNull(details.getLanguages());
        assertFalse(details.getLanguages().isEmpty());
        assertNotNull(details.getCountries());
        assertFalse(details.getCountries().isEmpty());
    }

    // @Test
    // void testPoster() throws Exception {
    // List<Image> poster = movieService.getPoster(Configuration.TEST_MOVIE_ID);
    //
    // assertNotNull(poster);
    // assertTrue(poster.size() >= 1);
    // }

    @Test
    @EnabledIfSystemProperty(named = Settings.PROPERTY_MOVIE_DB_API_KEY, matches = ".*")
    void testImages() {
        final Images images = movieService.images(Settings.TEST_MOVIE_ID);

        assertNotNull(images);
        assertNotNull(images.getBackdrops());
        assertFalse(images.getBackdrops().isEmpty());
        assertNotNull(images.getPosters());
        assertFalse(images.getPosters().isEmpty());
    }

    @Test
    @EnabledIfSystemProperty(named = Settings.PROPERTY_MOVIE_DB_API_KEY, matches = ".*")
    void testSearch() {
        final Search result = movieService.search(Settings.TEST_MOVIE);

        assertNotNull(result.getResults());
        assertFalse(result.getResults().isEmpty());

        for (Movie mov : result.getResults()) {
            assertNotNull(mov);
            assertNotEquals(0, mov.getId());
            assertNotNull(mov.getTitle());
            assertNotNull(mov.getOriginalTitle());
            // assertNotNull(mov.getReleaseDate());
            assertNotNull(mov.getPoster());

            // UrlResource resource = new UrlResource("https://image.tmdb.org/t/p/original" + mov.getPoster());
            // IOUtils.copy(resource.getInputStream(), new FileOutputStream("/tmp/" + mov.getPoster()));
        }
    }

    @Test
    @EnabledIfSystemProperty(named = Settings.PROPERTY_MOVIE_DB_API_KEY, matches = ".*")
    void testSearchYear() {
        final Search result = movieService.search(Settings.TEST_MOVIE, Settings.TEST_MOVIE_YEAR);

        assertNotNull(result.getResults());
        assertEquals(2, result.getResults().size());

        for (Movie mov : result.getResults()) {
            assertNotNull(mov);
            assertNotEquals(0, mov.getId());
            assertNotNull(mov.getTitle());
            assertNotNull(mov.getOriginalTitle());
            assertNotNull(mov.getReleaseDate());
            assertNotNull(mov.getPoster());

            // assertTrue(mov.getReleaseDate().startsWith("1981"));
            // UrlResource resource = new UrlResource("https://image.tmdb.org/t/p/original" + mov.getPoster());
            // IOUtils.copy(resource.getInputStream(), new FileOutputStream("/tmp/" + mov.getPoster()));
        }
    }
}
