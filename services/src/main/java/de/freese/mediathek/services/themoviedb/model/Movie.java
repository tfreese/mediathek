package de.freese.mediathek.services.themoviedb.model;

import java.util.Objects;

import javax.swing.ImageIcon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Thomas Freese
 * @since 24.04.2014
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie implements Comparable<Movie> {
    @JsonProperty("backdrop_path")
    private String backdrop;
    /**
     * id
     */
    private int id = -1;
    private ImageIcon imageIcon;
    @JsonProperty("original_title")
    private String originalTitle;
    @JsonProperty("poster_path")
    private String poster;
    @JsonProperty("release_date")
    private String releaseDate;
    /**
     * title
     */
    private String title;

    @Override
    public int compareTo(final Movie o) {
        final String s1 = getJahr();
        final String s2 = o.getJahr();

        return s1.compareTo(s2);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Movie movie)) {
            return false;
        }

        return id == movie.id && Objects.equals(backdrop, movie.backdrop) && Objects.equals(imageIcon, movie.imageIcon)
                && Objects.equals(originalTitle, movie.originalTitle) && Objects.equals(poster, movie.poster)
                && Objects.equals(releaseDate, movie.releaseDate) && Objects.equals(title, movie.title);
    }

    public String getBackdrop() {
        return backdrop;
    }

    public int getId() {
        return id;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public String getJahr() {
        return getReleaseDate().substring(0, 4);
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPoster() {
        return poster;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int hashCode() {
        return Objects.hash(backdrop, id, imageIcon, originalTitle, poster, releaseDate, title);
    }

    // @JsonSetter("backdrop_path")
    public void setBackdrop(final String backdrop) {
        this.backdrop = backdrop;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setImageIcon(final ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }

    // @JsonSetter("original_title")
    public void setOriginalTitle(final String originalTitle) {
        this.originalTitle = originalTitle;
    }

    // @JsonSetter("poster_path")
    public void setPoster(final String poster) {
        this.poster = poster;
    }

    // @JsonSetter("release_date")
    public void setReleaseDate(final String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Movie [id=" + id
                + ", title=" + title
                + ", originalTitle=" + originalTitle
                + ", releaseDate=" + releaseDate
                + ", backdrop=" + backdrop
                + ", poster=" + poster
                + "]";
    }
}
