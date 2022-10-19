// Created: 16.09.2014
package de.freese.mediathek.services.thetvdb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "thumb")
@XmlAccessorType(XmlAccessType.FIELD)
public class Thumb
{
    @XmlAttribute
    private String preview;

    public String getPreview()
    {
        return this.preview;
    }

    public void setPreview(final String preview)
    {
        this.preview = preview;
    }
}
