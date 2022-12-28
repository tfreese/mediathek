// Created: 28.12.22
package de.freese.mediathek.kodi.swing.controller;

import de.freese.mediathek.kodi.model.Show;

/**
 * @author Thomas Freese
 */
public class ShowController extends AbstractShowAndMovieController<Show>
{
    @Override
    public void setSelected(final Show entity)
    {
        getView().getGenreLabel().setText(entity.getGenres());
        getView().getIdLabel().setText(entity.getTvDbId());

        setImageIcon(entity);
    }
}
