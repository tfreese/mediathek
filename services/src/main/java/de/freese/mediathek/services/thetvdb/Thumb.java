/**
 * Created: 16.09.2014
 */

package de.freese.mediathek.services.thetvdb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "thumb")
@XmlAccessorType(XmlAccessType.FIELD)
public class Thumb
{
    /**
     *
     */
    @XmlAttribute
    private String preview;

    /**
     * @return String
     */
    public String getPreview()
    {
        return this.preview;
    }

    /**
     * @param preview String
     */
    public void setPreview(final String preview)
    {
        this.preview = preview;
    }
}
