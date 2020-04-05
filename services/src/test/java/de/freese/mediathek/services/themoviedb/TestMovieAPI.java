/**
 * Created: 24.04.2014
 */
package de.freese.mediathek.services.themoviedb;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class TestMovieAPI
{
    /**
     *
     */
    private static AccountService accountService = null;

    /**
     *
     */
    private static MovieService movieService = null;

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    public static void beforeAll() throws Exception
    {
        movieService = new DefaultMovieService(Settings.getMovieDbApiKey());
        ((DefaultMovieService) movieService).afterPropertiesSet();

        accountService = new DefaultAccountService(Settings.getMovieDbApiKey());
        ((DefaultAccountService) accountService).afterPropertiesSet();
    }

    /**
     * Erstellt ein neues {@link TestMovieAPI} Object.
     */
    public TestMovieAPI()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @EnabledIfSystemProperty(named = Settings.PROPERTY_MOVIE_DB_API_KEY, matches = ".*")
    public void testCasts() throws Exception
    {
        Casts casts = movieService.casts(Settings.TEST_MOVIE_ID);

        assertNotNull(casts);
        assertNotNull(casts.getCast());
        assertTrue(casts.getCast().size() > 1);
        assertNotNull(casts.getCrew());
        assertTrue(casts.getCrew().size() > 1);

        List<Crew> directors = casts.getDirectors();
        assertNotNull(directors);
        assertTrue(directors.size() >= 1);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @EnabledIfSystemProperty(named = Settings.PROPERTY_MOVIE_DB_API_KEY, matches = ".*")
    public void testConfiguration() throws Exception
    {
        Configuration configuration = accountService.getConfiguration();

        assertNotNull(configuration);
        assertNotNull(configuration.getImageBaseURL());
        // assertNotNull(configuration.getChangeKeys());
        // assertTrue(configuration.getChangeKeys().size() > 0);
        assertNotNull(configuration.getBackdropSizes());
        assertTrue(configuration.getBackdropSizes().size() >= 1);
        assertNotNull(configuration.getPosterSizes());
        assertTrue(configuration.getPosterSizes().size() >= 1);
        assertNotNull(configuration.getLogoSizes());
        assertTrue(configuration.getLogoSizes().size() >= 1);
        assertNotNull(configuration.getProfileSizes());
        assertTrue(configuration.getProfileSizes().size() >= 1);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @EnabledIfSystemProperty(named = Settings.PROPERTY_MOVIE_DB_API_KEY, matches = ".*")
    public void testDetails() throws Exception
    {
        MovieDetails details = movieService.details(Settings.TEST_MOVIE_ID);

        assertNotNull(details);
        assertNotNull(details.getId());
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
        assertTrue(details.getGenres().size() >= 1);
        assertNotNull(details.getStudios());
        assertTrue(details.getStudios().size() >= 1);
        assertNotNull(details.getLanguages());
        assertTrue(details.getLanguages().size() >= 1);
        assertNotNull(details.getCountries());
        assertTrue(details.getCountries().size() >= 1);
    }

    // /**
    // * @throws Exception Falls was schief geht.
    // */
    // @Test
    // public void testPoster() throws Exception
    // {
    // List<Image> poster = movieService.getPoster(Configuration.TEST_MOVIE_ID);
    //
    // assertNotNull(poster);
    // assertTrue(poster.size() >= 1);
    // }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @EnabledIfSystemProperty(named = Settings.PROPERTY_MOVIE_DB_API_KEY, matches = ".*")
    public void testImages() throws Exception
    {
        Images images = movieService.images(Settings.TEST_MOVIE_ID);

        assertNotNull(images);
        assertNotNull(images.getBackdrops());
        assertTrue(images.getBackdrops().size() >= 1);
        assertNotNull(images.getPosters());
        assertTrue(images.getPosters().size() >= 1);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @EnabledIfSystemProperty(named = Settings.PROPERTY_MOVIE_DB_API_KEY, matches = ".*")
    public void testSearch() throws Exception
    {
        Search result = movieService.search(Settings.TEST_MOVIE);

        assertNotNull(result.getResults());
        assertTrue(result.getResults().size() >= 1);

        for (Movie mov : result.getResults())
        {
            assertNotNull(mov.getId());
            assertNotNull(mov.getTitle());
            assertNotNull(mov.getOriginalTitle());
            // assertNotNull(mov.getReleaseDate());
            assertNotNull(mov.getPoster());

            // UrlResource resource = new UrlResource("https://image.tmdb.org/t/p/original" + mov.getPoster());
            // IOUtils.copy(resource.getInputStream(), new FileOutputStream("/tmp/" + mov.getPoster()));
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @EnabledIfSystemProperty(named = Settings.PROPERTY_MOVIE_DB_API_KEY, matches = ".*")
    public void testSearchYear() throws Exception
    {
        Search result = movieService.search(Settings.TEST_MOVIE, Settings.TEST_MOVIE_YEAR);

        assertNotNull(result.getResults());
        assertEquals(2, result.getResults().size());

        for (Movie mov : result.getResults())
        {
            assertNotNull(mov.getId());
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
