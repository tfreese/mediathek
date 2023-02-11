// Created: 10.11.2014
package de.freese.mediathek.services.thetvdb;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "Actors")
@XmlAccessorType(XmlAccessType.FIELD)
class Actors {
    @XmlElement(name = "Actor")
    private List<Actor> actors;

    List<Actor> getActors() {
        return this.actors;
    }
}
