// Created: 08.11.2014
package de.freese.mediathek.services.thetvdb;

import java.util.List;
import java.util.Objects;

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
    @XmlElement(name = "Actors")
    private String actors;
    private List<Actor> actorsList;
    @XmlElement()
    private String banner;
    @XmlElement(name = "Overview")
    private String beschreibung;
    private List<Episode> episodes;
    @XmlElement(name = "fanart")
    private String fanArt;
    private List<Image> fanartList;
    @XmlElement(name = "Genre")
    private String genres;
    @XmlElement(name = "id")
    private String id;
    @XmlElement(name = "IMDB_ID")
    private String imdbID;
    @XmlElements({@XmlElement(name = "language"), @XmlElement(name = "Language")})
    private String language;
    @XmlElement()
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

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final TVShow tvShow)) {
            return false;
        }

        return Objects.equals(actors, tvShow.actors) && Objects.equals(actorsList, tvShow.actorsList) && Objects.equals(banner, tvShow.banner)
                && Objects.equals(beschreibung, tvShow.beschreibung) && Objects.equals(episodes, tvShow.episodes) && Objects.equals(fanArt, tvShow.fanArt)
                && Objects.equals(fanartList, tvShow.fanartList) && Objects.equals(genres, tvShow.genres) && Objects.equals(id, tvShow.id)
                && Objects.equals(imdbID, tvShow.imdbID) && Objects.equals(language, tvShow.language) && Objects.equals(poster, tvShow.poster)
                && Objects.equals(posterList, tvShow.posterList) && Objects.equals(releaseDate, tvShow.releaseDate)
                && Objects.equals(seasonList, tvShow.seasonList) && Objects.equals(seriesList, tvShow.seriesList) && Objects.equals(title, tvShow.title);
    }

    public String getActors() {
        return actors;
    }

    public List<Actor> getActorsList() {
        return List.copyOf(actorsList);
    }

    public String getBanner() {
        return banner;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public List<Episode> getEpisodes() {
        return List.copyOf(episodes);
    }

    public String getFanArt() {
        return fanArt;
    }

    public List<Image> getFanartList() {
        return List.copyOf(fanartList);
    }

    public String getGenres() {
        return genres;
    }

    public String getID() {
        return id;
    }

    public String getImdbID() {
        return imdbID;
    }

    public String getJahr() {
        return getReleaseDate().substring(0, 4);
    }

    public String getLanguage() {
        return language;
    }

    public String getPoster() {
        return poster;
    }

    public List<Image> getPosterList() {
        return List.copyOf(posterList);
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public List<Image> getSeasonList() {
        return List.copyOf(seasonList);
    }

    public List<Image> getSeriesList() {
        return List.copyOf(seriesList);
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int hashCode() {
        return Objects.hash(actors, actorsList, banner, beschreibung, episodes, fanArt, fanartList, genres, id, imdbID, language, poster, posterList, releaseDate, seasonList,
                seriesList, title);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Serie [");
        builder.append("id=").append(id);
        builder.append(", releaseDate=").append(releaseDate);
        builder.append(", title=").append(title);
        builder.append(", language=").append(language);
        builder.append("]");

        return builder.toString();
    }

    void setActors(final String actors) {
        this.actors = actors;
    }

    void setActorsList(final List<Actor> actorsList) {
        this.actorsList = List.copyOf(actorsList);
    }

    void setEpisodes(final List<Episode> episodes) {
        this.episodes = List.copyOf(episodes);
    }

    void setFanartList(final List<Image> fanartList) {
        this.fanartList = List.copyOf(fanartList);
    }

    void setPosterList(final List<Image> posterList) {
        this.posterList = List.copyOf(posterList);
    }

    void setSeasonList(final List<Image> seasonList) {
        this.seasonList = List.copyOf(seasonList);
    }

    void setSeriesList(final List<Image> seriesList) {
        this.seriesList = List.copyOf(seriesList);
    }
}
