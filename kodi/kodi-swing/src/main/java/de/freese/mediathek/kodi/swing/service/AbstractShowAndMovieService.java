// Created: 28.12.22
package de.freese.mediathek.kodi.swing.service;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.utils.ImageUtils;
import de.freese.mediathek.utils.cache.ResourceCache;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public abstract class AbstractShowAndMovieService<T> extends AbstractService
{
    protected AbstractShowAndMovieService(final ApplicationContext applicationContext)
    {
        super(applicationContext);
    }

    public List<Genre> getAllGenres()
    {
        return getMediaService().getGenres();
    }

    public abstract List<Genre> getEntityGenres(T entity);

    public abstract List<T> load();

    public ImageIcon loadImageIcon(T entity) throws Exception
    {
        String url = getImageUrl(entity);

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
    }

    public abstract void updateEntityGenres(T entity, int[] newGenreIDs);

    protected abstract String getImageUrl(T entity);

    protected ResourceCache getResourceCache()
    {
        return getApplicationContext().getBean(ResourceCache.class);
    }
}
