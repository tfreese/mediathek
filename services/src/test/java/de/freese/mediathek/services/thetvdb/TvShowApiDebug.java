// Created: 26.04.2014
package de.freese.mediathek.services.thetvdb;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.freese.mediathek.services.Settings;
import org.springframework.web.client.RestTemplate;

/**
 * API-Debug für <a href="http://www.thetvdb.com">...</a>.
 *
 * @author Thomas Freese
 */
public class TvShowApiDebug
{
    public static final String TEST_SHOW = Settings.TEST_SHOW;
    /**
     * imdb_id: tt0374455; SG-1
     */
    public static final String TEST_SHOW_ID = Settings.TEST_SHOW_ID;

    public static void main(final String[] args) throws Exception
    {
        TvShowApiDebug debug = new TvShowApiDebug();
        // debug.testSearch();
        // debug.testDetails();
        // debug.testDetailsAll();
        // debug.testActors();
        debug.testImages();
    }

    public TvShowApiDebug()
    {
        super();
    }

    // @Test
    public void testActors() throws Exception
    {
        RestTemplate template = new RestTemplate();

        String result = template.getForObject("http://thetvdb.com/api/{apiKey}/series/{id}/actors.xml", String.class, getApiKey(), TEST_SHOW_ID,
                getLocale().getLanguage());

        prettyPrint(result);
    }

    // @Test
    public void testDetails() throws Exception
    {
        RestTemplate template = new RestTemplate();

        String result = template.getForObject("http://thetvdb.com/api/{apiKey}/series/{id}/{lang}.xml", String.class, getApiKey(), TEST_SHOW_ID,
                getLocale().getLanguage());

        prettyPrint(result);
    }

    // @Test
    public void testDetailsAll() throws Exception
    {
        RestTemplate template = new RestTemplate();

        String result = template.getForObject("http://thetvdb.com/api/{apiKey}/series/{id}/all/{lang}.xml", String.class, getApiKey(), TEST_SHOW_ID,
                getLocale().getLanguage());

        prettyPrint(result);
    }

    // @Test
    public void testHTTP() throws Exception
    {
        URL url = new URL(String.format("http://thetvdb.com/api/GetSeries.php?seriesname=%s&language=%s", TEST_SHOW, "de"));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
        {
            throw new RuntimeException("Operation failed: " + connection.getResponseCode());
        }

        System.out.println("Content-Type = " + connection.getContentType());
        System.out.println("Location: " + connection.getHeaderField("Location"));

        ObjectMapper mapper = new XmlMapper();

        try (InputStream inputStream = connection.getInputStream())
        {
            Object xml = mapper.readValue(inputStream, Object.class);
            // System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(xml));
            prettyPrint(xml.toString());

            // try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
            // {
            // String line = reader.readLine();
            //
            // while (line != null)
            // {
            // LOGGER.info(line);
            // line = reader.readLine();
            // }
            // }
        }

        connection.disconnect();
    }

    // @Test
    public void testImages() throws Exception
    {
        RestTemplate template = new RestTemplate();

        String result = template.getForObject("http://thetvdb.com/api/{apiKey}/series/{id}/banners.xml", String.class, getApiKey(), TEST_SHOW_ID,
                getLocale().getLanguage());

        prettyPrint(result);
    }

    // @Test
    public void testSearch() throws Exception
    {
        RestTemplate template = new RestTemplate();

        String result = template.getForObject("http://thetvdb.com/api/GetSeries.php?seriesname={name}&language={lang}", String.class, TEST_SHOW,
                getLocale().getLanguage());

        prettyPrint(result);
    }

    private String getApiKey()
    {
        return Settings.getTvDbApiKey();
    }

    private Locale getLocale()
    {
        return Locale.GERMANY;
    }

    private void prettyPrint(final String result) throws Exception
    {
        // ObjectMapper mapper = new XmlMapper();
        // mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // System.out.println(mapper.writeValueAsString(result));
        //
        // XmlMapper mapper = new XmlMapper();
        // // // Object xml = mapper.readValue(result, Object.class);
        // // System.out.println(mapper.writeValueAsString(result));
        // System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
        System.out.println(result);
    }
}
