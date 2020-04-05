/**
 * Created: 13.09.2014
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
public interface MediaDAO
{
	/**
	 * Löscht alle Genres des Films.
	 * 
	 * @param movieID int
	 */
	public void deleteMovieGenres(int movieID);

	/**
	 * Löscht alle Genres der Serie.
	 * 
	 * @param showID int
	 */
	public void deleteShowGenres(int showID);

	/**
	 * Liefert alle Filme des Genres.
	 * 
	 * @param genreID int
	 * @return {@link List}
	 */
	public List<Movie> getGenreMovies(int genreID);

	/**
	 * Liefert alle Genres.
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
	 * Liefert alle Genres des Films.
	 * 
	 * @param movieID int
	 * @return {@link List}
	 */
	public List<Genre> getMovieGenres(int movieID);

	/**
	 * Liefert alle Filme.
	 * 
	 * @return {@link List}
	 */
	public List<Movie> getMovies();

	/**
	 * Liefert alle Genres der Serie.
	 * 
	 * @param showID int
	 * @return {@link List}
	 */
	public List<Genre> getShowGenres(int showID);

	/**
	 * Liefert alle Serien.
	 * 
	 * @return {@link List}
	 */
	public List<Show> getShows();

	/**
	 * Erzeugt das Genre des Films.
	 * 
	 * @param movieID int
	 * @param genreID int
	 */
	public void insertMovieGenre(int movieID, int genreID);

	/**
	 * Erzeugt das Genre der Serie.
	 * 
	 * @param showID int
	 * @param genreID int
	 */
	public void insertShowGenre(int showID, int genreID);

	/**
	 * Aktualisiert die Genre-Beschreibung des Films.
	 * 
	 * @param movieID int
	 * @return String
	 */
	public String updateMovieGenres(int movieID);

	/**
	 * Aktualisiert die Genre-Beschreibung der Serie.
	 * 
	 * @param showID int
	 * @return String
	 */
	public String updateShowGenres(int showID);
}
