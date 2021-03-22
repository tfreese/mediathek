package de.freese.mediathek.kodi.javafx.controller;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Movie;
import javafx.beans.binding.Bindings;

/**
 * @author Thomas Freese
 */
public class MovieController extends AbstractTvShowMovieController<Movie>
{
    /**
     * Erstellt ein neues {@link MovieController} Object.
     *
     * @param applicationContext {@link ApplicationContext}
     * @param resourceBundle {@link ResourceBundle}
     */
    public MovieController(final ApplicationContext applicationContext, final ResourceBundle resourceBundle)
    {
        super(applicationContext, resourceBundle);

        initialize(null, resourceBundle);
    }

    /**
     * @see de.freese.mediathek.kodi.javafx.controller.AbstractTvShowMovieController#getGenres(de.freese.mediathek.kodi.model.IModel)
     */
    @Override
    protected List<Genre> getGenres(final Movie value)
    {
        return getMediaService().getMovieGenres(value.getPK());
    }

    /**
     * @see de.freese.mediathek.kodi.javafx.controller.AbstractTvShowMovieController#getImageUri(de.freese.mediathek.kodi.model.IModel)
     */
    @Override
    protected URI getImageUri(final Movie value)
    {
        String url = StringUtils.substringBetween(value.getPosters(), "preview=\"", "\">");
        url = StringUtils.replace(url, "t/p/w500", "t/p/w342"); // w92, w154, w185, w342, w500

        if ((url == null) || url.isBlank())
        {
            return null;
        }

        return URI.create(url);
    }

    /**
     * @see de.freese.mediathek.kodi.javafx.controller.AbstractTvShowMovieController#initialize(java.net.URL, java.util.ResourceBundle)
     */
    @Override
    public void initialize(final URL url, final ResourceBundle rb)
    {
        super.initialize(url, rb);

        getPane().getIDProperty().bind(Bindings.selectString(getPane().getTableSelectionModel().selectedItemProperty(), "imdbID"));
    }

    /**
     * @see de.freese.mediathek.kodi.javafx.controller.AbstractTvShowMovieController#load()
     */
    @Override
    protected List<Movie> load()
    {
        List<Movie> movies = getMediaService().getMovies();

        return movies;
    }

    /**
     * @see de.freese.mediathek.kodi.javafx.controller.AbstractTvShowMovieController#updateDetails(de.freese.mediathek.kodi.model.IModel)
     */
    @Override
    protected void updateDetails(final Movie value)
    {
        super.updateDetails(value);

        getPane().getGenresProperty().set(null);

        if (value != null)
        {
            getPane().getGenresProperty().set(value.getGenres());
        }
    }

    /**
     * @see de.freese.mediathek.kodi.javafx.controller.AbstractTvShowMovieController#updateGenres(de.freese.mediathek.kodi.model.IModel, int[])
     */
    @Override
    protected void updateGenres(final Movie value, final int[] genreIDs)
    {
        String newGenres = getMediaService().updateMovieGenres(value.getPK(), genreIDs);
        getPane().getGenresProperty().set(newGenres);
        value.setGenres(newGenres);
    }
}
