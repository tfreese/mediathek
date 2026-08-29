package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author Thomas Freese
 * @since 26.04.2014
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
        return "Crew [name=" + name
                + ", job=" + job
                + ", profile=" + profile
                + "]";
    }
}
