package de.freese.mediathek.kodi.javafx.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import org.springframework.context.ApplicationContext;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.utils.MediaDbUtils;

/**
 * @author Thomas Freese
 */
public class TvShowController extends AbstractTvShowMovieController<Show> {
    public TvShowController(final ApplicationContext applicationContext, final ResourceBundle resourceBundle) {
        super(applicationContext, resourceBundle);

        initialize(null, resourceBundle);
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        super.initialize(url, rb);

        getPane().getIdProperty().bind(Bindings.selectString(getPane().getTableSelectionModel().selectedItemProperty(), "tvDbId"));
    }

    @Override
    protected List<Genre> getGenres(final Show value) {
        return getMediaService().getShowGenres(value.getPk());
    }

    @Override
    protected String getImageUrl(final Show value) {
        String url = MediaDbUtils.subStringBetween("preview=\"", "\">", value.getBanner());

        if ((url == null) || url.isBlank()) {
            return null;
        }

        return url;
    }

    @Override
    protected List<Show> load() {
        return getMediaService().getShows();
    }

    @Override
    protected void updateDetails(final Show value) {
        super.updateDetails(value);

        getPane().getGenresProperty().set(null);

        if (value != null) {
            getPane().getGenresProperty().set(value.getGenres());
        }
    }

    @Override
    protected void updateGenres(final Show value, final int[] genreIDs) {
        String newGenres = getMediaService().updateShowGenres(value.getPk(), genreIDs);
        getPane().getGenresProperty().set(newGenres);
        value.setGenres(newGenres);
    }
}
