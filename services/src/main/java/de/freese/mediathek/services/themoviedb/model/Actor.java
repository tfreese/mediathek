// Created: 26.04.2014
package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Actor implements Comparable<Actor> {
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

    @Override
    public int compareTo(final Actor o) {
        return this.order - o.getOrder();
    }

    public String getName() {
        return this.name;
    }

    public int getOrder() {
        return this.order;
    }

    public String getProfile() {
        return this.profile;
    }

    public String getRole() {
        return this.role;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    @JsonSetter("profile_path")
    public void setProfile(final String profile) {
        this.profile = profile;
    }

    @JsonSetter("character")
    public void setRole(final String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
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
