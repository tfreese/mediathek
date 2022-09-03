package de.freese.mediathek.kodi.javafx.controller;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Model;
import de.freese.mediathek.kodi.model.Show;
import javafx.beans.binding.Bindings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public class TvShowController extends AbstractTvShowMovieController<Show>
{
    /**
     * Erstellt ein neues {@link TvShowController} Object.
     *
     * @param applicationContext {@link ApplicationContext}
     * @param resourceBundle {@link ResourceBundle}
     */
    public TvShowController(final ApplicationContext applicationContext, final ResourceBundle resourceBundle)
    {
        super(applicationContext, resourceBundle);

        initialize(null, resourceBundle);
    }

    /**
     * @see de.freese.mediathek.kodi.javafx.controller.AbstractTvShowMovieController#initialize(java.net.URL, java.util.ResourceBundle)
     */
    @Override
    public void initialize(final URL url, final ResourceBundle rb)
    {
        super.initialize(url, rb);

        getPane().getIDProperty().bind(Bindings.selectString(getPane().getTableSelectionModel().selectedItemProperty(), "tvdbID"));
    }

    /**
     * @see de.freese.mediathek.kodi.javafx.controller.AbstractTvShowMovieController#getGenres(Model)
     */
    @Override
    protected List<Genre> getGenres(final Show value)
    {
        return getMediaService().getShowGenres(value.getPK());
    }

    /**
     * @see de.freese.mediathek.kodi.javafx.controller.AbstractTvShowMovieController#getImageUri(Model)
     */
    @Override
    protected URI getImageUri(final Show value)
    {
        String url = StringUtils.substringBetween(value.getBanner(), "preview=\"", "\">");

        if ((url == null) || url.isBlank())
        {
            return null;
        }

        return URI.create(url);
    }

    /**
     * @see de.freese.mediathek.kodi.javafx.controller.AbstractTvShowMovieController#load()
     */
    @Override
    protected List<Show> load()
    {
        return getMediaService().getShows();
    }

    /**
     * @see de.freese.mediathek.kodi.javafx.controller.AbstractTvShowMovieController#updateDetails(Model)
     */
    @Override
    protected void updateDetails(final Show value)
    {
        super.updateDetails(value);

        getPane().getGenresProperty().set(null);

        if (value != null)
        {
            getPane().getGenresProperty().set(value.getGenres());
        }
    }

    /**
     * @see de.freese.mediathek.kodi.javafx.controller.AbstractTvShowMovieController#updateGenres(Model, int[])
     */
    @Override
    protected void updateGenres(final Show value, final int[] genreIDs)
    {
        String newGenres = getMediaService().updateShowGenres(value.getPK(), genreIDs);
        getPane().getGenresProperty().set(newGenres);
        value.setGenres(newGenres);
    }
}
