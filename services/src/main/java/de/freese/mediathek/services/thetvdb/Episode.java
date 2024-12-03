// Created: 10.11.2014
package de.freese.mediathek.services.thetvdb;

import java.util.Objects;

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
public class Episode implements Comparable<Episode> {
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
    @XmlElements({@XmlElement(name = "language"), @XmlElement(name = "Language")})
    private String language;
    @XmlElement(name = "FirstAired")
    private String releaseDate;
    @XmlElement(name = "EpisodeName")
    private String title;

    @Override
    public int compareTo(final Episode o) {
        int comp = getSeason() - o.getSeason();

        if (comp == 0) {
            comp = getEpisode() - o.getEpisode();
        }

        return comp;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Episode episode1)) {
            return false;
        }

        return episode == episode1.episode && season == episode1.season && Objects.equals(beschreibung, episode1.beschreibung)
                && Objects.equals(guestStars, episode1.guestStars) && Objects.equals(id, episode1.id) && Objects.equals(image, episode1.image)
                && Objects.equals(language, episode1.language) && Objects.equals(releaseDate, episode1.releaseDate) && Objects.equals(title, episode1.title);
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public int getEpisode() {
        return episode;
    }

    public String getGuestStars() {
        return guestStars;
    }

    public String getID() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getLanguage() {
        return language;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int getSeason() {
        return season;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int hashCode() {
        return Objects.hash(episode, season, beschreibung, guestStars, id, image, language, releaseDate, title);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Episode [");
        builder.append("id=").append(id);
        builder.append(", season=").append(season);
        builder.append(", episode=").append(episode);
        builder.append(", releaseDate=").append(releaseDate);
        builder.append(", title=").append(title);
        builder.append(", language=").append(language);
        builder.append("]");

        return builder.toString();
    }
}
