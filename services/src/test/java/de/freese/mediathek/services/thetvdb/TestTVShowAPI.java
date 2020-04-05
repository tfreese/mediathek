/**
 * Created: 24.04.2014
 */
package de.freese.mediathek.services.thetvdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import de.freese.mediathek.services.Settings;

/**
 * @author Thomas Freese
 */
public class TestTVShowAPI
{
    /**
    *
    */
    private static TVService service = null;

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    public static void beforeClass() throws Exception
    {
        service = new TVService(Settings.getTvDbApiKey());
        service.afterPropertiesSet();
    }

    /**
     * Erstellt ein neues {@link TestTVShowAPI} Object.
     */
    public TestTVShowAPI()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @EnabledIfSystemProperty(named = Settings.PROPERTY_TV_DB_API_KEY, matches = ".*")
    public void testDetails() throws Exception
    {
        TVShow show = service.getDetails(TVShowAPIDebug.TEST_SHOW_ID);

        assertNotNull(show);

        assertNotNull(show.getActors());
        assertTrue(show.getActors().startsWith("|Richard Dean Anderson|Michael Shanks|"));
        assertNotNull(show.getBanner());
        assertNotNull(show.getBeschreibung());
        assertTrue(show.getBeschreibung().startsWith("Die Fortsetzung des Films \"Stargate\" von 1994,"));
        assertNotNull(show.getFanart());
        assertNotNull(show.getGenres());
        assertEquals(show.getGenres(), "|Action|Adventure|Fantasy|Science-Fiction|");
        assertNotNull(show.getID());
        assertEquals(show.getID(), "72449");
        assertNotNull(show.getLanguage());
        assertEquals(show.getLanguage(), "de");
        assertNotNull(show.getPoster());
        assertNotNull(show.getReleaseDate());
        assertEquals(show.getReleaseDate(), "1997-07-01");
        assertNotNull(show.getTitle());
        assertEquals(show.getTitle(), "Stargate SG-1");
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @EnabledIfSystemProperty(named = Settings.PROPERTY_TV_DB_API_KEY, matches = ".*")
    public void testDetailsAll() throws Exception
    {
        TVShow show = service.getDetailsAll(TVShowAPIDebug.TEST_SHOW_ID);

        assertNotNull(show);

        assertNotNull(show.getActors());
        assertTrue(show.getActors().startsWith("|Richard Dean Anderson|"));
        assertNotNull(show.getBanner());
        assertNotNull(show.getBeschreibung());
        assertTrue(show.getBeschreibung().startsWith("Die Fortsetzung des Films \"Stargate\" von 1994,"));
        assertNotNull(show.getFanart());
        assertNotNull(show.getGenres());
        assertEquals(show.getGenres(), "|Action|Adventure|Fantasy|Science-Fiction|");
        assertNotNull(show.getID());
        assertEquals(show.getID(), "72449");
        assertNotNull(show.getLanguage());
        assertEquals(show.getLanguage(), "de");
        assertNotNull(show.getPoster());
        assertNotNull(show.getReleaseDate());
        assertEquals(show.getReleaseDate(), "1997-07-01");
        assertNotNull(show.getTitle());
        assertEquals(show.getTitle(), "Stargate SG-1");

        assertNotNull(show.getEpisodes());
        assertTrue(show.getEpisodes().size() == 222);

        assertNotNull(show.getActorsList());
        assertTrue(show.getActorsList().size() == 23);

        assertNotNull(show.getPosterList());
        assertTrue(show.getPosterList().size() > 1);

        assertNotNull(show.getFanartList());
        assertTrue(show.getFanartList().size() > 1);

        assertNotNull(show.getSeriesList());
        assertTrue(show.getSeriesList().size() > 1);

        assertNotNull(show.getSeasonList());
        assertTrue(show.getSeasonList().size() > 1);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @EnabledIfSystemProperty(named = Settings.PROPERTY_TV_DB_API_KEY, matches = ".*")
    public void testSearch() throws Exception
    {
        List<TVShow> result = service.search(TVShowAPIDebug.TEST_SHOW);

        assertNotNull(result);
        assertTrue(result.size() > 1);

        for (TVShow show : result)
        {
            assertNotNull(show.getBeschreibung());
            assertNotNull(show.getBanner());
            assertNotNull(show.getID());
            assertNotNull(show.getImdbID());
            assertNotNull(show.getJahr());
            assertNotNull(show.getLanguage());
            assertNotNull(show.getReleaseDate());
            assertNotNull(show.getTitle());
        }
    }
}
