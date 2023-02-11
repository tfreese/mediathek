// Created: 28.12.22
package de.freese.mediathek.kodi.swing.service;

import java.util.List;

import org.springframework.context.ApplicationContext;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;

/**
 * @author Thomas Freese
 */
public class GenreService extends AbstractService {
    public GenreService(final ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public List<Movie> getGenreMovies(Genre genre) {
        return getMediaService().getGenreMovies(genre.getPk());
    }

    public List<Show> getGenreShows(Genre genre) {
        return getMediaService().getGenreShows(genre.getPk());
    }

    public List<Genre> load() {
        return getMediaService().getGenres();
    }
}
