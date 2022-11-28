// Created: 13.09.2014
package de.freese.mediathek.kodi.model;

/**
 * @author Thomas Freese
 */
public class Show extends AbstractModel
{
    private String banner;

    private String fanart;

    private String genres;

    private String tvdbID;

    public String getBanner()
    {
        return this.banner;
    }

    public String getFanart()
    {
        return this.fanart;
    }

    public String getGenres()
    {
        return this.genres;
    }

    public String getTvdbID()
    {
        return this.tvdbID;
    }

    public void setBanner(final String banner)
    {
        this.banner = banner;
    }

    public void setFanart(final String fanart)
    {
        this.fanart = fanart;
    }

    public void setGenres(final String genres)
    {
        this.genres = genres;
    }

    public void setTvdbID(final String tvdbID)
    {
        this.tvdbID = tvdbID;
    }
}
