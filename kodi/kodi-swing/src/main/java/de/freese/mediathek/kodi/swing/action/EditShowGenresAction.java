// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.action;

import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.SwingWorker;

import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.KodiSwingClient;
import de.freese.mediathek.kodi.swing.beans.ShowModel;
import de.freese.mediathek.kodi.swing.components.GenreAuswahlDialog;
import org.springframework.context.ApplicationContext;

/**
 * {@link Action} zum Editieren der Genres.
 *
 * @author Thomas Freese
 */
public class EditShowGenresAction extends AbstractAction
{
    /**
     *
     */
    private static final long serialVersionUID = -8720949560687284814L;
    /**
     *
     */
    private final ApplicationContext applicationContext;
    /**
     *
     */
    private final ShowModel model;

    /**
     * Erstellt ein neues {@link EditShowGenresAction} Object.
     *
     * @param applicationContext {@link ApplicationContext}
     * @param model {@link ShowModel}
     */
    public EditShowGenresAction(final ApplicationContext applicationContext, final ShowModel model)
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
        final Show show = this.model.getSelectedShow();

        SwingWorker<List<List<Genre>>, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected List<List<Genre>> doInBackground() throws Exception
            {
                MediaService service = EditShowGenresAction.this.applicationContext.getBean(MediaService.class);
                List<Genre> allGenres = service.getGenres();
                List<Genre> showGenres = service.getShowGenres(show.getPK());

                List<List<Genre>> result = new ArrayList<>();
                result.add(allGenres);
                result.add(showGenres);

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

                    GenreAuswahlDialog dialog = new GenreAuswahlDialog(KodiSwingClient.FRAME);
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

                    MediaService service = EditShowGenresAction.this.applicationContext.getBean(MediaService.class);

                    String newGenres = service.updateShowGenres(show.getPK(), genreIDs);
                    EditShowGenresAction.this.model.getBean().setGenres(newGenres);
                }
                catch (Exception ex)
                {
                    KodiSwingClient.LOGGER.error(null, ex);
                }
            }
        };
        worker.execute();
    }
}
