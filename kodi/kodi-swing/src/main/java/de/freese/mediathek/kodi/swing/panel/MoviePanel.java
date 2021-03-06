/**
 * Created: 16.09.2014
 */
package de.freese.mediathek.kodi.swing.panel;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.util.Collections;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.springframework.context.ApplicationContext;
import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.swing.GBCBuilder;
import de.freese.mediathek.kodi.swing.action.EditMovieGenresAction;
import de.freese.mediathek.kodi.swing.beans.MovieBean;
import de.freese.mediathek.kodi.swing.beans.MovieModel;
import de.freese.mediathek.kodi.swing.components.table.MovieTableAdapter;

/**
 * {@link IPanel} der Filme.
 *
 * @author Thomas Freese
 */
public class MoviePanel extends AbstractPanel
{
    /**
     * @author Thomas Freese
     */
    private class MovieSelectionListener implements ListSelectionListener
    {
        /**
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        @Override
        public void valueChanged(final ListSelectionEvent e)
        {
            getMovieModel().setBean(null);

            if (e.getValueIsAdjusting())
            {
                return;
            }

            Movie selectedMovie = getMovieModel().getSelectedMovie();

            if (selectedMovie == null)
            {
                return;
            }

            getMovieModel().setBean(new MovieBean(selectedMovie));
        }
    }

    /**
     *
     */
    private final MovieModel movieModel;

    /**
     * Erstellt ein neues {@link MoviePanel} Object.
     *
     * @param applicationContext {@link ApplicationContext}
     */
    public MoviePanel(final ApplicationContext applicationContext)
    {
        super(applicationContext);

        this.movieModel = new MovieModel();

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
        splitPane.setDividerLocation(500);

        // Liste
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());

        JLabel label = new JLabel("Filter:");
        leftPanel.add(label, new GBCBuilder(0, 0));

        JTextField textFieldFilter = new JTextField();
        getMovieModel().bindTextFieldFilter(textFieldFilter);
        leftPanel.add(textFieldFilter, new GBCBuilder(1, 0).fillHorizontal());

        JTable jTable = new JTable(new MovieTableAdapter());
        getMovieModel().bindMovieTable(jTable, new MovieSelectionListener());
        jTable.getColumnModel().getColumn(0).setMinWidth(50);
        jTable.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scrollPane = new JScrollPane(jTable);
        leftPanel.add(scrollPane, new GBCBuilder(0, 1).gridwidth(2).fillBoth());
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        splitPane.setRightComponent(rightPanel);

        // Details
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new GridBagLayout());
        detailPanel.setBorder(new TitledBorder("Details"));

        // Details Poster
        label = new JLabel();
        getMovieModel().bindPosterLabel(label);
        detailPanel.add(label, new GBCBuilder(0, 0).gridheight(5).weighty(1.0D).fillVertical());

        // Details Genres
        label = new JLabel("Genres:");
        detailPanel.add(label, new GBCBuilder(1, 0));
        label = new JLabel();
        getMovieModel().bindGenreLabel(label);
        detailPanel.add(label, new GBCBuilder(2, 0).fillHorizontal());

        // Details IMDB_ID
        label = new JLabel("IMDB_ID:");
        detailPanel.add(label, new GBCBuilder(1, 1));
        label = new JLabel();
        getMovieModel().bindIMDBIDLabel(label);
        detailPanel.add(label, new GBCBuilder(2, 1));

        rightPanel.add(detailPanel, new GBCBuilder(0, 0).weightx(1.0D).fillHorizontal());

        // Genres
        JButton button = new JButton(new EditMovieGenresAction(getApplicationContext(), getMovieModel()));
        rightPanel.add(button, new GBCBuilder(0, 1));
        // JPanel genrePanel = new JPanel();
        // genrePanel.setBorder(new TitledBorder("Genres"));
        // panel.add(genrePanel, new GBCBuilder(0, 1).weightx(1.0D).fillBoth());

        // Alles nach oben drücken.
        rightPanel.add(Box.createGlue(), new GBCBuilder(0, 2).fillBoth());

        component.add(splitPane, BorderLayout.CENTER);
    }

    /**
     * @return {@link MovieModel}
     */
    private MovieModel getMovieModel()
    {
        return this.movieModel;
    }

    /**
     * @see de.freese.mediathek.kodi.swing.panel.IPanel#reload()
     */
    @Override
    public void reload()
    {
        List<Movie> emptyList = Collections.emptyList();
        getMovieModel().setList(emptyList);

        SwingWorker<List<Movie>, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected List<Movie> doInBackground() throws Exception
            {
                MediaService service = getBean(MediaService.class);
                List<Movie> movies = service.getMovies();

                return movies;
            }

            /**
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done()
            {
                try
                {
                    getMovieModel().setList(get());
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
