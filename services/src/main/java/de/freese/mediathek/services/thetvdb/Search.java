/**
 * Created: 08.11.2014
 */

package de.freese.mediathek.services.thetvdb;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "Data")
@XmlAccessorType(XmlAccessType.FIELD)
class Search
{
    /**
    *
    */
    @XmlElement(name = "Episode", required = false)
    private List<Episode> episodes;

    /**
    *
    */
    @XmlElement(name = "Series")
    private List<TVShow> series;

    /**
     * @return {@link List}<Episode>
     */
    List<Episode> getEpisodes()
    {
        return this.episodes;
    }

    /**
     * @return {@link List}<Serie>
     */
    List<TVShow> getSeries()
    {
        return this.series;
    }
}
