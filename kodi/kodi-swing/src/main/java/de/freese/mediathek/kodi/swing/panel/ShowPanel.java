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
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.GbcBuilder;
import de.freese.mediathek.kodi.swing.action.EditShowGenresAction;
import de.freese.mediathek.kodi.swing.controller.ShowController;
import de.freese.mediathek.utils.cache.ResourceCache;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public class ShowPanel extends AbstractPanel
{
    private final ShowController controller;

    public ShowPanel(final ApplicationContext applicationContext)
    {
        super(applicationContext);

        this.controller = new ShowController(applicationContext.getBean(ResourceCache.class));
    }

    /**
     * @see Panel#reload()
     */
    @Override
    public void reload()
    {
        getController().clearShows();

        SwingWorker<List<Show>, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected List<Show> doInBackground() throws Exception
            {
                MediaService service = getBean(MediaService.class);

                return service.getShows();
            }

            /**
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done()
            {
                try
                {
                    getController().setShows(get());
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
        getController().bindShowTable(table);
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
        label = new JLabel("TvDb Id:");
        detailPanel.add(label, new GbcBuilder(0, 1));
        label = new JLabel();
        getController().bindTvDbIdLabel(label);
        detailPanel.add(label, new GbcBuilder(1, 1));

        // Details Banner
        label = new JLabel();
        getController().bindBannerLabel(label);
        detailPanel.add(label, new GbcBuilder(0, 2).gridWidth(2).weightX(1.0D).fillHorizontal().anchorCenter());

        rightPanel.add(detailPanel, new GbcBuilder(0, 0).weightX(1.0D).fillHorizontal());

        // Genres
        JButton button = new JButton(new EditShowGenresAction(getApplicationContext(), getController()));
        rightPanel.add(button, new GbcBuilder(0, 1));

        // Push all up.
        rightPanel.add(Box.createGlue(), new GbcBuilder(0, 2).fillBoth());

        component.add(splitPane, BorderLayout.CENTER);
    }

    private ShowController getController()
    {
        return this.controller;
    }
}
