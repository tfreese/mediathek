// Created: 24.04.2014
package de.freese.mediathek.services.thetvdb;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import de.freese.mediathek.services.AbstractService;
import de.freese.mediathek.services.themoviedb.model.Image;
import de.freese.mediathek.services.themoviedb.model.Images;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

/***
 * Implementierung für den Zugriff auf http:// www.thetvdb.com.<br>
 * http:// www.thetvdb.com/wiki/index.php?title=Programmers_API
 **
 * @author Thomas Freese
 */
public class TVService extends AbstractService
{
    /**
     *
     */
    private RestTemplate restTemplate;

    /**
     * Erstellt ein neues {@link TVService} Object.
     *
     * @param apiKey String
     */
    public TVService(final String apiKey)
    {
        super(apiKey);
    }

    /**
     * @see de.freese.mediathek.services.AbstractService#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        super.afterPropertiesSet();

        if (this.restTemplate == null)
        {
            List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
            messageConverters.add(new Jaxb2RootElementHttpMessageConverter());

            this.restTemplate = new RestTemplate(messageConverters);
            // this.restTemplate.getMessageConverters().add(new Jaxb2RootElementHttpMessageConverter());

        }
    }

    /**
     * Liefert Details zur Serie ohne die Episoden, Actors und Images.<br>
     *
     * @param id String
     *
     * @return {@link TVShow}
     */
    public TVShow getDetails(final String id)
    {
        // http://thetvdb.com//api/1D62F2F90030C444/series/72449/de.xml
        StringBuilder url = url().append("{apikey}/series/{id}/{lang}.xml");

        Search search = getRestTemplate().getForObject(url.toString(), Search.class, getApiKey(), id, getLocale().getLanguage());

        if (CollectionUtils.isEmpty(search.getSeries()))
        {
            return null;
        }

        return search.getSeries().get(0);
    }

    /**
     * Liefert alle Details zur Serie mit Episoden, Actors und Images.<br>
     *
     * @param id String
     *
     * @return {@link TVShow}
     */
    public TVShow getDetailsAll(final String id)
    {
        // http://thetvdb.com//api/1D62F2F90030C444/series/72449/all/de.xml
        // http://thetvdb.com/api/1D62F2F90030C444/series/72449/actors.xml
        // http://thetvdb.com/api/1D62F2F90030C444/series/72449/banners.xml
        // http://thetvdb.com/banners/episodes/72449/85751.jpg

        // Serie mit Episoden
        StringBuilder url = url().append("{apikey}/series/{id}/all/{lang}.xml");

        Search search = getRestTemplate().getForObject(url.toString(), Search.class, getApiKey(), id, getLocale().getLanguage());

        if (CollectionUtils.isEmpty(search.getSeries()))
        {
            return null;
        }

        TVShow show = search.getSeries().get(0);

        List<Episode> episodes = search.getEpisodes();
        Collections.sort(episodes);
        show.setEpisodes(episodes);

        // Actors
        url = url().append("{apikey}/series/{id}/actors.xml");
        Actors actors = getRestTemplate().getForObject(url.toString(), Actors.class, getApiKey(), id);
        List<Actor> actorsList = actors.getActors();
        Collections.sort(actorsList);
        show.setActorsList(actorsList);

        StringBuilder sb = new StringBuilder("|");

        for (Actor actor : actorsList)
        {
            sb.append(actor.getName()).append("|");
        }

        show.setActors(sb.toString());

        // Banner
        // BannerType: poster, fanart, series or season
        // BannerType2: graphical, text or blank
        url = url().append("{apikey}/series/{id}/banners.xml");
        Images images = getRestTemplate().getForObject(url.toString(), Images.class, getApiKey(), id);
        // List<Image> banners = images.getBanners();
        List<Image> poster = images.getPosters();
        List<Image> fanart = new ArrayList<>();
        List<Image> series = images.getBackdrops();
        List<Image> season = new ArrayList<>();

        // // Alles ausser de und en entfernen.
        // for (Iterator<Image> iterator = banners.iterator(); iterator.hasNext();)
        // {
        // Image image = iterator.next();
        //
        // if (!"de".equals(image.getLanguage()) && !"en".equals(image.getLanguage()))
        // {
        // iterator.remove();
        // }
        //
        // if ("poster".equals(image.getType()))
        // {
        // poster.add(image);
        // }
        // else if ("fanart".equals(image.getType()))
        // {
        // fanart.add(image);
        // }
        // else if ("series".equals(image.getType()))
        // {
        // series.add(image);
        // }
        // else if ("season".equals(image.getType()))
        // {
        // season.add(image);
        // }
        // }

        Collections.sort(poster);
        Collections.sort(fanart);
        Collections.sort(series);
        Collections.sort(season);

        show.setPosterList(poster);
        show.setFanartList(fanart);
        show.setSeriesList(series);
        show.setSeasonList(season);

        return show;
    }

    /**
     * Liefert das Bild
     *
     * @param path String
     *
     * @return {@link BufferedImage}
     *
     * @throws Exception Falls was schief geht.
     */
    public BufferedImage getImage(final String path) throws Exception
    {
        if (path == null || path.isBlank())
        {
            return null;
        }

        String url = String.format("http://thetvdb.com/banners/%s", path);
        BufferedImage image = null;

        try (InputStream inputStream = getCache().getResource(URI.create(url)).get())
        {
            image = ImageIO.read(inputStream);
        }

        return image;
    }

    /**
     * Suche nach Name.
     *
     * @param name String
     *
     * @return {@link List}
     */
    @SuppressWarnings(
            {
                    "unchecked", "rawtypes"
            })
    public List<TVShow> search(final String name)
    {
        // http://thetvdb.com/api/GetSeries.php?seriesname=stargate&language=de
        StringBuilder url = url().append("GetSeries.php?seriesname={name}&language={lang}");

        Search search = getRestTemplate().getForObject(url.toString(), Search.class, urlEncode(name), getLocale().getLanguage());

        if (CollectionUtils.isEmpty(search.getSeries()))
        {
            return null;
        }

        // Reduntante Treffer für Sprache filtern.
        Map<String, TVShow> map = new HashMap<>();
        Map<String, TVShow> map2 = new HashMap<>();

        for (TVShow show : search.getSeries())
        {
            if (show.getLanguage().equals(getLocale().getLanguage()))
            {
                map.put(show.getID(), show);
            }
            else
            {
                map2.put(show.getID(), show);
            }
        }

        for (Entry<String, TVShow> entry : map2.entrySet())
        {
            if (map.containsKey(entry.getKey()))
            {
                continue;
            }

            map.put(entry.getKey(), entry.getValue());
        }

        List<TVShow> result = new ArrayList(map.values());
        Collections.sort(result);

        map.clear();
        map = null;
        map2.clear();
        map2 = null;

        return result;
    }

    /**
     * @param restTemplate {@link RestTemplate}
     */
    public void setRestTemplate(final RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    /**
     * @return {@link RestTemplate}
     */
    private RestTemplate getRestTemplate()
    {
        return this.restTemplate;
    }

    /**
     * Liefert 'http://thetvdb.com/api/'.<br>
     * http://thetvdb.com/api/1D62F2F90030C444/mirrors.xml<br>
     *
     * @return {@link StringBuilder}
     */
    private StringBuilder url()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("http://thetvdb.com/api/");

        return sb;
    }
}
