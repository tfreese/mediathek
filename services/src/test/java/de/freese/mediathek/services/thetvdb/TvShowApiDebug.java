package de.freese.mediathek.services.thetvdb;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Locale;
import java.util.Objects;

import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.xml.XmlMapper;

import de.freese.mediathek.services.Settings;

/**
 * API-Debug für <a href="http://www.thetvdb.com">...</a>.
 *
 * @author Thomas Freese
 * @since 26.04.2014
 */
public class TvShowApiDebug {
    public static final String TEST_SHOW = Settings.TEST_SHOW;
    /**
     * imdb_id: tt0374455; SG-1
     */
    public static final String TEST_SHOW_ID = Settings.TEST_SHOW_ID;

    static void main() throws Exception {
        final RestClient restClient = RestClient.builder().build();

        final TvShowApiDebug debug = new TvShowApiDebug(restClient);
        // debug.testSearch();
        // debug.testDetails();
        // debug.testDetailsAll();
        // debug.testActors();
        debug.testImages();
    }

    private final JsonMapper jsonMapper;
    private final RestClient restClient;

    public TvShowApiDebug(final RestClient restClient) {
        super();

        this.restClient = Objects.requireNonNull(restClient, "restClient required");
        this.jsonMapper = JsonMapper.builder().build();
    }

    // @Test
    public void testActors() {
        final String result = restClient
                .get()
                .uri("http://thetvdb.com/api/{apiKey}/series/{id}/actors.xml", getApiKey(), TEST_SHOW_ID, getLocale().getLanguage())
                .retrieve()
                .toEntity(String.class)
                .getBody();

        prettyPrint(result);
    }

    // @Test
    public void testDetails() {
        final String result = restClient
                .get()
                .uri("http://thetvdb.com/api/{apiKey}/series/{id}/{lang}.xml", getApiKey(), TEST_SHOW_ID, getLocale().getLanguage())
                .retrieve()
                .toEntity(String.class)
                .getBody();

        prettyPrint(result);
    }

    // @Test
    public void testDetailsAll() {
        final String result = restClient
                .get()
                .uri("http://thetvdb.com/api/{apiKey}/series/{id}/all/{lang}.xml", getApiKey(), TEST_SHOW_ID, getLocale().getLanguage())
                .retrieve()
                .toEntity(String.class)
                .getBody();

        prettyPrint(result);
    }

    // @Test
    public void testHTTP() throws Exception {
        final URI uri = URI.create(String.format("http://thetvdb.com/api/GetSeries.php?seriesname=%s&language=%s", TEST_SHOW, "de"));
        final HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Operation failed: " + connection.getResponseCode());
        }

        System.out.println("Content-Type = " + connection.getContentType());
        System.out.println("Location: " + connection.getHeaderField("Location"));

        final XmlMapper xmlMapper = XmlMapper.builder().build();

        try (InputStream inputStream = connection.getInputStream()) {
            final Object xml = xmlMapper.readValue(inputStream, Object.class);
            // System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(xml));
            prettyPrint(xml.toString());

            // try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            // String line = reader.readLine();
            //
            // while (line != null) {
            // LOGGER.info(line);
            // line = reader.readLine();
            // }
            // }
        }

        connection.disconnect();
    }

    // @Test
    public void testImages() {
        final String result = restClient
                .get()
                .uri("http://thetvdb.com/api/{apiKey}/series/{id}/banners.xml", getApiKey(), TEST_SHOW_ID, getLocale().getLanguage())
                .retrieve()
                .toEntity(String.class)
                .getBody();

        prettyPrint(result);
    }

    // @Test
    public void testSearch() {
        final String result = restClient
                .get()
                .uri("http://thetvdb.com/api/GetSeries.php?seriesname={name}&language={lang}", TEST_SHOW, getLocale().getLanguage())
                .retrieve()
                .toEntity(String.class)
                .getBody();

        prettyPrint(result);
    }

    private String getApiKey() {
        return Settings.getTvDbApiKey();
    }

    private Locale getLocale() {
        return Locale.GERMANY;
    }

    private void prettyPrint(final String result) {
        final Object json = jsonMapper.readValue(result, Object.class);

        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
    }
}
