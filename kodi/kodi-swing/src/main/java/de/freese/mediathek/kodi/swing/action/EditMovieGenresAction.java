// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.action;

import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.SwingWorker;

import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.swing.KodiSwingClient;
import de.freese.mediathek.kodi.swing.components.GenreDialog;
import de.freese.mediathek.kodi.swing.controller.MovieController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public class EditMovieGenresAction extends AbstractAction
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EditMovieGenresAction.class);
    @Serial
    private static final long serialVersionUID = -3961368866360343742L;
    private final ApplicationContext applicationContext;

    private final MovieController controller;

    public EditMovieGenresAction(final ApplicationContext applicationContext, final MovieController controller)
    {
        super("Edit Genres");

        this.applicationContext = applicationContext;
        this.controller = controller;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e)
    {
        final Movie movie = this.controller.getSelectedMovie();
        final MediaService mediaService = applicationContext.getBean(MediaService.class);

        SwingWorker<List<List<Genre>>, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected List<List<Genre>> doInBackground() throws Exception
            {
                List<Genre> allGenres = mediaService.getGenres();
                List<Genre> movieGenres = mediaService.getMovieGenres(movie.getPk());

                List<List<Genre>> result = new ArrayList<>();
                result.add(allGenres);
                result.add(movieGenres);

                return result;
            }

            /**
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done()
            {
                try
                {
                    List<List<Genre>> result = get();

                    GenreDialog dialog = new GenreDialog(KodiSwingClient.FRAME);
                    dialog.open(result.get(0), result.get(1));

                    if (dialog.hasBeenCanceled())
                    {
                        return;
                    }

                    List<Genre> selected = dialog.getSelectedGenres();
                    int[] genreIDs = new int[selected.size()];

                    for (int i = 0; i < genreIDs.length; i++)
                    {
                        genreIDs[i] = selected.get(i).getPk();
                    }

                    String newGenres = mediaService.updateMovieGenres(movie.getPk(), genreIDs);
                    movie.setGenres(newGenres);
                }
                catch (Exception ex)
                {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        };
        worker.execute();
    }
}
