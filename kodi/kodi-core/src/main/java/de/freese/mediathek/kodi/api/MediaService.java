// Created: 16.09.2014
package de.freese.mediathek.kodi.api;

import java.util.List;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;

/**
 * @author Thomas Freese
 */
public interface MediaService {
    List<Movie> getGenreMovies(int genreID);

    List<Show> getGenreShows(int genreID);

    List<Genre> getGenres();

    List<Genre> getMovieGenres(int movieID);

    List<Movie> getMovies();

    List<Genre> getShowGenres(int showID);

    List<Show> getShows();

    String updateMovieGenres(int movieID, int... genreIDs);

    String updateShowGenres(int showID, int... genreIDs);
}
