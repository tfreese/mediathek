// Created: 24.04.2014
package de.freese.mediathek.services.themoviedb.model;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDetails extends Movie {
    private List<Actor> actors;
    /**
     * belongs_to_collection
     */
    private String collection;
    /**
     * production_countries
     */
    private List<Country> countries;
    private List<Crew> directors;
    /**
     * genres
     */
    private List<Genre> genres;
    /**
     * imdb_id
     */
    private String imdbID;
    /**
     * spoken_languages
     */
    private List<Language> languages;
    /**
     * runtime
     */
    private int runtime;
    /**
     * production_companies
     */
    private List<Studio> studios;
    /**
     * tagline
     */
    private String tagline;
    /**
     * vote_average
     */
    private float voteAverage;
    /**
     * vote_count
     */
    private int voteCount;

    public List<Actor> getActors() {
        return this.actors;
    }

    public String getCollection() {
        return this.collection;
    }

    public List<Country> getCountries() {
        return this.countries;
    }

    public List<Crew> getDirectors() {
        return this.directors;
    }

    public List<Genre> getGenres() {
        return this.genres;
    }

    public String getImdbID() {
        return this.imdbID;
    }

    public List<Language> getLanguages() {
        return this.languages;
    }

    public int getRuntime() {
        return this.runtime;
    }

    public List<Studio> getStudios() {
        return this.studios;
    }

    public String getTagline() {
        return this.tagline;
    }

    public float getVoteAverage() {
        return this.voteAverage;
    }

    public int getVoteCount() {
        return this.voteCount;
    }

    public void setActors(final List<Actor> actors) {
        this.actors = actors;

        Collections.sort(this.actors);
    }

    @JsonSetter("belongs_to_collection")
    public void setCollection(final String collection) {
        this.collection = collection;
    }

    @JsonSetter("production_countries")
    public void setCountries(final List<Country> countries) {
        this.countries = countries;

        Collections.sort(this.countries);
    }

    public void setDirectors(final List<Crew> directors) {
        this.directors = directors;
    }

    public void setGenres(final List<Genre> genres) {
        this.genres = genres;

        Collections.sort(this.genres);
    }

    @JsonSetter("imdb_id")
    public void setImdbID(final String imdbID) {
        this.imdbID = imdbID;
    }

    @JsonSetter("spoken_languages")
    public void setLanguages(final List<Language> languages) {
        this.languages = languages;

        // Collections.sort(this.languages);
    }

    public void setRuntime(final int runtime) {
        this.runtime = runtime;
    }

    @JsonSetter("production_companies")
    public void setStudios(final List<Studio> studios) {
        this.studios = studios;

        // Collections.sort(this.studios);
    }

    public void setTagline(final String tagline) {
        this.tagline = tagline;
    }

    @JsonSetter("vote_average")
    public void setVoteAverage(final float voteAverage) {
        this.voteAverage = voteAverage;
    }

    @JsonSetter("vote_count")
    public void setVoteCount(final int voteCount) {
        this.voteCount = voteCount;
    }
}
