// Created: 26.04.2014
package de.freese.mediathek.services.themoviedb.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Images {
    /**
     * backdrops
     */
    private List<Image> backdrops;
    /**
     * posters
     */
    private List<Image> posters;

    public List<Image> getBackdrops() {
        return List.copyOf(backdrops);
    }

    public List<Image> getPosters() {
        return List.copyOf(posters);
    }

    public void setBackdrops(final List<Image> backdrops) {
        this.backdrops = List.copyOf(backdrops);
    }

    public void setPosters(final List<Image> posters) {
        this.posters = List.copyOf(posters);
    }
}
