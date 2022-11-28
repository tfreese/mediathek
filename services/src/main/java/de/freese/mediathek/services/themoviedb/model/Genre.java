// Created: 27.04.2014
package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Genre implements Comparable<Genre>
{
    /**
     * id
     */
    private int id;
    /**
     * name
     */
    private String name;

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Genre o)
    {
        return this.name.compareTo(o.getName());
    }

    public int getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setId(final int id)
    {
        this.id = id;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Genre [name=");
        builder.append(this.name);
        builder.append(", id=");
        builder.append(this.id);
        builder.append("]");

        return builder.toString();
    }
}
