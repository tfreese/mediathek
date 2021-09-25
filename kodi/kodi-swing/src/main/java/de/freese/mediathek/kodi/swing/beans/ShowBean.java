// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.binding.beans.Model;

import de.freese.mediathek.kodi.model.Show;

/**
 * {@link Model} der {@link Show}.
 *
 * @author Thomas Freese
 */
public class ShowBean extends Model
{
    /**
     *
     */
    static final String PROPERTY_GENRES = "genres";
    /**
     *
     */
    static final String PROPERTY_TVDB_ID = "tvdbID";
    /**
     *
     */
    private static final long serialVersionUID = 952793224295913500L;
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     *
     */
    private final Show show;

    /**
     * Erstellt ein neues {@link ShowBean} Object.
     *
     * @param show {@link Show}
     */
    public ShowBean(final Show show)
    {
        super();

        this.show = show;
    }

    /**
     * @return String
     */
    public String getBanner()
    {
        return this.show.getBanner();
    }

    /**
     * @return String
     */
    public String getGenres()
    {
        return this.show.getGenres();
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.show.getName();
    }

    /**
     * @return String
     */
    public String getTvdbID()
    {
        return this.show.getTvdbID();
    }

    /**
     * @param banner String
     */
    public void setBanner(final String banner)
    {
        this.show.setBanner(banner);
    }

    /**
     * Setzt die neuen Genres.
     *
     * @param genres String
     */
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
