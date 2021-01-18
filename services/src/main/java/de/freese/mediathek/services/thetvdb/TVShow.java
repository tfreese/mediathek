/**
 * Created: 08.11.2014
 */

package de.freese.mediathek.services.thetvdb;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import de.freese.mediathek.services.themoviedb.model.Image;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "Series")
@XmlAccessorType(XmlAccessType.FIELD)
public class TVShow implements Comparable<TVShow>
{
    /**
    *
    */
    @XmlElement(name = "Actors", required = false)
    private String actors;

    /**
    *
    */
    private List<Actor> actorsList;

    /**
    *
    */
    @XmlElement()
    private String banner;

    /**
    *
    */
    @XmlElement(name = "Overview")
    private String beschreibung;

    /**
    *
    */
    private List<Episode> episodes;

    /**
    *
    */
    @XmlElement(required = false)
    private String fanart;

    /**
    *
    */
    private List<Image> fanartList;

    /**
    *
    */
    @XmlElement(name = "Genre", required = false)
    private String genres;

    /**
    *
    */
    @XmlElement(name = "id")
    private String id;

    /**
    *
    */
    @XmlElement(name = "IMDB_ID")
    private String imdbID;

    /**
    *
    */
    @XmlElements(
    {
            @XmlElement(name = "language"), @XmlElement(name = "Language")
    })
    private String language = null;

    /**
    *
    */
    @XmlElement(required = false)
    private String poster = null;

    /**
    *
    */
    private List<Image> posterList = null;

    /**
    *
    */
    @XmlElement(name = "FirstAired")
    private String releaseDate = null;

    /**
    *
    */
    private List<Image> seasonList = null;

    /**
    *
    */
    private List<Image> seriesList = null;

    /**
    *
    */
    @XmlElement(name = "SeriesName")
    private String title = null;

    /**
     * Erstellt ein neues {@link TVShow} Object.
     */
    TVShow()
    {
        super();
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final TVShow o)
    {
        int comp = getJahr().compareTo(o.getJahr());

        if (comp == 0)
        {
            comp = getTitle().compareTo(o.getTitle());
        }

        return comp;
    }

    /**
     * @return String
     */
    public String getActors()
    {
        return this.actors;
    }

    /**
     * @return {@link List}<Actor>
     */
    public List<Actor> getActorsList()
    {
        return this.actorsList;
    }

    /**
     * @return String
     */
    public String getBanner()
    {
        return this.banner;
    }

    /**
     * @return String
     */
    public String getBeschreibung()
    {
        return this.beschreibung;
    }

    /**
     * @return {@link List}<Episode>
     */
    public List<Episode> getEpisodes()
    {
        return this.episodes;
    }

    /**
     * @return String
     */
    public String getFanart()
    {
        return this.fanart;
    }

    /**
     * @return {@link List}<Image>
     */
    public List<Image> getFanartList()
    {
        return this.fanartList;
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
    public String getID()
    {
        return this.id;
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
    public String getJahr()
    {
        if (StringUtils.isNoneBlank(getReleaseDate()))
        {
            return getReleaseDate().substring(0, 4);
        }

        return null;
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
    public String getPoster()
    {
        return this.poster;
    }

    /**
     * @return {@link List}<Image>
     */
    public List<Image> getPosterList()
    {
        return this.posterList;
    }

    /**
     * @return String
     */
    public String getReleaseDate()
    {
        return this.releaseDate;
    }

    /**
     * @return {@link List}<Image>
     */
    public List<Image> getSeasonList()
    {
        return this.seasonList;
    }

    /**
     * @return {@link List}<Image>
     */
    public List<Image> getSeriesList()
    {
        return this.seriesList;
    }

    /**
     * @return String
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * @param actors String
     */
    void setActors(final String actors)
    {
        this.actors = actors;
    }

    /**
     * @param actorsList {@link List}<Actor>
     */
    void setActorsList(final List<Actor> actorsList)
    {
        this.actorsList = actorsList;
    }

    /**
     * @param episodes {@link List}<Episode>
     */
    void setEpisodes(final List<Episode> episodes)
    {
        this.episodes = episodes;
    }

    /**
     * @param fanartList {@link List}<Image>
     */
    void setFanartList(final List<Image> fanartList)
    {
        this.fanartList = fanartList;
    }

    /**
     * @param posterList {@link List}<Image>
     */
    void setPosterList(final List<Image> posterList)
    {
        this.posterList = posterList;
    }

    /**
     * @param seasonList {@link List}<Image>
     */
    void setSeasonList(final List<Image> seasonList)
    {
        this.seasonList = seasonList;
    }

    /**
     * @param seriesList {@link List}<Image>
     */
    void setSeriesList(final List<Image> seriesList)
    {
        this.seriesList = seriesList;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Serie [");
        builder.append("id=").append(this.id);
        builder.append(", releaseDate=").append(this.releaseDate);
        builder.append(", title=").append(this.title);
        builder.append(", language=").append(this.language);
        builder.append("]");

        return builder.toString();
    }
}
