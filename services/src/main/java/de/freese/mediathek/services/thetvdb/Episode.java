/**
 * Created: 10.11.2014
 */

package de.freese.mediathek.services.thetvdb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "Episode")
@XmlAccessorType(XmlAccessType.FIELD)
public class Episode implements Comparable<Episode>
{
    /**
     * 
     */
    @XmlElement(name = "Overview")
    private String beschreibung;

    /**
     * 
     */
    @XmlElement(name = "EpisodeNumber")
    private int episode = -1;

    /**
     * 
     */
    @XmlElement(name = "GuestStars")
    private String guestStars;

    /**
     * 
     */
    @XmlElement(name = "id")
    private String id;

    /**
     * 
     */
    @XmlElement(name = "filename")
    private String image;

    /**
     * 
     */
    @XmlElements(
    {
            @XmlElement(name = "language"), @XmlElement(name = "Language")
    })
    private String language;

    /**
     * 
     */
    @XmlElement(name = "FirstAired")
    private String releaseDate;

    /**
     * 
     */
    @XmlElement(name = "SeasonNumber")
    private int season = -1;

    /**
     * 
     */
    @XmlElement(name = "EpisodeName")
    private String title;

    /**
     * Erstellt ein neues {@link Episode} Object.
     */
    Episode()
    {
        super();
    }

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

    /**
     * @return String
     */
    public String getBeschreibung()
    {
        return this.beschreibung;
    }

    /**
     * @return int
     */
    public int getEpisode()
    {
        return this.episode;
    }

    /**
     * @return String
     */
    public String getGuestStars()
    {
        return this.guestStars;
    }

    /**
     * @return String
     */
    public String getID()
    {
        return this.id;
    }

    /**
     * @return String
     */
    public String getImage()
    {
        return this.image;
    }

    /**
     * @return String
     */
    public String getLanguage()
    {
        return this.language;
    }

    /**
     * @return String
     */
    public String getReleaseDate()
    {
        return this.releaseDate;
    }

    /**
     * @return int
     */
    public int getSeason()
    {
        return this.season;
    }

    /**
     * @return String
     */
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
