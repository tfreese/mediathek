// Created: 28.12.22
package de.freese.mediathek.kodi.swing.controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Model;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.service.GenreService;
import de.freese.mediathek.kodi.swing.view.GenreView;

/**
 * @author Thomas Freese
 */
public class GenreController extends AbstractController {
    public GenreController(final GenreService service, GenreView view) {
        super(service, view);
    }

    public void clear() {
        getView().clear();
    }

    public void reload() {
        getView().clear();

        List<Genre> data = getService().load();
        getView().fill(data);
    }

    public void setSelected(Genre genre) {
        getView().clear();

        SwingWorker<List<List<? extends Model>>, Void> worker = new SwingWorker<>() {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected List<List<? extends Model>> doInBackground() throws Exception {
                List<Show> shows = getService().getGenreShows(genre);
                List<Movie> movies = getService().getGenreMovies(genre);

                List<List<? extends Model>> results = new ArrayList<>();
                results.add(shows);
                results.add(movies);

                return results;
            }

            /**
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done() {
                try {
                    List<List<? extends Model>> results = get();

                    getView().setShowsAndMovies(results.get(0), results.get(1));
                }
                catch (Exception ex) {
                    getLogger().error(ex.getMessage(), ex);
                }
            }
        };
        worker.execute();
    }

    @Override
    protected GenreService getService() {
        return (GenreService) super.getService();
    }

    @Override
    protected GenreView getView() {
        return (GenreView) super.getView();
    }
}
