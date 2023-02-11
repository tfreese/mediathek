// Created: 28.12.22
package de.freese.mediathek.kodi.swing.service;

import java.util.List;

import org.springframework.context.ApplicationContext;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.utils.MediaDbUtils;

/**
 * @author Thomas Freese
 */
public class ShowService extends AbstractShowAndMovieService<Show> {
    public ShowService(final ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public List<Genre> getEntityGenres(final Show entity) {
        return getMediaService().getShowGenres(entity.getPk());
    }

    @Override
    public List<Show> load() {
        return getMediaService().getShows();
    }

    @Override
    public void updateEntityGenres(final Show entity, final int[] newGenreIDs) {
        String newGenres = getMediaService().updateShowGenres(entity.getPk(), newGenreIDs);
        entity.setGenres(newGenres);
    }

    @Override
    protected String getImageUrl(final Show entity) {
        String url = MediaDbUtils.subStringBetween("preview=\"", "\">", entity.getBanner());

        if (url.contains("\"")) {
            url = url.substring(0, url.indexOf('"'));
        }

        // url = StringUtils.replace(url, "t/p/w500", "t/p/w92");

        if (url.isBlank()) {
            url = MediaDbUtils.subStringBetween(">", "<", entity.getBanner());
        }

        return url;
    }
}
