// Created: 16.09.2014
package de.freese.mediathek.kodi.swing.panel;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
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

import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.GbcBuilder;
import de.freese.mediathek.kodi.swing.components.list.DefaultListListModel;
import de.freese.mediathek.kodi.swing.components.list.MovieListCellRenderer;
import de.freese.mediathek.kodi.swing.components.list.ShowListCellRenderer;
import de.freese.mediathek.kodi.swing.controller.GenreController;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public class GenrePanel extends AbstractPanel
{
    private final GenreController controller;

    public GenrePanel(final ApplicationContext applicationContext)
    {
        super(applicationContext);

        this.controller = new GenreController(applicationContext);
    }

    @Override
    public void reload()
    {
        getController().clearGenres();

        SwingWorker<List<Genre>, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected List<Genre> doInBackground() throws Exception
            {
                MediaService service = getBean(MediaService.class);

                return service.getGenres();
            }

            /**
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done()
            {
                try
                {
                    getController().setGenres(get());
                }
                catch (Exception ex)
                {
                    getLogger().error(ex.getMessage(), ex);
                }
            }
        };
        worker.execute();
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

        JTable table = new JTable();
        getController().bindGenreTable(table);
        table.getColumnModel().getColumn(1).setMinWidth(50);
        table.getColumnModel().getColumn(1).setMaxWidth(50);
        table.getColumnModel().getColumn(2).setMinWidth(50);
        table.getColumnModel().getColumn(2).setMaxWidth(50);
        JScrollPane scrollPane = new JScrollPane(table);
        splitPane.setLeftComponent(scrollPane);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        splitPane.setRightComponent(panel);

        JList<Movie> jListMovies = new JList<>(new DefaultListListModel<>());
        getController().bindMovieSelection(jListMovies);
        jListMovies.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jListMovies.setCellRenderer(new MovieListCellRenderer());
        scrollPane = new JScrollPane(jListMovies);
        scrollPane.setBorder(new TitledBorder("Filme"));
        panel.add(scrollPane, new GbcBuilder(0, 0).fillBoth());

        JList<Show> jListShows = new JList<>(new DefaultListListModel<>());
        getController().bindShowSelection(jListShows);
        jListShows.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jListShows.setCellRenderer(new ShowListCellRenderer());
        scrollPane = new JScrollPane(jListShows);
        scrollPane.setBorder(new TitledBorder("Serien"));
        panel.add(scrollPane, new GbcBuilder(1, 0).fillBoth());

        // Push all up.
        component.add(splitPane, BorderLayout.CENTER);
    }

    private GenreController getController()
    {
        return this.controller;
    }
}
