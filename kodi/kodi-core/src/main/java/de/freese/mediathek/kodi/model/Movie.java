// Created: 13.09.2014
package de.freese.mediathek.kodi.model;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public final class Movie extends AbstractModel {
    private String fanArts;
    private String genres;
    private String imDbId;
    private String poster;
    private String posters;
    private int setID;
    private int year;

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Movie movie)) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        return setID == movie.setID && year == movie.year && Objects.equals(fanArts, movie.fanArts) && Objects.equals(genres, movie.genres)
                && Objects.equals(imDbId, movie.imDbId) && Objects.equals(poster, movie.poster) && Objects.equals(posters, movie.posters);
    }

    public String getFanArts() {
        return fanArts;
    }

    public String getGenres() {
        return genres;
    }

    public String getImDbId() {
        return imDbId;
    }

    public String getPoster() {
        return poster;
    }

    public String getPosters() {
        return posters;
    }

    public int getSetID() {
        return setID;
    }

    public int getYear() {
        return year;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fanArts, genres, imDbId, poster, posters, setID, year);
    }

    public void setFanArts(final String fanArts) {
        this.fanArts = fanArts;
    }

    public void setGenres(final String genres) {
        this.genres = genres;
    }

    public void setImDbId(final String imDbId) {
        this.imDbId = imDbId;
    }

    public void setPoster(final String poster) {
        this.poster = poster;
    }

    public void setPosters(final String posters) {
        this.posters = posters;
    }

    public void setSetID(final int setID) {
        this.setID = setID;
    }

    public void setYear(final int year) {
        this.year = year;
    }
}
