// Created: 26.12.22
package de.freese.mediathek.kodi.swing.controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;

import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Model;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.components.list.DefaultListListModel;
import de.freese.mediathek.kodi.swing.components.table.GenreTableModel;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public class GenreController extends AbstractController
{
    private final ApplicationContext applicationContext;

    private JTable genreTable;

    private JList<Movie> listMovies;

    private JList<Show> listShows;

    public GenreController(ApplicationContext applicationContext)
    {
        super(null);

        this.applicationContext = applicationContext;
    }

    public void bindGenreTable(JTable genreTable)
    {
        this.genreTable = genreTable;

        GenreTableModel genreTableModel = new GenreTableModel();
        this.genreTable.setModel(genreTableModel);

        this.genreTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.genreTable.getSelectionModel().addListSelectionListener(event ->
        {
            if (event.getValueIsAdjusting())
            {
                return;
            }

            int viewRow = this.genreTable.getSelectedRow();

            if (viewRow == -1)
            {
                updateSelectedGenre(null);
                return;
            }

            int modelRow = this.genreTable.convertRowIndexToModel(viewRow);

            Genre genre = genreTableModel.getObjectAt(modelRow);

            updateSelectedGenre(genre);

            getLogger().debug("{}", genre);
        });
    }

    public void bindMovieSelection(JList<Movie> listMovies)
    {
        this.listMovies = listMovies;
    }

    public void bindShowSelection(JList<Show> listShows)
    {
        this.listShows = listShows;
    }

    public void clearGenres()
    {
        getGenreTableModel().clear();
    }

    public Genre getSelectedGenre()
    {
        int viewRow = this.genreTable.getSelectedRow();

        if (viewRow < 0)
        {
            return null;
        }

        int modelRow = this.genreTable.convertRowIndexToModel(viewRow);

        return getGenreTableModel().getObjectAt(modelRow);
    }

    public void setGenres(List<Genre> genres)
    {
        getGenreTableModel().addAll(genres);

        if (genres != null && !genres.isEmpty())
        {
            this.genreTable.setRowSelectionInterval(0, 0);
        }
    }

    private GenreTableModel getGenreTableModel()
    {
        return (GenreTableModel) this.genreTable.getModel();
    }

    private void updateSelectedGenre(Genre genre)
    {
        ((DefaultListListModel) this.listShows.getModel()).clear();
        ((DefaultListListModel) this.listMovies.getModel()).clear();

        if (genre == null)
        {
            return;
        }

        SwingWorker<List<List<? extends Model>>, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected List<List<? extends Model>> doInBackground() throws Exception
            {
                MediaService mediaService = applicationContext.getBean(MediaService.class);

                List<Show> shows = mediaService.getGenreShows(genre.getPk());
                List<Movie> movies = mediaService.getGenreMovies(genre.getPk());

                List<List<? extends Model>> results = new ArrayList<>();
                results.add(shows);
                results.add(movies);

                return results;
            }

            /**
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done()
            {
                try
                {
                    List<List<? extends Model>> results = get();

                    ((DefaultListListModel) listShows.getModel()).addAll(results.get(0));
                    ((DefaultListListModel) listMovies.getModel()).addAll(results.get(1));
                }
                catch (Exception ex)
                {
                    getLogger().error(ex.getMessage(), ex);
                }
            }
        };
        worker.execute();
    }
}
