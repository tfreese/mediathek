// Created: 24.04.2014
package de.freese.mediathek.services.themoviedb.model;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final MovieDetails that)) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        return runtime == that.runtime && Float.compare(voteAverage, that.voteAverage) == 0 && voteCount == that.voteCount && Objects.equals(actors, that.actors)
                && Objects.equals(collection, that.collection) && Objects.equals(countries, that.countries) && Objects.equals(directors, that.directors)
                && Objects.equals(genres, that.genres) && Objects.equals(imdbID, that.imdbID) && Objects.equals(languages, that.languages)
                && Objects.equals(studios, that.studios) && Objects.equals(tagline, that.tagline);
    }

    public List<Actor> getActors() {
        return List.copyOf(actors);
    }

    public String getCollection() {
        return collection;
    }

    public List<Country> getCountries() {
        return List.copyOf(countries);
    }

    public List<Crew> getDirectors() {
        return List.copyOf(directors);
    }

    public List<Genre> getGenres() {
        return List.copyOf(genres);
    }

    public String getImdbID() {
        return imdbID;
    }

    public List<Language> getLanguages() {
        return List.copyOf(languages);
    }

    public int getRuntime() {
        return runtime;
    }

    public List<Studio> getStudios() {
        return List.copyOf(studios);
    }

    public String getTagline() {
        return tagline;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), actors, collection, countries, directors, genres, imdbID, languages, runtime, studios, tagline, voteAverage, voteCount);
    }

    public void setActors(final List<Actor> actors) {
        this.actors = List.copyOf(actors);
    }

    @JsonSetter("belongs_to_collection")
    public void setCollection(final String collection) {
        this.collection = collection;
    }

    @JsonSetter("production_countries")
    public void setCountries(final List<Country> countries) {
        this.countries = List.copyOf(countries);
    }

    public void setDirectors(final List<Crew> directors) {
        this.directors = List.copyOf(directors);
    }

    public void setGenres(final List<Genre> genres) {
        this.genres = List.copyOf(genres);
    }

    @JsonSetter("imdb_id")
    public void setImdbID(final String imdbID) {
        this.imdbID = imdbID;
    }

    @JsonSetter("spoken_languages")
    public void setLanguages(final List<Language> languages) {
        this.languages = List.copyOf(languages);
    }

    public void setRuntime(final int runtime) {
        this.runtime = runtime;
    }

    @JsonSetter("production_companies")
    public void setStudios(final List<Studio> studios) {
        this.studios = List.copyOf(studios);
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
