/**
 * Created: 16.09.2014
 */
package de.freese.mediathek.kodi.swing.panel;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.springframework.context.ApplicationContext;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.BeanAdapter;
import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.GBCBuilder;
import de.freese.mediathek.kodi.swing.beans.GenreModel;
import de.freese.mediathek.kodi.swing.components.list.MovieListCellRenderer;
import de.freese.mediathek.kodi.swing.components.list.ShowListCellRenderer;
import de.freese.mediathek.kodi.swing.components.table.GenreTableAdapter;

/**
 * {@link IPanel} der Genres.<br>
 * com.jgoodies.jsdl.component.JGComponentFactory
 *
 * @author Thomas Freese
 */
public class GenrePanel extends AbstractPanel
{
    /**
     * @author Thomas Freese
     */
    private class GenreSelectionListener implements ListSelectionListener
    {
        /**
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        @Override
        public void valueChanged(final ListSelectionEvent e)
        {
            getGenreModel().setBean(null);

            if (e.getValueIsAdjusting())
            {
                return;
            }

            // JTable table = (JTable) e.getSource();
            // int row = table.convertRowIndexToModel(e.getFirstIndex());
            Genre selectedGenre = getGenreModel().getSelectedGenre();

            if (selectedGenre == null)
            {
                return;
            }

            getGenreModel().setBean(new BeanAdapter<>(selectedGenre));

            getLogger().debug(selectedGenre.toString());
        }
    }

    /**
     *
     */
    private final GenreModel genreModel;

    /**
     * Erstellt ein neues {@link GenrePanel} Object.
     *
     * @param applicationContext {@link ApplicationContext}
     */
    public GenrePanel(final ApplicationContext applicationContext)
    {
        super(applicationContext);

        this.genreModel = new GenreModel(applicationContext);

        // Die ShowSelection als Bean funktioniert nur, wenn die Objekte darin vom Typ Model sind.
        // Dann würde auch kein expliziter ListSelectionListener benötigt werden.
        // this.detailsModel = new PresentationModel<>(this.showSelection);
    }

    /**
     * @see de.freese.mediathek.kodi.swing.panel.AbstractPanel#buildPanel(javax.swing.JComponent)
     */
    @Override
    protected void buildPanel(final JComponent component)
    {
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(260);

        // Tabelle
        // JTable table = BasicComponentFactory.createTable(getGenreModel().getGenreSelection(), new GenreTableAdapter());
        JTable table = new JTable(new GenreTableAdapter());
        Bindings.bind(table, getGenreModel().getGenreSelection());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new GenreSelectionListener());
        table.getColumnModel().getColumn(1).setMinWidth(50);
        table.getColumnModel().getColumn(1).setMaxWidth(50);
        table.getColumnModel().getColumn(2).setMinWidth(50);
        table.getColumnModel().getColumn(2).setMaxWidth(50);
        JScrollPane scrollPane = new JScrollPane(table);
        splitPane.setLeftComponent(scrollPane);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        splitPane.setRightComponent(panel);

        // Filme
        // JList<Movie> jListMovies = BasicComponentFactory.createList(getGenreModel().getMovieSelection());
        JList<Movie> jListMovies = new JList<>();
        Bindings.bind(jListMovies, getGenreModel().getMovieSelection());
        jListMovies.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jListMovies.setCellRenderer(new MovieListCellRenderer());
        scrollPane = new JScrollPane(jListMovies);
        scrollPane.setBorder(new TitledBorder("Filme"));
        panel.add(scrollPane, new GBCBuilder(0, 0).fillBoth());

        // Serien
        // JList<Show> jListShows = BasicComponentFactory.createList(getGenreModel().getShowSelection());
        JList<Show> jListShows = new JList<>();
        Bindings.bind(jListShows, getGenreModel().getShowSelection());
        jListShows.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jListShows.setCellRenderer(new ShowListCellRenderer());
        scrollPane = new JScrollPane(jListShows);
        scrollPane.setBorder(new TitledBorder("Serien"));
        panel.add(scrollPane, new GBCBuilder(1, 0).fillBoth());

        // Alles nach oben drücken.
        // panel.add(Box.createGlue(), new GBCBuilder(0, 2).fillBoth());
        component.add(splitPane, BorderLayout.CENTER);
    }

    /**
     * @return {@link GenreModel}
     */
    private GenreModel getGenreModel()
    {
        return this.genreModel;
    }

    /**
     * @see de.freese.mediathek.kodi.swing.panel.IPanel#reload()
     */
    @Override
    public void reload()
    {
        List<Genre> emptyList = Collections.emptyList();
        getGenreModel().setList(emptyList);

        SwingWorker<List<Genre>, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected List<Genre> doInBackground() throws Exception
            {
                MediaService service = getBean(MediaService.class);
                List<Genre> genres = service.getGenres();

                return genres;
            }

            /**
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done()
            {
                try
                {
                    getGenreModel().setList(get());
                }
                catch (Exception ex)
                {
                    getLogger().error(null, ex);
                }
            }
        };
        worker.execute();
    }
}
