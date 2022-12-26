// Created: 16.09.2014
package de.freese.mediathek.kodi.swing.panel;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
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

import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.swing.GbcBuilder;
import de.freese.mediathek.kodi.swing.action.EditMovieGenresAction;
import de.freese.mediathek.kodi.swing.controller.MovieController;
import de.freese.mediathek.utils.cache.ResourceCache;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public class MoviePanel extends AbstractPanel
{
    private final MovieController controller;

    public MoviePanel(final ApplicationContext applicationContext)
    {
        super(applicationContext);

        this.controller = new MovieController(applicationContext.getBean(ResourceCache.class));
    }

    /**
     * @see Panel#reload()
     */
    @Override
    public void reload()
    {
        getController().clearMovies();

        SwingWorker<List<Movie>, Void> worker = new SwingWorker<>()
        {
            /**
             * @see SwingWorker#doInBackground()
             */
            @Override
            protected List<Movie> doInBackground() throws Exception
            {
                MediaService service = getBean(MediaService.class);

                return service.getMovies();
            }

            /**
             * @see SwingWorker#done()
             */
            @Override
            protected void done()
            {
                try
                {
                    getController().setMovies(get());
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
     * @see AbstractPanel#buildPanel(JComponent)
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
        leftPanel.add(label, new GbcBuilder(0, 0));

        JTextField textFieldFilter = new JTextField();
        getController().bindTextFieldFilter(textFieldFilter);
        leftPanel.add(textFieldFilter, new GbcBuilder(1, 0).fillHorizontal());

        JTable table = new JTable();
        getController().bindMovieTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        table.getColumnModel().getColumn(0).setMinWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        leftPanel.add(scrollPane, new GbcBuilder(0, 1).gridWidth(2).fillBoth());
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        splitPane.setRightComponent(rightPanel);

        // Details
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new GridBagLayout());
        detailPanel.setBorder(new TitledBorder("Details"));

        // Details Genres
        label = new JLabel("Genres:");
        detailPanel.add(label, new GbcBuilder(0, 0));
        label = new JLabel();
        getController().bindGenreLabel(label);
        detailPanel.add(label, new GbcBuilder(1, 0));

        // Details TvDb ID
        label = new JLabel("ImDb Id:");
        detailPanel.add(label, new GbcBuilder(0, 1));
        label = new JLabel();
        getController().bindImDbIdLabel(label);
        detailPanel.add(label, new GbcBuilder(1, 1));

        // Details Poster
        label = new JLabel();
        getController().bindPosterLabel(label);
        detailPanel.add(label, new GbcBuilder(0, 2).gridWidth(2).weightX(1.0D).fillHorizontal().anchorCenter());

        rightPanel.add(detailPanel, new GbcBuilder(0, 0).weightX(1.0D).fillHorizontal());

        // Genres
        JButton button = new JButton(new EditMovieGenresAction(getApplicationContext(), getController()));
        rightPanel.add(button, new GbcBuilder(0, 1));

        // Push all up.
        rightPanel.add(Box.createGlue(), new GbcBuilder(0, 2).fillBoth());

        component.add(splitPane, BorderLayout.CENTER);
    }

    private MovieController getController()
    {
        return this.controller;
    }
}
