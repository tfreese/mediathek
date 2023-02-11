// Created: 13.09.2014
package de.freese.mediathek.kodi.api;

import java.util.List;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;

/**
 * @author Thomas Freese
 */
public interface MediaDao {
    void deleteMovieGenres(int movieID);

    void deleteShowGenres(int showID);

    List<Movie> getGenreMovies(int genreID);

    List<Show> getGenreShows(int genreID);

    List<Genre> getGenres();

    List<Genre> getMovieGenres(int movieID);

    List<Movie> getMovies();

    List<Genre> getShowGenres(int showID);

    List<Show> getShows();

    void insertMovieGenre(int movieID, int genreID);

    void insertShowGenre(int showID, int genreID);

    String updateMovieGenres(int movieID);

    String updateShowGenres(int showID);
}
