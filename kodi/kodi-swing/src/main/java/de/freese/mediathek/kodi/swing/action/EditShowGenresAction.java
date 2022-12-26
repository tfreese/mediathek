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
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.KodiSwingClient;
import de.freese.mediathek.kodi.swing.components.GenreDialog;
import de.freese.mediathek.kodi.swing.controller.ShowController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public class EditShowGenresAction extends AbstractAction
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EditShowGenresAction.class);

    @Serial
    private static final long serialVersionUID = -8720949560687284814L;

    private final ApplicationContext applicationContext;

    private final ShowController controller;

    public EditShowGenresAction(final ApplicationContext applicationContext, final ShowController controller)
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
        final Show show = this.controller.getSelectedShow();
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
                List<Genre> showGenres = mediaService.getShowGenres(show.getPk());

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

                    String newGenres = mediaService.updateShowGenres(show.getPk(), genreIDs);
                    show.setGenres(newGenres);
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
