// Created: 27.04.2014
package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country implements Comparable<Country> {
    /**
     * iso_3166_1
     */
    private String iso31661;
    /**
     * name
     */
    private String name;

    @Override
    public int compareTo(final Country o) {
        return this.name.compareTo(o.getName());
    }

    public String getIso31661() {
        return this.iso31661;
    }

    public String getName() {
        return this.name;
    }

    @JsonSetter("iso_3166_1")
    public void setIso31661(final String iso31661) {
        this.iso31661 = iso31661;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Country [iso_3166_1=");
        builder.append(this.iso31661);
        builder.append(", name=");
        builder.append(this.name);
        builder.append("]");

        return builder.toString();
    }
}
