/**
 * Created: 28.09.2014
 */
package de.freese.mediathek.kodi.swing.beans;

import com.jgoodies.binding.beans.Model;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Model} der {@link Show}.
 *
 * @author Thomas Freese
 */
public class MovieBean extends Model
{
    /**
     *
     */
    static final String PROPERTY_GENRES = "genres";

    /**
     *
     */
    static final String PROPERTY_IMDB_ID = "imdbID";

    /**
     *
     */
    private static final long serialVersionUID = 5961025455444774034L;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private final Movie movie;

    /**
     * Erstellt ein neues {@link MovieBean} Object.
     *
     * @param movie {@link Movie}
     */
    public MovieBean(final Movie movie)
    {
        super();

        this.movie = movie;
    }

    /**
     * @return String
     */
    public String getGenres()
    {
        return this.movie.getGenres();
    }

    /**
     * @return String
     */
    public String getImdbID()
    {
        return this.movie.getImdbID();
    }

    /**
     * @return String
     */
    public String getPoster()
    {
        return this.movie.getPoster();
    }

    /**
     * @return String
     */
    public String getPosters()
    {
        return this.movie.getPosters();
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.movie.getName();
    }

    /**
     * Setzt die neuen Genres.
     *
     * @param genres String
     */
    public void setGenres(final String genres)
    {
        Object oldValue = getGenres();
        this.movie.setGenres(genres);

        if (this.logger.isDebugEnabled())
        {
            this.logger.debug("oldValue={}, newValue={}", oldValue, genres);
        }

        firePropertyChange(PROPERTY_GENRES, oldValue, genres);
    }

    /**
     * @param poster String
     */
    public void setPoster(final String poster)
    {
        this.movie.setPoster(poster);
    }
}
