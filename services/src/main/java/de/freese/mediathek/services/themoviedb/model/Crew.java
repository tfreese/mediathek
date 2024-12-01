// Created: 26.04.2014
package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Crew {
    /**
     * job
     */
    private String job;
    /**
     * name
     */
    private String name;
    /**
     * profile_path
     */
    private String profile;

    public String getJob() {
        return job;
    }

    public String getName() {
        return name;
    }

    public String getProfile() {
        return profile;
    }

    public void setJob(final String job) {
        this.job = job;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @JsonSetter("profile_path")
    public void setProfile(final String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Crew [name=");
        builder.append(name);
        builder.append(", job=");
        builder.append(job);
        builder.append(", profile=");
        builder.append(profile);
        builder.append("]");

        return builder.toString();
    }
}
