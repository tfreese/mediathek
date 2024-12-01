// Created: 26.04.2014
package de.freese.mediathek.services.themoviedb.model;

import java.util.Objects;

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
        return order - o.getOrder();
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Actor actor)) {
            return false;
        }
        
        return order == actor.order && Objects.equals(name, actor.name) && Objects.equals(profile, actor.profile) && Objects.equals(role, actor.role);
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public String getProfile() {
        return profile;
    }

    public String getRole() {
        return role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, order, profile, role);
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
        builder.append(name);
        builder.append(", role=");
        builder.append(role);
        builder.append(", order=");
        builder.append(order);
        builder.append(", profile=");
        builder.append(profile);
        builder.append("]");

        return builder.toString();
    }
}
