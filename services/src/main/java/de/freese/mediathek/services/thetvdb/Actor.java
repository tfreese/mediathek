// Created: 10.11.2014
package de.freese.mediathek.services.thetvdb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "Actor")
@XmlAccessorType(XmlAccessType.FIELD)
public class Actor implements Comparable<Actor> {
    @XmlElement(name = "SortOrder")
    private final int sortOrder = -1;
    @XmlElement(name = "id")
    private String id;
    @XmlElement(name = "Image")
    private String image;
    @XmlElement(name = "Name")
    private String name;
    @XmlElement(name = "Role")
    private String role;

    @Override
    public int compareTo(final Actor o) {
        int comp = this.sortOrder - o.sortOrder;

        if (comp == 0) {
            comp = getName().compareTo(o.getName());
        }

        return comp;
    }

    public String getImage() {
        return this.image;
    }

    public String getName() {
        return this.name;
    }

    public String getRole() {
        return this.role;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
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
