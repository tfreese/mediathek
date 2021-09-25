// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.action;

import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.SwingWorker;

import org.springframework.context.ApplicationContext;

import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.swing.KODISwingClient;
import de.freese.mediathek.kodi.swing.beans.MovieModel;
import de.freese.mediathek.kodi.swing.components.GenreAuswahlDialog;

/**
 * {@link Action} zum Editieren der Genres.
 *
 * @author Thomas Freese
 */
public class EditMovieGenresAction extends AbstractAction
{
    /**
     *
     */
    private static final long serialVersionUID = -3961368866360343742L;
    /**
     *
     */
    private final ApplicationContext applicationContext;
    /**
     *
     */
    private final MovieModel model;

    /**
     * Erstellt ein neues {@link EditMovieGenresAction} Object.
     *
     * @param applicationContext {@link ApplicationContext}
     * @param model {@link MovieModel}
     */
    public EditMovieGenresAction(final ApplicationContext applicationContext, final MovieModel model)
    {
        super("Edit Genres");

        this.applicationContext = applicationContext;
        this.model = model;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e)
    {
        final Movie movie = this.model.getSelectedMovie();

        SwingWorker<List<List<Genre>>, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected List<List<Genre>> doInBackground() throws Exception
            {
                MediaService service = EditMovieGenresAction.this.applicationContext.getBean(MediaService.class);
                List<Genre> allGenres = service.getGenres();
                List<Genre> movieGenres = service.getMovieGenres(movie.getPK());

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

                    GenreAuswahlDialog dialog = new GenreAuswahlDialog(KODISwingClient.FRAME);
                    dialog.open(result.get(0), result.get(1));

                    if (dialog.hasBeenCanceled())
                    {
                        return;
                    }

                    List<Genre> selected = dialog.getSelectedGenres();
                    int[] genreIDs = new int[selected.size()];

                    for (int i = 0; i < genreIDs.length; i++)
                    {
                        genreIDs[i] = selected.get(i).getPK();
                    }

                    MediaService service = EditMovieGenresAction.this.applicationContext.getBean(MediaService.class);

                    String newGenres = service.updateMovieGenres(movie.getPK(), genreIDs);
                    EditMovieGenresAction.this.model.getBean().setGenres(newGenres);
                }
                catch (Exception ex)
                {
                    KODISwingClient.LOGGER.error(null, ex);
                }
            }
        };
        worker.execute();
    }
}
