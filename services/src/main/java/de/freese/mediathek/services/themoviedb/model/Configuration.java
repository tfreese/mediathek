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
public class Configuration
{
    /**
     * backdrop_sizes
     */
    private List<String> backdropSizes;
    // /**
    // *
    // */
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

    /**
     * @return {@link List}<String>
     */
    public List<String> getBackdropSizes()
    {
        return this.backdropSizes;
    }

    // /**
    // * @return {@link List}<String>
    // */
    // public List<String> getChangeKeys()
    // {
    // return this.changeKeys;
    // }

    /**
     * @return String
     */
    public String getImageBaseURL()
    {
        return this.imageBaseURL;
    }

    /**
     * @return {@link List}<String>
     */
    public List<String> getLogoSizes()
    {
        return this.logoSizes;
    }

    /**
     * @return {@link List}<String>
     */
    public List<String> getPosterSizes()
    {
        return this.posterSizes;
    }

    /**
     * @return {@link List}<String>
     */
    public List<String> getProfileSizes()
    {
        return this.profileSizes;
    }

    /**
     * @param propertyName String
     * @param propertyValue String
     */
    @SuppressWarnings("unchecked")
    @JsonAnySetter
    public void parseUnknownProperties(final String propertyName, final Object propertyValue)
    {
        if ("images".equals(propertyName))
        {
            Map<String, Object> map = (Map<String, Object>) propertyValue;

            this.imageBaseURL = (String) map.get("secure_base_url");
            // this.imageBaseURL = (String) map.get("base_url");
            this.backdropSizes = (List<String>) map.get("backdrop_sizes");
            this.posterSizes = (List<String>) map.get("poster_sizes");
            this.logoSizes = (List<String>) map.get("logo_sizes");
            this.profileSizes = (List<String>) map.get("profile_sizes");
        }
    }

    // /**
    // * @param changeKeys {@link List}<String>
    // */
    // @JsonSetter("change_keys")
    // public void setChangeKeys(final List<String> changeKeys)
    // {
    // this.changeKeys = changeKeys;
    // }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
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
