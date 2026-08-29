package de.freese.mediathek.services.themoviedb.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.NonNull;

/**
 * @author Thomas Freese
 * @since 24.04.2014
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Search implements Iterable<Movie> {
    private List<Movie> results;

    public List<Movie> getResults() {
        return List.copyOf(results);
    }

    @Override
    public @NonNull Iterator<Movie> iterator() {
        if (results != null) {
            return results.iterator();
        }

        return Collections.emptyIterator();
    }

    public void setResults(final List<Movie> results) {
        this.results = List.copyOf(results);
    }
}
