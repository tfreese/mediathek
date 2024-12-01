// Created: 10.11.2014
package de.freese.mediathek.services.thetvdb;

import java.util.Objects;

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
        int comp = sortOrder - o.sortOrder;

        if (comp == 0) {
            comp = getName().compareTo(o.getName());
        }

        return comp;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Actor actor)) {
            return false;
        }

        return sortOrder == actor.sortOrder && Objects.equals(id, actor.id) && Objects.equals(image, actor.image) && Objects.equals(name, actor.name) &&
                Objects.equals(role, actor.role);
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sortOrder, id, image, name, role);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Actor [");
        builder.append("id=").append(id);
        builder.append(", name=").append(name);
        builder.append(", role=").append(role);
        builder.append(", sortOrder=").append(sortOrder);
        builder.append(", image=").append(image);
        builder.append("]");

        return builder.toString();
    }
}
