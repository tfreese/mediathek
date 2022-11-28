// Created: 13.09.2014
package de.freese.mediathek.kodi.model;

/**
 * @author Thomas Freese
 */
public class Movie extends AbstractModel
{
    private String fanarts;

    private String genres;

    private String imdbID;

    private String poster;

    private String posters;

    private int setID;

    private int year;

    public String getFanarts()
    {
        return this.fanarts;
    }

    public String getGenres()
    {
        return this.genres;
    }

    public String getImdbID()
    {
        return this.imdbID;
    }

    public String getPoster()
    {
        return this.poster;
    }

    public String getPosters()
    {
        return this.posters;
    }

    public int getSetID()
    {
        return this.setID;
    }

    public int getYear()
    {
        return this.year;
    }

    public void setFanarts(final String fanarts)
    {
        this.fanarts = fanarts;
    }

    public void setGenres(final String genres)
    {
        this.genres = genres;
    }

    public void setImdbID(final String imdbID)
    {
        this.imdbID = imdbID;
    }

    public void setPoster(final String poster)
    {
        this.poster = poster;
    }

    public void setPosters(final String posters)
    {
        this.posters = posters;
    }

    public void setSetID(final int setID)
    {
        this.setID = setID;
    }

    public void setYear(final int year)
    {
        this.year = year;
    }
}
