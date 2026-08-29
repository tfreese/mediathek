package de.freese.mediathek.kodi.swing.controller;

import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.swing.service.MovieService;
import de.freese.mediathek.kodi.swing.view.MovieView;

/**
 * @author Thomas Freese
 * @since 28.12.2022
 */
public class MovieController extends AbstractShowAndMovieController<Movie> {
    public MovieController(final MovieService service, final MovieView view) {
        super(service, view);
    }

    @Override
    public void setSelected(final Movie entity) {
        getView().getGenreLabel().setText(entity.getGenres());
        getView().getIdLabel().setText(entity.getImDbId());

        setImageIcon(entity);
    }
}
