package de.freese.mediathek.kodi.javafx.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import org.springframework.context.ApplicationContext;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.utils.MediaDbUtils;

/**
 * @author Thomas Freese
 */
public class MovieController extends AbstractTvShowMovieController<Movie> {
    public MovieController(final ApplicationContext applicationContext, final ResourceBundle resourceBundle) {
        super(applicationContext, resourceBundle);

        initialize(null, resourceBundle);
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        super.initialize(url, rb);

        getPane().getIdProperty().bind(Bindings.selectString(getPane().getTableSelectionModel().selectedItemProperty(), "imDbId"));
    }

    @Override
    protected List<Genre> getGenres(final Movie value) {
        return getMediaService().getMovieGenres(value.getPk());
    }

    @Override
    protected String getImageUrl(final Movie value) {
        String url = MediaDbUtils.subStringBetween("preview=\"", "\">", value.getPosters());
        url = url.replace("t/p/w500", "t/p/w342"); // w92, w154, w185, w342, w500

        if ((url == null) || url.isBlank()) {
            return null;
        }

        return url;
    }

    @Override
    protected List<Movie> load() {
        return getMediaService().getMovies();
    }

    @Override
    protected void updateDetails(final Movie value) {
        super.updateDetails(value);

        getPane().getGenresProperty().set(null);

        if (value != null) {
            getPane().getGenresProperty().set(value.getGenres());
        }
    }

    @Override
    protected void updateGenres(final Movie value, final int[] genreIDs) {
        final String newGenres = getMediaService().updateMovieGenres(value.getPk(), genreIDs);
        getPane().getGenresProperty().set(newGenres);
        value.setGenres(newGenres);
    }
}
