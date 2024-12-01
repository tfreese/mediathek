// Created: 08.11.2014
package de.freese.mediathek.services.thetvdb;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "Data")
@XmlAccessorType(XmlAccessType.FIELD)
class Search {
    @XmlElement(name = "Episode")
    private List<Episode> episodes;
    @XmlElement(name = "Series")
    private List<TVShow> series;

    List<Episode> getEpisodes() {
        return episodes;
    }

    List<TVShow> getSeries() {
        return series;
    }
}
