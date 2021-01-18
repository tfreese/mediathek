/**
 * Created: 26.04.2014
 */

package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image implements Comparable<Image>
{
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

    /**
     * Erstellt ein neues {@link Image} Object.
     */
    public Image()
    {
        super();
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Image o)
    {
        return Float.compare(o.getVoteAverage(), this.voteAverage);
    }

    /**
     * @return int
     */
    public int getHeight()
    {
        return this.height;
    }

    /**
     * @return String
     */
    public String getPath()
    {
        return this.path;
    }

    /**
     * @return float
     */
    public float getVoteAverage()
    {
        return this.voteAverage;
    }

    /**
     * @return int
     */
    public int getWidth()
    {
        return this.width;
    }

    /**
     * @param height int
     */
    public void setHeight(final int height)
    {
        this.height = height;
    }

    /**
     * @param path String
     */
    @JsonSetter("file_path")
    public void setPath(final String path)
    {
        this.path = path;
    }

    /**
     * @param voteAverage float
     */
    @JsonSetter("vote_average")
    public void setVoteAverage(final float voteAverage)
    {
        this.voteAverage = voteAverage;
    }

    /**
     * @param width int
     */
    public void setWidth(final int width)
    {
        this.width = width;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
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
