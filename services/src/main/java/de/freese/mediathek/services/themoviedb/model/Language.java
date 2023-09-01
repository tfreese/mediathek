// Created: 27.04.2014
package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Language implements Comparable<Language> {
    /**
     * iso_639_1
     */
    private String iso6391;
    /**
     * name
     */
    private String name;

    @Override
    public int compareTo(final Language o) {
        return this.name.compareTo(o.getName());
    }

    public String getIso6391() {
        return this.iso6391;
    }

    public String getName() {
        return this.name;
    }

    @JsonSetter("iso_639_1")
    public void setIso6391(final String iso6391) {
        this.iso6391 = iso6391;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Language [iso_639_1=");
        builder.append(this.iso6391);
        builder.append(", name=");
        builder.append(this.name);
        builder.append("]");

        return builder.toString();
    }
}
