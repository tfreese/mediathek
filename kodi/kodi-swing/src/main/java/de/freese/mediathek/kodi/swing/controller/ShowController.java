// Created: 28.12.22
package de.freese.mediathek.kodi.swing.controller;

import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.service.ShowService;

/**
 * @author Thomas Freese
 */
public class ShowController extends AbstractShowAndMovieController<Show, ShowService>
{
    public ShowController(final ShowService service)
    {
        super(service);
    }

    @Override
    public void setSelected(final Show entity)
    {
        getView().getGenreLabel().setText(entity.getGenres());
        getView().getIdLabel().setText(entity.getTvDbId());

        setImageIcon(entity);
    }
}
