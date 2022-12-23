// Created: 16.09.2014
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

import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.GbcBuilder;
import de.freese.mediathek.kodi.swing.action.EditShowGenresAction;
import de.freese.mediathek.kodi.swing.beans.ShowBean;
import de.freese.mediathek.kodi.swing.beans.ShowModel;
import de.freese.mediathek.kodi.swing.components.table.ShowTableAdapter;
import de.freese.mediathek.utils.cache.ResourceCache;
import org.springframework.context.ApplicationContext;

/**
 * {@link Panel} der Serien.
 *
 * @author Thomas Freese
 */
public class ShowPanel extends AbstractPanel
{
    /**
     * @author Thomas Freese
     */
    private class ShowSelectionListener implements ListSelectionListener
    {
        /**
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        @Override
        public void valueChanged(final ListSelectionEvent e)
        {
            getShowModel().setBean(null);

            if (e.getValueIsAdjusting())
            {
                return;
            }

            Show selectedShow = getShowModel().getSelectedShow();

            if (selectedShow == null)
            {
                return;
            }

            getShowModel().setBean(new ShowBean(selectedShow));

            getLogger().debug(selectedShow.toString());
        }
    }

    private final ShowModel showModel;

    public ShowPanel(final ApplicationContext applicationContext)
    {
        super(applicationContext);

        this.showModel = new ShowModel(applicationContext.getBean(ResourceCache.class));

        // Die ShowSelection als Bean funktioniert nur, wenn die Objekte darin vom Typ Model sind.
        // Dann würde auch kein expliziter ListSelectionListener benötigt werden.
        // this.detailsModel = new PresentationModel<>(this.showSelection);
    }

    /**
     * @see Panel#reload()
     */
    @Override
    public void reload()
    {
        List<Show> emptyList = Collections.emptyList();
        getShowModel().setList(emptyList);

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
                    getShowModel().setList(get());
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
        getShowModel().bindTextFieldFilter(textFieldFilter);
        leftPanel.add(textFieldFilter, new GbcBuilder(1, 0).fillHorizontal());

        JTable jTable = new JTable(new ShowTableAdapter());
        getShowModel().bindShowTable(jTable, new ShowSelectionListener());
        JScrollPane scrollPane = new JScrollPane(jTable);
        jTable.getColumnModel().getColumn(0).setMinWidth(50);
        jTable.getColumnModel().getColumn(0).setMaxWidth(50);

        leftPanel.add(scrollPane, new GbcBuilder(0, 1).gridWidth(2).fillBoth());
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        splitPane.setRightComponent(rightPanel);

        // Details
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new GridBagLayout());
        detailPanel.setBorder(new TitledBorder("Details"));

        // Details Banner
        label = new JLabel();
        getShowModel().bindBannerLabel(label);
        detailPanel.add(label, new GbcBuilder(0, 0).gridWidth(2).weightX(1.0D).fillHorizontal().anchorCenter());

        // Details Genres
        label = new JLabel("Genres:");
        detailPanel.add(label, new GbcBuilder(0, 1));
        label = new JLabel();
        getShowModel().bindGenreLabel(label);
        detailPanel.add(label, new GbcBuilder(1, 1));

        // Details TVDB_ID
        label = new JLabel("TVDB_ID:");
        detailPanel.add(label, new GbcBuilder(0, 2));
        label = new JLabel();
        getShowModel().bindTVDIDLabel(label);
        detailPanel.add(label, new GbcBuilder(1, 2));

        rightPanel.add(detailPanel, new GbcBuilder(0, 0).weightX(1.0D).fillHorizontal());

        // Genres
        JButton button = new JButton(new EditShowGenresAction(getApplicationContext(), getShowModel()));
        rightPanel.add(button, new GbcBuilder(0, 1));
        // JPanel genrePanel = new JPanel();
        // genrePanel.setBorder(new TitledBorder("Genres"));
        // panel.add(genrePanel, new GbcBuilder(0, 1).weightX(1.0D).fillBoth());

        // Alles nach oben drücken.
        rightPanel.add(Box.createGlue(), new GbcBuilder(0, 2).fillBoth());

        component.add(splitPane, BorderLayout.CENTER);
    }

    private ShowModel getShowModel()
    {
        return this.showModel;
    }
}
