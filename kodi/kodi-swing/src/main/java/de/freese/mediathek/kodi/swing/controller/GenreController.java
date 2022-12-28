// Created: 28.12.22
package de.freese.mediathek.kodi.swing.controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Model;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.view.GenreView;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public class GenreController extends AbstractController<Genre, GenreView>
{
    public GenreController(final ApplicationContext applicationContext)
    {
        super(applicationContext);
    }

    @Override
    public void init(final GenreView view)
    {
        super.init(view);

        view.doOnSelection(this::onSelection);
    }

    @Override
    protected List<Genre> loadEntities()
    {
        return getMediaService().getGenres();
    }

    @Override
    protected void onSelection(final Genre entity)
    {
        getView().updateWithSelection(entity);

        if (entity == null)
        {
            return;
        }

        SwingWorker<List<List<? extends Model>>, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected List<List<? extends Model>> doInBackground() throws Exception
            {
                MediaService mediaService = getMediaService();

                List<Show> shows = mediaService.getGenreShows(entity.getPk());
                List<Movie> movies = mediaService.getGenreMovies(entity.getPk());

                List<List<? extends Model>> results = new ArrayList<>();
                results.add(shows);
                results.add(movies);

                return results;
            }

            /**
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done()
            {
                try
                {
                    List<List<? extends Model>> results = get();

                    getView().setShowsAndMovies(results.get(0), results.get(1));
                }
                catch (Exception ex)
                {
                    getLogger().error(ex.getMessage(), ex);
                }
            }
        };
        worker.execute();
    }
}
