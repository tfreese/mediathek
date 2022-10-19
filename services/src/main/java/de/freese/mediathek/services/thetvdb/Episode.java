// Created: 10.11.2014
package de.freese.mediathek.services.thetvdb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "Episode")
@XmlAccessorType(XmlAccessType.FIELD)
public class Episode implements Comparable<Episode>
{
    @XmlElement(name = "EpisodeNumber")
    private final int episode = -1;
    @XmlElement(name = "SeasonNumber")
    private final int season = -1;
    @XmlElement(name = "Overview")
    private String beschreibung;
    @XmlElement(name = "GuestStars")
    private String guestStars;
    @XmlElement(name = "id")
    private String id;
    @XmlElement(name = "filename")
    private String image;
    @XmlElements(
            {
                    @XmlElement(name = "language"), @XmlElement(name = "Language")
            })
    private String language;
    @XmlElement(name = "FirstAired")
    private String releaseDate;
    @XmlElement(name = "EpisodeName")
    private String title;

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Episode o)
    {
        int comp = getSeason() - o.getSeason();

        if (comp == 0)
        {
            comp = getEpisode() - o.getEpisode();
        }

        return comp;
    }

    public String getBeschreibung()
    {
        return this.beschreibung;
    }

    public int getEpisode()
    {
        return this.episode;
    }

    public String getGuestStars()
    {
        return this.guestStars;
    }

    public String getID()
    {
        return this.id;
    }

    public String getImage()
    {
        return this.image;
    }

    public String getLanguage()
    {
        return this.language;
    }

    public String getReleaseDate()
    {
        return this.releaseDate;
    }

    public int getSeason()
    {
        return this.season;
    }

    public String getTitle()
    {
        return this.title;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Episode [");
        builder.append("id=").append(this.id);
        builder.append(", season=").append(this.season);
        builder.append(", episode=").append(this.episode);
        builder.append(", releaseDate=").append(this.releaseDate);
        builder.append(", title=").append(this.title);
        builder.append(", language=").append(this.language);
        builder.append("]");

        return builder.toString();
    }
}
