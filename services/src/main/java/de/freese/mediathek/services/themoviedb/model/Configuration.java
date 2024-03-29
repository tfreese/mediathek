// Created: 24.04.2014
package de.freese.mediathek.services.themoviedb.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration {
    /**
     * backdrop_sizes
     */
    private List<String> backdropSizes;
    // private List<String> changeKeys = null;
    /**
     * secure_base_url
     */
    private String imageBaseURL;
    /**
     * logo_sizes
     */
    private List<String> logoSizes;
    /**
     * poster_sizes
     */
    private List<String> posterSizes;
    /**
     * profile_sizes
     */
    private List<String> profileSizes;

    public List<String> getBackdropSizes() {
        return this.backdropSizes;
    }

    // public List<String> getChangeKeys()
    // {
    // return this.changeKeys;
    // }

    public String getImageBaseURL() {
        return this.imageBaseURL;
    }

    public List<String> getLogoSizes() {
        return this.logoSizes;
    }

    public List<String> getPosterSizes() {
        return this.posterSizes;
    }

    public List<String> getProfileSizes() {
        return this.profileSizes;
    }

    @SuppressWarnings("unchecked")
    @JsonAnySetter
    public void parseUnknownProperties(final String propertyName, final Object propertyValue) {
        if ("images".equals(propertyName)) {
            final Map<String, Object> map = (Map<String, Object>) propertyValue;

            this.imageBaseURL = (String) map.get("secure_base_url");
            // this.imageBaseURL = (String) map.get("base_url");
            this.backdropSizes = (List<String>) map.get("backdrop_sizes");
            this.posterSizes = (List<String>) map.get("poster_sizes");
            this.logoSizes = (List<String>) map.get("logo_sizes");
            this.profileSizes = (List<String>) map.get("profile_sizes");
        }
    }

    // @JsonSetter("change_keys")
    // public void setChangeKeys(final List<String> changeKeys)
    // {
    // this.changeKeys = changeKeys;
    // }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Configuration [imageBaseURL=");
        builder.append(this.imageBaseURL);
        builder.append(", backdropSizes=");
        builder.append(this.backdropSizes);
        builder.append(", posterSizes=");
        builder.append(this.posterSizes);
        builder.append(", logoSizes=");
        builder.append(this.logoSizes);
        builder.append(", profileSizes=");
        builder.append(this.profileSizes);
        // builder.append(", changeKeys=");
        // builder.append(this.changeKeys);
        builder.append("]");

        return builder.toString();
    }
}
