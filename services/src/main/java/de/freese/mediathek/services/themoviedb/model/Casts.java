// Created: 26.04.2014
package de.freese.mediathek.services.themoviedb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Casts {
    private List<Actor> cast;
    private List<Crew> crew;

    public List<Actor> getCast() {
        return this.cast;
    }

    public List<Crew> getCrew() {
        return this.crew;
    }

    public List<Crew> getDirectors() {
        final List<Crew> directors = new ArrayList<>();

        for (Crew c : getCrew()) {
            if ("Director".equals(c.getJob())) {
                directors.add(c);
            }
        }

        return directors;
    }

    public void setCast(final List<Actor> cast) {
        this.cast = cast;

        Collections.sort(this.cast);
    }

    public void setCrew(final List<Crew> crew) {
        this.crew = crew;
    }
}
