// Created: 26.04.2014
package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Actor implements Comparable<Actor>
{
    /**
     * name
     */
    private String name;
    /**
     * order
     */
    private int order;
    /**
     * profile_path
     */
    private String profile;
    /**
     * character
     */
    private String role;

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Actor o)
    {
        return this.order - o.getOrder();
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return int
     */
    public int getOrder()
    {
        return this.order;
    }

    /**
     * @return String
     */
    public String getProfile()
    {
        return this.profile;
    }

    /**
     * @return String
     */
    public String getRole()
    {
        return this.role;
    }

    /**
     * @param name String
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @param order int
     */
    public void setOrder(final int order)
    {
        this.order = order;
    }

    /**
     * @param profile String
     */
    @JsonSetter("profile_path")
    public void setProfile(final String profile)
    {
        this.profile = profile;
    }

    /**
     * @param role String
     */
    @JsonSetter("character")
    public void setRole(final String role)
    {
        this.role = role;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Actor [name=");
        builder.append(this.name);
        builder.append(", role=");
        builder.append(this.role);
        builder.append(", order=");
        builder.append(this.order);
        builder.append(", profile=");
        builder.append(this.profile);
        builder.append("]");

        return builder.toString();
    }
}
