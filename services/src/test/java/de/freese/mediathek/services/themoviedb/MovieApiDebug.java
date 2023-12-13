// Created: 26.04.2014
package de.freese.mediathek.services.themoviedb;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import de.freese.mediathek.services.Settings;

/**
 * API-Debug für <a href="http://www.thetvdb.com">thetvdb</a>.
 *
 * @author Thomas Freese
 */
public class MovieApiDebug {
    public static final String TEST_MOVIE = Settings.TEST_MOVIE;

    public static final int TEST_MOVIE_ID = Settings.TEST_MOVIE_ID;

    public static void main(final String[] args) throws Exception {
        final MovieApiDebug debug = new MovieApiDebug();
        debug.testSearch();
        // debug.testDetails();
        // debug.testImages();
        // debug.testActors();
    }

    public MovieApiDebug() {
        super();
    }

    // @Test
    public void testActors() throws Exception {
        final RestTemplate template = new RestTemplate();
        final String result = template.getForObject("https://api.themoviedb.org/3/movie/{movieID}/credits?api_key={api_key}&language=de", String.class, TEST_MOVIE_ID, getApiKey());

        prettyPrint(result);
    }

    // @Test
    public void testConfiguration() throws Exception {
        final RestTemplate template = new RestTemplate();
        final String result = template.getForObject("https://api.themoviedb.org/3/configuration?api_key={api_key}", String.class, getApiKey());

        // ObjectMapper mapper = new ObjectMapper();
        // Object json = mapper.readValue(result, Object.class);
        // Configuration configuration = mapper.readValue(result, Configuration.class);

        prettyPrint(result);
    }

    // @Test
    public void testDetails() throws Exception {
        final RestTemplate template = new RestTemplate();
        final String result = template.getForObject("https://api.themoviedb.org/3/movie/{movieID}?api_key={api_key}&language=de", String.class, TEST_MOVIE_ID, getApiKey());

        prettyPrint(result);
    }

    // @Test
    public void testHTTP() throws Exception {
        final URI uri = URI.create(String.format("https://api.themoviedb.org/3/search/movie?api_key=%s&language=de&query=%s", getApiKey(), TEST_MOVIE));
        final HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Operation failed: " + connection.getResponseCode());
        }

        System.out.println("Content-Type = " + connection.getContentType());
        System.out.println("Location: " + connection.getHeaderField("Location"));

        final ObjectMapper mapper = new ObjectMapper();

        try (InputStream inputStream = connection.getInputStream()) {
            final Object json = mapper.readValue(inputStream, Object.class);
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
        }

        // try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())))
        // {
        // String line = reader.readLine();
        //
        // while (line != null)
        // {
        // LOGGER.info(line);
        // line = reader.readLine();
        // }
        // }

        connection.disconnect();
    }

    // @Test
    public void testImages() throws Exception {
        final RestTemplate template = new RestTemplate();
        final String result = template.getForObject("https://api.themoviedb.org/3/movie/{movieID}/images?api_key={api_key}", String.class, TEST_MOVIE_ID, getApiKey());

        prettyPrint(result);
    }

    // @Test
    public void testSearch() throws Exception {
        final RestTemplate template = new RestTemplate();
        final String result = template.getForObject("https://api.themoviedb.org/3/search/movie?api_key={api_key}&language={lang}&query={query}", String.class, getApiKey(), getLocale().getLanguage(), TEST_MOVIE);
        // String result =
        // template.getForObject("https://api.themoviedb.org/3/search/tv?api_key={api_key}&language={lang}&query={query}", String.class, getApiKey(),
        // getLocale().getLanguage(), "stargate"); // 4629
        // String result =
        // template.getForObject("https://api.themoviedb.org/3/tv/{id}?api_key={api_key}&language={lang}", String.class, "4629", getApiKey(), getLocale()
        // .getLanguage());
        // String result =
        // template.getForObject("https://api.themoviedb.org/3/tv/{id}/season/1?api_key={api_key}&language={lang}", String.class, "4629", getApiKey(),
        // getLocale().getLanguage());
        // String result =
        // template.getForObject("https://api.themoviedb.org/3/movie/{id}/keywords?api_key={api_key}", String.class, TEST_MOVIE_ID, getApiKey(),
        // getLocale().getLanguage());

        prettyPrint(result);
    }

    private String getApiKey() {
        return Settings.getMovieDbApiKey();
    }

    private Locale getLocale() {
        return Locale.GERMANY;
    }

    private void prettyPrint(final String result) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final Object json = mapper.readValue(result, Object.class);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
    }
}
