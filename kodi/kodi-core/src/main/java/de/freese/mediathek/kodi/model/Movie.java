// Created: 13.09.2014
package de.freese.mediathek.kodi.model;

/**
 * @author Thomas Freese
 */
public class Movie extends AbstractModel
{
    /**
     *
     */
    private String fanarts;
    /**
     *
     */
    private String genres;
    /**
     *
     */
    private String imdbID;
    /**
     *
     */
    private String poster;
    /**
     *
     */
    private String posters;
    /**
     *
     */
    private int setID;
    /**
     *
     */
    private int year;

    /**
     * @return String
     */
    public String getFanarts()
    {
        return this.fanarts;
    }

    /**
     * @return String
     */
    public String getGenres()
    {
        return this.genres;
    }

    /**
     * @return String
     */
    public String getImdbID()
    {
        return this.imdbID;
    }

    /**
     * @return String
     */
    public String getPoster()
    {
        return this.poster;
    }

    /**
     * @return String
     */
    public String getPosters()
    {
        return this.posters;
    }

    /**
     * @return int
     */
    public int getSetID()
    {
        return this.setID;
    }

    /**
     * @return int
     */
    public int getYear()
    {
        return this.year;
    }

    /**
     * @param fanarts String
     */
    public void setFanarts(final String fanarts)
    {
        this.fanarts = fanarts;
    }

    /**
     * @param genres String
     */
    public void setGenres(final String genres)
    {
        this.genres = genres;
    }

    /**
     * @param imdbID String
     */
    public void setImdbID(final String imdbID)
    {
        this.imdbID = imdbID;
    }

    /**
     * @param poster String
     */
    public void setPoster(final String poster)
    {
        this.poster = poster;
    }

    /**
     * @param posters String
     */
    public void setPosters(final String posters)
    {
        this.posters = posters;
    }

    /**
     * @param setID int
     */
    public void setSetID(final int setID)
    {
        this.setID = setID;
    }

    /**
     * @param year int
     */
    public void setYear(final int year)
    {
        this.year = year;
    }
}
