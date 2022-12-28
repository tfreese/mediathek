// Created: 28.12.22
package de.freese.mediathek.kodi.swing.controller;

import de.freese.mediathek.kodi.model.Movie;

/**
 * @author Thomas Freese
 */
public class MovieController extends AbstractShowAndMovieController<Movie>
{
    @Override
    public void setSelected(final Movie entity)
    {
        getView().getGenreLabel().setText(entity.getGenres());
        getView().getIdLabel().setText(entity.getImDbId());

        setImageIcon(entity);
    }
}
