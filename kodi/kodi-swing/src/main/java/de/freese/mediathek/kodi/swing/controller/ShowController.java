// Created: 27.12.22
package de.freese.mediathek.kodi.swing.controller;

import java.util.List;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.view.ShowView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public class ShowController extends AbstractShowAndMovieController<Show, ShowView>
{
    public ShowController(ApplicationContext applicationContext)
    {
        super(applicationContext);
    }

    @Override
    protected List<Genre> getEntityGenres(final Show entity)
    {
        return getMediaService().getShowGenres(entity.getPk());
    }

    @Override
    protected String getImageUrl(Show show)
    {
        String url = StringUtils.substringBetween(show.getBanner(), "preview=\"", "\">");

        if (url.contains("\""))
        {
            url = url.substring(0, url.indexOf('"'));
        }

        // url = StringUtils.replace(url, "t/p/w500", "t/p/w92");

        if (url.isBlank())
        {
            url = StringUtils.substringBetween(show.getBanner(), ">", "<");
        }

        return url;
    }

    @Override
    protected List<Show> loadEntities()
    {
        return getMediaService().getShows();
    }

    @Override
    protected void updateEntityGenres(final Show entity, final int[] newGenreIDs)
    {
        String newGenres = getMediaService().updateShowGenres(entity.getPk(), newGenreIDs);
        entity.setGenres(newGenres);
    }
}
