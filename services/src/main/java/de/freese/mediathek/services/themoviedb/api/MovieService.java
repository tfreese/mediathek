// Created: 26.04.2014
package de.freese.mediathek.services.themoviedb.api;

import de.freese.mediathek.services.themoviedb.model.Casts;
import de.freese.mediathek.services.themoviedb.model.Images;
import de.freese.mediathek.services.themoviedb.model.MovieDetails;
import de.freese.mediathek.services.themoviedb.model.Search;

/**
 * Interface für den Zugriff auf die Movie-API.
 *
 * @author Thomas Freese
 */
public interface MovieService
{
    /**
     * Liefert den Cast des Films, Schauspieler und Crew.
     *
     * @param id int
     *
     * @return {@link Casts}
     */
    Casts casts(int id);

    /**
     * Liefert die Details des Films.
     *
     * @param id int
     *
     * @return {@link MovieDetails}
     */
    MovieDetails details(int id);

    /**
     * Liefert die Bilder des Films, Hintergründe und Poster.
     *
     * @param id int
     *
     * @return {@link Images}
     */
    Images images(int id);

    /**
     * Sucht nach einem Film.
     *
     * @param movie String
     *
     * @return {@link Search}
     */
    Search search(String movie);

    /**
     * Sucht nach einem Film in dem Jahr.
     *
     * @param movie String
     * @param year int
     *
     * @return {@link Search}
     */
    Search search(String movie, int year);
}
