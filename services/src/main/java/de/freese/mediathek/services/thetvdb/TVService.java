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

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import de.freese.mediathek.services.AbstractService;
import de.freese.mediathek.services.themoviedb.model.Image;
import de.freese.mediathek.services.themoviedb.model.Images;

/***
 * Implementierung für den Zugriff auf http:// www.thetvdb.com.<br>
 * http:// www.thetvdb.com/wiki/index.php?title=Programmers_API
 **
 * @author Thomas Freese
 */
public class TVService extends AbstractService {
    private RestTemplate restTemplate;

    public TVService(final String apiKey) {
        super(apiKey);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        if (restTemplate == null) {
            final List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
            messageConverters.add(new Jaxb2RootElementHttpMessageConverter());

            restTemplate = new RestTemplate(messageConverters);
            // restTemplate.getMessageConverters().add(new Jaxb2RootElementHttpMessageConverter());

        }
    }

    public TVShow getDetails(final String id) {
        // http://thetvdb.com//api/1D62F2F90030C444/series/72449/de.xml
        final StringBuilder url = url().append("{apikey}/series/{id}/{lang}.xml");

        final Search search = getRestTemplate().getForObject(url.toString(), Search.class, getApiKey(), id, getLocale().getLanguage());

        if (search == null || search.getSeries().isEmpty()) {
            return null;
        }

        return search.getSeries().getFirst();
    }

    public TVShow getDetailsAll(final String id) {
        // http://thetvdb.com//api/1D62F2F90030C444/series/72449/all/de.xml
        // http://thetvdb.com/api/1D62F2F90030C444/series/72449/actors.xml
        // http://thetvdb.com/api/1D62F2F90030C444/series/72449/banners.xml
        // http://thetvdb.com/banners/episodes/72449/85751.jpg

        // Serie mit Episoden
        StringBuilder url = url().append("{apikey}/series/{id}/all/{lang}.xml");

        final Search search = getRestTemplate().getForObject(url.toString(), Search.class, getApiKey(), id, getLocale().getLanguage());

        if (search == null || search.getSeries().isEmpty()) {
            return null;
        }

        final TVShow show = search.getSeries().getFirst();

        final List<Episode> episodes = search.getEpisodes();
        Collections.sort(episodes);
        show.setEpisodes(episodes);

        // Actors
        url = url().append("{apikey}/series/{id}/actors.xml");
        final Actors actors = getRestTemplate().getForObject(url.toString(), Actors.class, getApiKey(), id);

        if (actors != null) {
            final List<Actor> actorsList = actors.getActorList();
            Collections.sort(actorsList);
            show.setActorsList(actorsList);

            final StringBuilder sb = new StringBuilder("|");

            for (Actor actor : actorsList) {
                sb.append(actor.getName()).append("|");
            }

            show.setActors(sb.toString());
        }

        // Banner
        // BannerType: poster, fanart, series or season
        // BannerType2: graphical, text or blank
        url = url().append("{apikey}/series/{id}/banners.xml");
        final Images images = getRestTemplate().getForObject(url.toString(), Images.class, getApiKey(), id);

        if (images != null) {
            // List<Image> banners = images.getBanners();
            final List<Image> poster = images.getPosters();
            final List<Image> fanArt = new ArrayList<>();
            final List<Image> series = images.getBackdrops();
            final List<Image> season = new ArrayList<>();

            Collections.sort(poster);
            Collections.sort(fanArt);
            Collections.sort(series);
            Collections.sort(season);

            show.setPosterList(poster);
            show.setFanartList(fanArt);
            show.setSeriesList(series);
            show.setSeasonList(season);
        }

        return show;
    }

    public BufferedImage getImage(final String path) throws Exception {
        if (path == null || path.isBlank()) {
            return null;
        }

        final String url = String.format("http://thetvdb.com/banners/%s", path);
        BufferedImage image = null;

        try (InputStream inputStream = getCache().getResource(URI.create(url))) {
            image = ImageIO.read(inputStream);
        }

        return image;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<TVShow> search(final String name) {
        // http://thetvdb.com/api/GetSeries.php?seriesname=stargate&language=de
        final StringBuilder url = url().append("GetSeries.php?seriesname={name}&language={lang}");

        final Search search = getRestTemplate().getForObject(url.toString(), Search.class, urlEncode(name), getLocale().getLanguage());

        if (search == null || search.getSeries().isEmpty()) {
            return List.of();
        }

        // Redundante Treffer für Sprache filtern.
        final Map<String, TVShow> map = new HashMap<>();
        final Map<String, TVShow> map2 = new HashMap<>();

        for (TVShow show : search.getSeries()) {
            if (show.getLanguage().equals(getLocale().getLanguage())) {
                map.put(show.getID(), show);
            }
            else {
                map2.put(show.getID(), show);
            }
        }

        for (Entry<String, TVShow> entry : map2.entrySet()) {
            if (map.containsKey(entry.getKey())) {
                continue;
            }

            map.put(entry.getKey(), entry.getValue());
        }

        final List<TVShow> result = new ArrayList(map.values());
        Collections.sort(result);

        map.clear();
        map2.clear();

        return result;
    }

    public void setRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private RestTemplate getRestTemplate() {
        return restTemplate;
    }

    /**
     * Liefert '<a href="http://thetvdb.com/api/">thetvdb-api</a>'.<br>
     * <a href="http://thetvdb.com/api/1D62F2F90030C444/mirrors.xml">http://thetvdb.com/api/1D62F2F90030C444/mirrors.xml</a><br>
     */
    private StringBuilder url() {
        final StringBuilder sb = new StringBuilder();
        sb.append("http://thetvdb.com/api/");

        return sb;
    }
}
