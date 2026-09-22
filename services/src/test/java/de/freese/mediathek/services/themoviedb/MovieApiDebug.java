package de.freese.mediathek.services.themoviedb;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Locale;
import java.util.Objects;

import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import de.freese.mediathek.services.Settings;

/**
 * API-Debug für <a href="http://www.thetvdb.com">thetvdb</a>.
 *
 * @author Thomas Freese
 * @since 26.04.2014
 */
public class MovieApiDebug {
    public static final String TEST_MOVIE = Settings.TEST_MOVIE;
    public static final int TEST_MOVIE_ID = Settings.TEST_MOVIE_ID;

    static void main() throws Exception {
        final RestClient restClient = RestClient.builder().build();

        final MovieApiDebug debug = new MovieApiDebug(restClient);
        debug.testSearch();
        // debug.testDetails();
        // debug.testImages();
        // debug.testActors();
    }

    private final JsonMapper jsonMapper;
    private final RestClient restClient;

    public MovieApiDebug(final RestClient restClient) {
        super();

        this.restClient = Objects.requireNonNull(restClient, "restClient required");
        this.jsonMapper = JsonMapper.builder().build();
    }

    // @Test
    public void testActors() {
        final String result = restClient
                .get()
                .uri("https://api.themoviedb.org/3/movie/{movieID}/credits?api_key={api_key}&language=de", TEST_MOVIE_ID, getApiKey())
                .retrieve()
                .toEntity(String.class)
                .getBody();

        prettyPrint(result);
    }

    // @Test
    public void testConfiguration() {
        final String result = restClient
                .get()
                .uri("https://api.themoviedb.org/3/configuration?api_key={api_key}", getApiKey())
                .retrieve()
                .toEntity(String.class)
                .getBody();

        // ObjectMapper mapper = new ObjectMapper();
        // Object json = mapper.readValue(result, Object.class);
        // Configuration configuration = mapper.readValue(result, Configuration.class);

        prettyPrint(result);
    }

    // @Test
    public void testDetails() {
        final String result = restClient
                .get()
                .uri("https://api.themoviedb.org/3/movie/{movieID}?api_key={api_key}&language=de", TEST_MOVIE_ID, getApiKey())
                .retrieve()
                .toEntity(String.class)
                .getBody();

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

        final JsonMapper jsonMapper = JsonMapper.builder().build();

        try (InputStream inputStream = connection.getInputStream()) {
            final Object json = jsonMapper.readValue(inputStream, Object.class);
            System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
        }

        // try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        // String line = reader.readLine();
        //
        // while (line != null) {
        // LOGGER.info(line);
        // line = reader.readLine();
        // }
        // }

        connection.disconnect();
    }

    // @Test
    public void testImages() {
        final String result = restClient
                .get()
                .uri("https://api.themoviedb.org/3/movie/{movieID}/images?api_key={api_key}", TEST_MOVIE_ID, getApiKey())
                .retrieve()
                .toEntity(String.class)
                .getBody();

        prettyPrint(result);
    }

    // @Test
    public void testSearch() {
        final String result = restClient
                .get()
                .uri("https://api.themoviedb.org/3/search/movie?api_key={api_key}&language={lang}&query={query}", getApiKey(), getLocale().getLanguage(), TEST_MOVIE)
                .retrieve()
                .toEntity(String.class)
                .getBody();

        prettyPrint(result);
    }

    private String getApiKey() {
        return Settings.getMovieDbApiKey();
    }

    private Locale getLocale() {
        return Locale.GERMANY;
    }

    private void prettyPrint(final String result) {
        final Object json = jsonMapper.readValue(result, Object.class);

        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
    }
}
