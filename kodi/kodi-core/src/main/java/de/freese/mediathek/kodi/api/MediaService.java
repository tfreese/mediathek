/**
 * Created: 16.09.2014
 */

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
public interface MediaService
{
    /**
     * Liefert alle Filme des Genres.
     * 
     * @param genreID int
     * @return {@link List}
     */
    public List<Movie> getGenreMovies(int genreID);

    /**
     * Liefert alle Genres in alphabetischer Reihenfolge.
     * 
     * @return {@link List}
     */
    public List<Genre> getGenres();

    /**
     * Liefert alle Serien des Genres.
     * 
     * @param genreID int
     * @return {@link List}
     */
    public List<Show> getGenreShows(int genreID);

    /**
     * Liefert alle Genres des Films in alphabetischer Reihenfolge.
     * 
     * @param movieID int
     * @return {@link List}
     */
    public List<Genre> getMovieGenres(int movieID);

    /**
     * Liefert alle Filme in alphabetischer Reihenfolge.
     * 
     * @return {@link List}
     */
    public List<Movie> getMovies();

    /**
     * Liefert alle Genres der Serie in alphabetischer Reihenfolge.
     * 
     * @param showID int
     * @return {@link List}
     */
    public List<Genre> getShowGenres(int showID);

    /**
     * Liefert alle Serien in alphabetischer Reihenfolge.
     * 
     * @return {@link List}
     */
    public List<Show> getShows();

    /**
     * Aktualisiert die Genres eines Films.
     * 
     * @param movieID int
     * @param genreIDs int[]
     * @return String
     */
    public String updateMovieGenres(int movieID, int...genreIDs);

    /**
     * Aktualisiert die Genres einer Serie.
     * 
     * @param showID int
     * @param genreIDs int[]
     * @return String
     */
    public String updateShowGenres(int showID, int...genreIDs);
}
