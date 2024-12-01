// Created: 26.04.2014
package de.freese.mediathek.services.themoviedb.model;

import java.util.Objects;

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
        return Float.compare(o.getVoteAverage(), voteAverage);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Image image)) {
            return false;
        }
        
        return height == image.height && Float.compare(voteAverage, image.voteAverage) == 0 && width == image.width && Objects.equals(path, image.path);
    }

    public int getHeight() {
        return height;
    }

    public String getPath() {
        return path;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, path, voteAverage, width);
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
        builder.append(width);
        builder.append(", height=");
        builder.append(height);
        builder.append(", voteAverage=");
        builder.append(voteAverage);
        builder.append(", path=");
        builder.append(path);
        builder.append("]");

        return builder.toString();
    }
}
