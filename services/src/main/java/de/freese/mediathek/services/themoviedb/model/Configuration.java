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
        return List.copyOf(backdropSizes);
    }

    // public List<String> getChangeKeys() {
    // return List.copyOf(changeKeys);
    // }

    public String getImageBaseURL() {
        return imageBaseURL;
    }

    public List<String> getLogoSizes() {
        return List.copyOf(logoSizes);
    }

    public List<String> getPosterSizes() {
        return List.copyOf(posterSizes);
    }

    public List<String> getProfileSizes() {
        return List.copyOf(profileSizes);
    }

    @SuppressWarnings("unchecked")
    @JsonAnySetter
    public void parseUnknownProperties(final String propertyName, final Object propertyValue) {
        if ("images".equals(propertyName)) {
            final Map<String, Object> map = (Map<String, Object>) propertyValue;

            imageBaseURL = (String) map.get("secure_base_url");
            // imageBaseURL = (String) map.get("base_url");
            backdropSizes = (List<String>) map.get("backdrop_sizes");
            posterSizes = (List<String>) map.get("poster_sizes");
            logoSizes = (List<String>) map.get("logo_sizes");
            profileSizes = (List<String>) map.get("profile_sizes");
        }
    }

    // @JsonSetter("change_keys")
    // public void setChangeKeys(final List<String> changeKeys) {
    // this.changeKeys = List.copyOf(changeKeys);
    // }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Configuration [imageBaseURL=");
        builder.append(imageBaseURL);
        builder.append(", backdropSizes=");
        builder.append(backdropSizes);
        builder.append(", posterSizes=");
        builder.append(posterSizes);
        builder.append(", logoSizes=");
        builder.append(logoSizes);
        builder.append(", profileSizes=");
        builder.append(profileSizes);
        // builder.append(", changeKeys=");
        // builder.append(changeKeys);
        builder.append("]");

        return builder.toString();
    }
}
