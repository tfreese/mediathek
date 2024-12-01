// Created: 27.04.2014
package de.freese.mediathek.services.themoviedb.model;

import java.util.Objects;

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
        return name.compareTo(o.getName());
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Language language)) {
            return false;
        }
        
        return Objects.equals(iso6391, language.iso6391) && Objects.equals(name, language.name);
    }

    public String getIso6391() {
        return iso6391;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(iso6391, name);
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
        final StringBuilder builder = new StringBuilder();
        builder.append("Language [iso_639_1=");
        builder.append(iso6391);
        builder.append(", name=");
        builder.append(name);
        builder.append("]");

        return builder.toString();
    }
}
