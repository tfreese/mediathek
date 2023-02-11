// Created: 16.09.2014
package de.freese.mediathek.services.thetvdb;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "fanart")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThumbList {
    @XmlElement(name = "thumb")
    private List<Thumb> thumbs = new ArrayList<>();

    public List<Thumb> getThumbs() {
        return this.thumbs;
    }

    public void setThumbs(final List<Thumb> thumbs) {
        this.thumbs = thumbs;
    }
}
