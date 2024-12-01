// Created: 27.04.2014
package de.freese.mediathek.services.themoviedb.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Genre implements Comparable<Genre> {
    /**
     * id
     */
    private int id;
    /**
     * name
     */
    private String name;

    @Override
    public int compareTo(final Genre o) {
        return name.compareTo(o.getName());
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Genre genre)) {
            return false;
        }
        
        return id == genre.id && Objects.equals(name, genre.name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Genre [name=");
        builder.append(name);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");

        return builder.toString();
    }
}
