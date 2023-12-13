// Created: 08.11.2014
package de.freese.mediathek.services.thetvdb;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;

import de.freese.mediathek.services.themoviedb.model.Image;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "Series")
@XmlAccessorType(XmlAccessType.FIELD)
public class TVShow implements Comparable<TVShow> {
    @XmlElement(name = "Actors", required = false)
    private String actors;
    private List<Actor> actorsList;
    @XmlElement()
    private String banner;
    @XmlElement(name = "Overview")
    private String beschreibung;
    private List<Episode> episodes;
    @XmlElement(name = "fanart", required = false)
    private String fanArt;
    private List<Image> fanartList;
    @XmlElement(name = "Genre", required = false)
    private String genres;
    @XmlElement(name = "id")
    private String id;
    @XmlElement(name = "IMDB_ID")
    private String imdbID;
    @XmlElements({@XmlElement(name = "language"), @XmlElement(name = "Language")})
    private String language;
    @XmlElement(required = false)
    private String poster;
    private List<Image> posterList;
    @XmlElement(name = "FirstAired")
    private String releaseDate;
    private List<Image> seasonList;
    private List<Image> seriesList;
    @XmlElement(name = "SeriesName")
    private String title;

    @Override
    public int compareTo(final TVShow o) {
        int comp = getJahr().compareTo(o.getJahr());

        if (comp == 0) {
            comp = getTitle().compareTo(o.getTitle());
        }

        return comp;
    }

    public String getActors() {
        return this.actors;
    }

    public List<Actor> getActorsList() {
        return this.actorsList;
    }

    public String getBanner() {
        return this.banner;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public List<Episode> getEpisodes() {
        return this.episodes;
    }

    public String getFanArt() {
        return this.fanArt;
    }

    public List<Image> getFanartList() {
        return this.fanartList;
    }

    public String getGenres() {
        return this.genres;
    }

    public String getID() {
        return this.id;
    }

    public String getImdbID() {
        return this.imdbID;
    }

    public String getJahr() {
        return getReleaseDate().substring(0, 4);
    }

    public String getLanguage() {
        return this.language;
    }

    public String getPoster() {
        return this.poster;
    }

    public List<Image> getPosterList() {
        return this.posterList;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public List<Image> getSeasonList() {
        return this.seasonList;
    }

    public List<Image> getSeriesList() {
        return this.seriesList;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Serie [");
        builder.append("id=").append(this.id);
        builder.append(", releaseDate=").append(this.releaseDate);
        builder.append(", title=").append(this.title);
        builder.append(", language=").append(this.language);
        builder.append("]");

        return builder.toString();
    }

    void setActors(final String actors) {
        this.actors = actors;
    }

    void setActorsList(final List<Actor> actorsList) {
        this.actorsList = actorsList;
    }

    void setEpisodes(final List<Episode> episodes) {
        this.episodes = episodes;
    }

    void setFanartList(final List<Image> fanartList) {
        this.fanartList = fanartList;
    }

    void setPosterList(final List<Image> posterList) {
        this.posterList = posterList;
    }

    void setSeasonList(final List<Image> seasonList) {
        this.seasonList = seasonList;
    }

    void setSeriesList(final List<Image> seriesList) {
        this.seriesList = seriesList;
    }
}
