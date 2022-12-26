// Created: 13.09.2014
package de.freese.mediathek.kodi.model;

/**
 * @author Thomas Freese
 */
public class Movie extends AbstractModel
{
    private String fanArts;

    private String genres;

    private String imDbId;

    private String poster;

    private String posters;

    private int setID;

    private int year;

    public String getFanArts()
    {
        return this.fanArts;
    }

    public String getGenres()
    {
        return this.genres;
    }

    public String getImDbId()
    {
        return this.imDbId;
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

    public void setFanArts(final String fanArts)
    {
        this.fanArts = fanArts;
    }

    public void setGenres(final String genres)
    {
        this.genres = genres;
    }

    public void setImDbId(final String imDbId)
    {
        this.imDbId = imDbId;
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
