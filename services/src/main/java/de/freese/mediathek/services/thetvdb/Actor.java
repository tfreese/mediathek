// Created: 10.11.2014
package de.freese.mediathek.services.thetvdb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "Actor")
@XmlAccessorType(XmlAccessType.FIELD)
public class Actor implements Comparable<Actor>
{
    /**
     *
     */
    @XmlElement(name = "id")
    private String id;
    /**
     *
     */
    @XmlElement(name = "Image")
    private String image;
    /**
     *
     */
    @XmlElement(name = "Name")
    private String name;
    /**
     *
     */
    @XmlElement(name = "Role")
    private String role;
    /**
     *
     */
    @XmlElement(name = "SortOrder")
    private int sortOrder = -1;

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Actor o)
    {
        int comp = this.sortOrder - o.sortOrder;

        if (comp == 0)
        {
            comp = getName().compareTo(o.getName());
        }

        return comp;
    }

    /**
     * @return String
     */
    public String getImage()
    {
        return this.image;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return String
     */
    public String getRole()
    {
        return this.role;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Actor [");
        builder.append("id=").append(this.id);
        builder.append(", name=").append(this.name);
        builder.append(", role=").append(this.role);
        builder.append(", sortOrder=").append(this.sortOrder);
        builder.append(", image=").append(this.image);
        builder.append("]");
        return builder.toString();
    }
}
