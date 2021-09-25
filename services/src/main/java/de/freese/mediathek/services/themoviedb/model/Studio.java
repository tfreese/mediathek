// Created: 27.04.2014
package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Studio implements Comparable<Studio>
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
    public int compareTo(final Studio o)
    {
        return this.name.compareTo(o.getName());
    }

    /**
     * @return int
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @param id int
     */
    public void setId(final int id)
    {
        this.id = id;
    }

    /**
     * @param name String
     */
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
        builder.append("Studio [name=");
        builder.append(this.name);
        builder.append(", id=");
        builder.append(this.id);
        builder.append("]");

        return builder.toString();
    }
}
