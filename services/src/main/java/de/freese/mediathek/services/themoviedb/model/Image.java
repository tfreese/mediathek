// Created: 26.04.2014
package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image implements Comparable<Image> {
    /**
     * height
     */
    private int height;
    /**
     * file_path
     */
    private String path;
    /**
     * vote_average
     */
    private float voteAverage;
    /**
     * width
     */
    private int width;

    @Override
    public int compareTo(final Image o) {
        return Float.compare(o.getVoteAverage(), this.voteAverage);
    }

    public int getHeight() {
        return this.height;
    }

    public String getPath() {
        return this.path;
    }

    public float getVoteAverage() {
        return this.voteAverage;
    }

    public int getWidth() {
        return this.width;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    @JsonSetter("file_path")
    public void setPath(final String path) {
        this.path = path;
    }

    @JsonSetter("vote_average")
    public void setVoteAverage(final float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Image [width=");
        builder.append(this.width);
        builder.append(", height=");
        builder.append(this.height);
        builder.append(", voteAverage=");
        builder.append(this.voteAverage);
        builder.append(", path=");
        builder.append(this.path);
        builder.append("]");

        return builder.toString();
    }
}
