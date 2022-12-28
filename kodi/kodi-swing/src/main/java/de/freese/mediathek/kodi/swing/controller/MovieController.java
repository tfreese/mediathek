// Created: 27.12.22
package de.freese.mediathek.kodi.swing.controller;

import java.util.List;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.swing.view.MovieView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public class MovieController extends AbstractShowAndMovieController<Movie, MovieView>
{
    public MovieController(ApplicationContext applicationContext)
    {
        super(applicationContext);
    }

    @Override
    protected List<Genre> getEntityGenres(final Movie entity)
    {
        return getMediaService().getMovieGenres(entity.getPk());
    }

    @Override
    protected String getImageUrl(final Movie movie)
    {
        String url = StringUtils.substringBetween(movie.getPosters(), "preview=\"", "\">");

        if (url.contains("\""))
        {
            url = url.substring(0, url.indexOf('"'));
        }

        // url = StringUtils.replace(url, "t/p/w500", "t/p/w92");

        if (url.isBlank())
        {
            url = StringUtils.substringBetween(movie.getPosters(), ">", "<");
        }

        url = url.replace("t/p/w500", "t/p/w342"); // w92, w154, w185, w342, w500

        return url;
    }

    @Override
    protected List<Movie> loadEntities()
    {
        return getMediaService().getMovies();
    }

    @Override
    protected void updateEntityGenres(final Movie entity, final int[] newGenreIDs)
    {
        String newGenres = getMediaService().updateMovieGenres(entity.getPk(), newGenreIDs);
        entity.setGenres(newGenres);
    }
}
