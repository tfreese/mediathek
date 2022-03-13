// Created: 16.09.2014
package de.freese.mediathek.kodi.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.freese.mediathek.kodi.api.MediaDAO;
import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementierung für den Service.
 *
 * @author Thomas Freese
 */
public class MediaServiceImpl implements MediaService
{
    /**
     *
     */
    private final MediaDAO mediaDAO;

    /**
     * Erstellt ein neues {@link MediaServiceImpl} Object.
     *
     * @param mediaDAO {@link MediaDAO}
     */
    public MediaServiceImpl(final MediaDAO mediaDAO)
    {
        super();

        this.mediaDAO = Objects.requireNonNull(mediaDAO, "mediaDAO required");
    }

    /**
     * @see de.freese.mediathek.kodi.api.MediaService#getGenreMovies(int)
     */
    @Override
    public List<Movie> getGenreMovies(final int genreID)
    {
        List<Movie> movies = getMediaDAO().getGenreMovies(genreID);
        Collections.sort(movies);

        return movies;
    }

    /**
     * @see de.freese.mediathek.kodi.api.MediaService#getGenreShows(int)
     */
    @Override
    public List<Show> getGenreShows(final int genreID)
    {
        List<Show> shows = getMediaDAO().getGenreShows(genreID);
        Collections.sort(shows);

        return shows;
    }

    /**
     * @see de.freese.mediathek.kodi.api.MediaService#getGenres()
     */
    @Override
    public List<Genre> getGenres()
    {
        List<Genre> genres = getMediaDAO().getGenres();
        Collections.sort(genres);

        return genres;
    }

    /**
     * @see de.freese.mediathek.kodi.api.MediaService#getMovieGenres(int)
     */
    @Override
    public List<Genre> getMovieGenres(final int movieID)
    {
        List<Genre> genres = getMediaDAO().getMovieGenres(movieID);
        Collections.sort(genres);

        return genres;
    }

    /**
     * @see de.freese.mediathek.kodi.api.MediaService#getMovies()
     */
    @Override
    @Transactional(value = "txManagerVideo", readOnly = true)
    public List<Movie> getMovies()
    {
        List<Movie> movies = getMediaDAO().getMovies();
        Collections.sort(movies);
        // Collections.sort(movies, new MovieSetIDYearComparator());

        return movies;
    }

    /**
     * @see de.freese.mediathek.kodi.api.MediaService#getShowGenres(int)
     */
    @Override
    public List<Genre> getShowGenres(final int showID)
    {
        List<Genre> genres = getMediaDAO().getShowGenres(showID);
        Collections.sort(genres);

        return genres;
    }

    /**
     * @see de.freese.mediathek.kodi.api.MediaService#getShows()
     */
    @Override
    @Transactional(value = "txManagerVideo", readOnly = true)
    public List<Show> getShows()
    {
        List<Show> shows = getMediaDAO().getShows();
        Collections.sort(shows);

        return shows;
    }

    /**
     * @see de.freese.mediathek.kodi.api.MediaService#updateMovieGenres(int, int[])
     */
    @Override
    @Transactional("txManagerVideo")
    public String updateMovieGenres(final int movieID, final int... genreIDs)
    {
        if (genreIDs == null || genreIDs.length == 0)
        {
            return null;
        }

        getMediaDAO().deleteMovieGenres(movieID);

        for (int genreID : genreIDs)
        {
            getMediaDAO().insertMovieGenre(movieID, genreID);
        }

        return getMediaDAO().updateMovieGenres(movieID);
    }

    /**
     * @see de.freese.mediathek.kodi.api.MediaService#updateShowGenres(int, int[])
     */
    @Override
    @Transactional("txManagerVideo")
    public String updateShowGenres(final int showID, final int... genreIDs)
    {
        if (genreIDs == null || genreIDs.length == 0)
        {
            return null;
        }

        getMediaDAO().deleteShowGenres(showID);

        for (int genreID : genreIDs)
        {
            getMediaDAO().insertShowGenre(showID, genreID);
        }

        return getMediaDAO().updateShowGenres(showID);
    }

    /**
     * @return {@link MediaDAO}
     */
    protected MediaDAO getMediaDAO()
    {
        return this.mediaDAO;
    }
}
