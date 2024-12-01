// Created: 24.04.2014
package de.freese.mediathek.services.themoviedb.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Search implements Iterable<Movie> {
    private List<Movie> results;

    public List<Movie> getResults() {
        return List.copyOf(results);
    }

    @Override
    public Iterator<Movie> iterator() {
        if (results != null) {
            return results.iterator();
        }

        return Collections.emptyIterator();
    }

    public void setResults(final List<Movie> results) {
        this.results = List.copyOf(results);
    }
}
