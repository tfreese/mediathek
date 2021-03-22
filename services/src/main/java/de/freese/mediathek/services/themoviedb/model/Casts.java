/**
 * Created: 26.04.2014
 */

package de.freese.mediathek.services.themoviedb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Casts
{
    /**
     *
     */
    private List<Actor> cast;

    /**
     *
     */
    private List<Crew> crew;

    /**
     * @return {@link List}<Actor>
     */
    public List<Actor> getCast()
    {
        return this.cast;
    }

    /**
     * @return {@link List}<Crew>
     */
    public List<Crew> getCrew()
    {
        return this.crew;
    }

    /**
     * @return {@link List}
     */
    public List<Crew> getDirectors()
    {
        List<Crew> directors = new ArrayList<>();

        for (Crew crew : getCrew())
        {
            if ("Director".equals(crew.getJob()))
            {
                directors.add(crew);
            }
        }

        return directors;
    }

    /**
     * @param cast {@link List}<Actor>
     */
    public void setCast(final List<Actor> cast)
    {
        this.cast = cast;

        Collections.sort(this.cast);
    }

    /**
     * @param crew {@link List}<Crew>
     */
    public void setCrew(final List<Crew> crew)
    {
        this.crew = crew;
    }
}
