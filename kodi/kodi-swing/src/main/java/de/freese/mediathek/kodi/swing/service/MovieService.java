// Created: 28.12.22
package de.freese.mediathek.kodi.swing.service;

import java.util.List;

import org.springframework.context.ApplicationContext;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.utils.MediaDbUtils;

/**
 * @author Thomas Freese
 */
public class MovieService extends AbstractShowAndMovieService<Movie> {
    public MovieService(final ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public List<Genre> getEntityGenres(final Movie entity) {
        return getMediaService().getMovieGenres(entity.getPk());
    }

    @Override
    public List<Movie> load() {
        return getMediaService().getMovies();
    }

    @Override
    public void updateEntityGenres(final Movie entity, final int[] newGenreIDs) {
        String newGenres = getMediaService().updateMovieGenres(entity.getPk(), newGenreIDs);
        entity.setGenres(newGenres);
    }

    @Override
    protected String getImageUrl(final Movie entity) {
        String url = MediaDbUtils.subStringBetween("preview=\"", "\">", entity.getPosters());

        if (url.contains("\"")) {
            url = url.substring(0, url.indexOf('"'));
        }

        // url = StringUtils.replace(url, "t/p/w500", "t/p/w92");

        if (url.isBlank()) {
            url = MediaDbUtils.subStringBetween(">", "<", entity.getPosters());
        }

        url = url.replace("t/p/w500", "t/p/w342"); // w92, w154, w185, w342, w500

        return url;
    }
}
