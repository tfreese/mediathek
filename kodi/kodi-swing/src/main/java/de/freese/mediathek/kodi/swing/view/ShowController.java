// Created: 27.12.22
package de.freese.mediathek.kodi.swing.view;

import java.util.List;

import de.freese.mediathek.kodi.model.Show;
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
    public void init(final ShowView view)
    {
        super.init(view);

        //        view.doOnGenres(button -> button.setAction(new EditShowGenresAction(getApplicationContext(),this)));
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

        if (url == null || url.isBlank())
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
}
