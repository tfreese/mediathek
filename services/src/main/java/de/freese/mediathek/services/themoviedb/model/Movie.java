// Created: 24.04.2014
package de.freese.mediathek.services.themoviedb.model;

import javax.swing.ImageIcon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Thomas Freese
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

    public String getBackdrop() {
        return this.backdrop;
    }

    public int getId() {
        return this.id;
    }

    public ImageIcon getImageIcon() {
        return this.imageIcon;
    }

    public String getJahr() {
        return getReleaseDate().substring(0, 4);
    }

    public String getOriginalTitle() {
        return this.originalTitle;
    }

    public String getPoster() {
        return this.poster;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public String getTitle() {
        return this.title;
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
        final StringBuilder builder = new StringBuilder();
        builder.append("Movie [id=");
        builder.append(this.id);
        builder.append(", title=");
        builder.append(this.title);
        builder.append(", originalTitle=");
        builder.append(this.originalTitle);
        builder.append(", releaseDate=");
        builder.append(this.releaseDate);
        builder.append(", backdrop=");
        builder.append(this.backdrop);
        builder.append(", poster=");
        builder.append(this.poster);
        builder.append("]");

        return builder.toString();
    }
}
