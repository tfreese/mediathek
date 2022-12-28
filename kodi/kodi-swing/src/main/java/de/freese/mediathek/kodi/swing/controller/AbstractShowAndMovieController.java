// Created: 27.12.22
package de.freese.mediathek.kodi.swing.controller;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.swing.KodiSwingClient;
import de.freese.mediathek.kodi.swing.components.GenreDialog;
import de.freese.mediathek.kodi.swing.view.AbstractShowAndMovieView;
import de.freese.mediathek.utils.ImageUtils;
import de.freese.mediathek.utils.cache.ResourceCache;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public abstract class AbstractShowAndMovieController<T, V extends AbstractShowAndMovieView<T>> extends AbstractController<T, V>
{
    protected AbstractShowAndMovieController(ApplicationContext applicationContext)
    {
        super(applicationContext);
    }

    @Override
    public void init(final V view)
    {
        super.init(view);

        view.doOnSelection(this::onSelection);

        view.doOnGenres(button -> button.addActionListener(event -> openGenreDialog()));
    }

    protected abstract List<Genre> getEntityGenres(T entity);

    protected abstract String getImageUrl(T entity);

    protected ResourceCache getResourceCache()
    {
        return getApplicationContext().getBean(ResourceCache.class);
    }

    @Override
    protected void onSelection(T entity)
    {
        getView().updateWithSelection(entity);

        if (entity == null)
        {
            return;
        }

        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected ImageIcon doInBackground() throws Exception
            {
                String url = getImageUrl(entity);

                if (url != null && !url.isBlank())
                {
                    try (InputStream inputStream = getResourceCache().getResource(URI.create(url)))
                    {
                        BufferedImage image = ImageIO.read(inputStream);

                        if (image == null)
                        {
                            return null;
                        }

                        image = ImageUtils.scaleImageKeepRatio(image, 1024, 768);

                        return new ImageIcon(image);
                    }
                    catch (Exception ex)
                    {
                        getLogger().error(ex.getMessage());
                    }
                }
                else
                {
                    getLogger().error("No valid url: {}", url);
                }

                return null;
            }

            /**
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done()
            {
                try
                {
                    getView().setImage(get());
                }
                catch (Exception ex)
                {
                    getLogger().error(ex.getMessage(), ex);
                }
            }
        };
        worker.execute();
    }

    protected void openGenreDialog()
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
                List<Genre> allGenres = getMediaService().getGenres();
                List<Genre> entityGenres = getEntityGenres(entity);

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

                    updateEntityGenres(entity, newGenreIDs);
                }
                catch (Exception ex)
                {
                    getLogger().error(ex.getMessage(), ex);
                }
            }
        };
        worker.execute();
    }

    protected abstract void updateEntityGenres(T entity, int[] newGenreIDs);
}
