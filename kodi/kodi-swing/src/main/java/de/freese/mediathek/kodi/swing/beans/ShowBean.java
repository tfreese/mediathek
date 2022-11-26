// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.beans;

import java.io.Serial;

import com.jgoodies.binding.beans.Model;
import de.freese.mediathek.kodi.model.Show;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Model} der {@link Show}.
 *
 * @author Thomas Freese
 */
public class ShowBean extends Model
{
    static final String PROPERTY_GENRES = "genres";

    static final String PROPERTY_TVDB_ID = "tvdbID";

    @Serial
    private static final long serialVersionUID = 952793224295913500L;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Show show;

    public ShowBean(final Show show)
    {
        super();

        this.show = show;
    }

    public String getBanner()
    {
        return this.show.getBanner();
    }

    public String getGenres()
    {
        return this.show.getGenres();
    }

    public String getName()
    {
        return this.show.getName();
    }

    public String getTvdbID()
    {
        return this.show.getTvdbID();
    }

    public void setBanner(final String banner)
    {
        this.show.setBanner(banner);
    }

    public void setGenres(final String genres)
    {
        Object oldValue = getGenres();
        this.show.setGenres(genres);

        if (this.logger.isDebugEnabled())
        {
            this.logger.debug("oldValue={}, newValue={}", oldValue, genres);
        }

        firePropertyChange(PROPERTY_GENRES, oldValue, genres);
    }
}
