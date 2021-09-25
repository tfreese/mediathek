// Created: 13.09.2014
package de.freese.mediathek.kodi.api;

import java.util.List;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;

/**
 * Interface für das DAO.
 *
 * @author Thomas Freese
 */
public interface MediaDAO
{
    /**
     * Löscht alle Genres des Films.
     *
     * @param movieID int
     */
    void deleteMovieGenres(int movieID);

    /**
     * Löscht alle Genres der Serie.
     *
     * @param showID int
     */
    void deleteShowGenres(int showID);

    /**
     * Liefert alle Filme des Genres.
     *
     * @param genreID int
     *
     * @return {@link List}
     */
    List<Movie> getGenreMovies(int genreID);

    /**
     * Liefert alle Genres.
     *
     * @return {@link List}
     */
    List<Genre> getGenres();

    /**
     * Liefert alle Serien des Genres.
     *
     * @param genreID int
     *
     * @return {@link List}
     */
    List<Show> getGenreShows(int genreID);

    /**
     * Liefert alle Genres des Films.
     *
     * @param movieID int
     *
     * @return {@link List}
     */
    List<Genre> getMovieGenres(int movieID);

    /**
     * Liefert alle Filme.
     *
     * @return {@link List}
     */
    List<Movie> getMovies();

    /**
     * Liefert alle Genres der Serie.
     *
     * @param showID int
     *
     * @return {@link List}
     */
    List<Genre> getShowGenres(int showID);

    /**
     * Liefert alle Serien.
     *
     * @return {@link List}
     */
    List<Show> getShows();

    /**
     * Erzeugt das Genre des Films.
     *
     * @param movieID int
     * @param genreID int
     */
    void insertMovieGenre(int movieID, int genreID);

    /**
     * Erzeugt das Genre der Serie.
     *
     * @param showID int
     * @param genreID int
     */
    void insertShowGenre(int showID, int genreID);

    /**
     * Aktualisiert die Genre-Beschreibung des Films.
     *
     * @param movieID int
     *
     * @return String
     */
    String updateMovieGenres(int movieID);

    /**
     * Aktualisiert die Genre-Beschreibung der Serie.
     *
     * @param showID int
     *
     * @return String
     */
    String updateShowGenres(int showID);
}
