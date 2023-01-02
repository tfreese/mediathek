// Created: 28.12.22
package de.freese.mediathek.kodi.swing.controller;

import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.service.ShowService;
import de.freese.mediathek.kodi.swing.view.ShowView;

/**
 * @author Thomas Freese
 */
public class ShowController extends AbstractShowAndMovieController<Show>
{
    public ShowController(final ShowService service, ShowView view)
    {
        super(service, view);
    }

    @Override
    public void setSelected(final Show entity)
    {
        getView().getGenreLabel().setText(entity.getGenres());
        getView().getIdLabel().setText(entity.getTvDbId());

        setImageIcon(entity);
    }
}
