// Created: 27.12.22
package de.freese.mediathek.kodi.swing.view;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

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
    }

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
}
