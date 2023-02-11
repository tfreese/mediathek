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
        List<Crew> directors = new ArrayList<>();

        for (Crew crew : getCrew()) {
            if ("Director".equals(crew.getJob())) {
                directors.add(crew);
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
