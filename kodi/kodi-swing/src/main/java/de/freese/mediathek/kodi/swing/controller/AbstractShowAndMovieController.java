// Created: 28.12.22
package de.freese.mediathek.kodi.swing.controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.swing.KodiSwingClient;
import de.freese.mediathek.kodi.swing.components.GenreDialog;
import de.freese.mediathek.kodi.swing.service.AbstractShowAndMovieService;
import de.freese.mediathek.kodi.swing.view.AbstractShowAndMovieView;

/**
 * @author Thomas Freese
 */
public abstract class AbstractShowAndMovieController<T, S extends AbstractShowAndMovieService> extends AbstractController<S>
{
    protected AbstractShowAndMovieController(final S service)
    {
        super(service);
    }

    public void clear()
    {
        getView().getImageLabel().setIcon(null);
        getView().getGenreLabel().setText(null);
        getView().getIdLabel().setText(null);
    }

    @Override
    public AbstractShowAndMovieView<T> getView()
    {
        return (AbstractShowAndMovieView<T>) super.getView();
    }

    public void openGenreDialog()
    {
        T entity = getView().getSelected();

        SwingWorker<List<List<Genre>>, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected List<List<Genre>> doInBackground() throws Exception
            {
                List<Genre> allGenres = getService().getAllGenres();
                List<Genre> entityGenres = getService().getEntityGenres(entity);

                List<List<Genre>> result = new ArrayList<>();
                result.add(allGenres);
                result.add(entityGenres);

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
                    int[] newGenreIDs = new int[selected.size()];

                    for (int i = 0; i < newGenreIDs.length; i++)
                    {
                        newGenreIDs[i] = selected.get(i).getPk();
                    }

                    getService().updateEntityGenres(entity, newGenreIDs);
                    setSelected(entity);
                }
                catch (Exception ex)
                {
                    getLogger().error(ex.getMessage(), ex);
                }
            }
        };
        worker.execute();
    }

    public void reload()
    {
        getView().clear();

        List<T> data = getService().load();
        getView().fill(data);
    }

    public abstract void setSelected(T entity);

    protected void setImageIcon(T entity)
    {
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected ImageIcon doInBackground() throws Exception
            {
                return getService().loadImageIcon(entity);
            }

            /**
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done()
            {
                try
                {
                    getView().getImageLabel().setIcon(get());
                }
                catch (Exception ex)
                {
                    getLogger().error("No valid url: {}", ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
