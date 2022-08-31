// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.beans;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.SwingWorker;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.list.SelectionInList;
import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Model;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.KodiSwingClient;
import org.springframework.context.ApplicationContext;

/**
 * {@link PresentationModel} der {@link Genre}.
 *
 * @author Thomas Freese
 */
public class GenreModel extends PresentationModel<BeanAdapter<Genre>>
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 2574749005329631553L;
    /**
     *
     */
    private final ApplicationContext applicationContext;
    /**
     *
     */
    private final SelectionInList<Genre> genreSelection;
    /**
     *
     */
    private final SelectionInList<Movie> movieSelection;
    /**
     *
     */
    private final SelectionInList<Show> showSelection;

    /**
     * Erstellt ein neues {@link GenreModel} Object.
     *
     * @param applicationContext {@link ApplicationContext}
     */
    public GenreModel(final ApplicationContext applicationContext)
    {
        super();

        this.applicationContext = applicationContext;
        this.genreSelection = new SelectionInList<>();
        this.movieSelection = new SelectionInList<>();
        this.showSelection = new SelectionInList<>();
    }

    /**
     * @return {@link SelectionInList}<Genre>
     */
    public SelectionInList<Genre> getGenreSelection()
    {
        return this.genreSelection;
    }

    /**
     * @return {@link SelectionInList}<Movie>
     */
    public SelectionInList<Movie> getMovieSelection()
    {
        return this.movieSelection;
    }

    /**
     * @return {@link Genre}
     */
    public Genre getSelectedGenre()
    {
        return getGenreSelection().getSelection();
    }

    /**
     * @return {@link SelectionInList}<Show>
     */
    public SelectionInList<Show> getShowSelection()
    {
        return this.showSelection;
    }

    /**
     * @see com.jgoodies.binding.PresentationModel#setBean(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setBean(final BeanAdapter<Genre> newBean)
    {
        super.setBean(newBean);

        this.showSelection.setList(null);
        this.movieSelection.setList(null);

        if (newBean == null)
        {
            return;
        }

        // Genre Laden
        loadGenres(() ->
        {
            Genre genre = getGenreSelection().getSelection();

            if (genre == null)
            {
                return null;
            }

            MediaService service = GenreModel.this.applicationContext.getBean(MediaService.class);

            List<Show> shows = service.getGenreShows(genre.getPK());
            List<Movie> movies = service.getGenreMovies(genre.getPK());

            List<List<? extends Model>> results = new ArrayList<>();
            results.add(shows);
            results.add(movies);

            return results;
        }, results ->
        {
            GenreModel.this.showSelection.setList((List<Show>) results.get(0));
            GenreModel.this.movieSelection.setList((List<Movie>) results.get(1));
        });

        // SwingWorker<List<List<?>>, Void> worker = new SwingWorker<List<List<?>>, Void>()
        // {
        // /**
        // * @see javax.swing.SwingWorker#doInBackground()
        // */
        // @Override
        // protected List<List<?>> doInBackground() throws Exception
        // {
        // Genre genre = getGenreSelection().getSelection();
        //
        // if (genre == null)
        // {
        // return null;
        // }
        //
        // IXbmcService service = GenreModel.this.applicationContext.getBean(IXbmcService.class);
        //
        // List<Show> shows = service.getGenreShows(genre.getPK());
        // List<Movie> movies = service.getGenreMovies(genre.getPK());
        //
        // List<List<?>> results = new ArrayList<>();
        // results.add(shows);
        // results.add(movies);
        //
        // return results;
        // }
        //
        // /**
        // * @see javax.swing.SwingWorker#done()
        // */
        // @SuppressWarnings("unchecked")
        // @Override
        // protected void done()
        // {
        // try
        // {
        // List<List<?>> results = get();
        //
        // GenreModel.this.showSelection.setList((List<Show>) results.get(0));
        // GenreModel.this.movieSelection.setList((List<Movie>) results.get(1));
        // }
        // catch (Exception ex)
        // {
        // KodiSwingClient.LOGGER.error(ex.getMessage(), ex);
        // }
        // }
        // };
        // worker.execute();
    }

    /**
     * @param genres {@link List}
     */
    public void setList(final List<Genre> genres)
    {
        getGenreSelection().setList(genres);

        if ((genres != null) && !genres.isEmpty() && !getGenreSelection().isEmpty())
        {
            getGenreSelection().setSelectionIndex(0);
        }
    }

    /**
     * @param <B> Typ für SwingWorker#doInBackground
     * @param backgroundSupplier {@link Supplier}
     * @param doneConsumer {@link Consumer}
     */
    private <B> void loadGenres(final Supplier<B> backgroundSupplier, final Consumer<B> doneConsumer)
    {
        SwingWorker<B, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected B doInBackground() throws Exception
            {
                return backgroundSupplier.get();
            }

            /**
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done()
            {
                try
                {
                    doneConsumer.accept(get());
                }
                catch (Exception ex)
                {
                    KodiSwingClient.LOGGER.error(ex.getMessage(), ex);
                }
            }
        };
        worker.execute();
    }
}
