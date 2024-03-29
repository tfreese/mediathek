// Created: 28.12.22
package de.freese.mediathek.kodi.swing.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Model;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.GbcBuilder;
import de.freese.mediathek.kodi.swing.components.list.DefaultListListModel;
import de.freese.mediathek.kodi.swing.components.list.MovieListCellRenderer;
import de.freese.mediathek.kodi.swing.components.list.ShowListCellRenderer;
import de.freese.mediathek.kodi.swing.components.table.GenreTableModel;
import de.freese.mediathek.kodi.swing.controller.Controller;
import de.freese.mediathek.kodi.swing.controller.GenreController;

/**
 * @author Thomas Freese
 */
public class GenreView extends AbstractView {
    private JList<Movie> listMovies;
    private JList<Show> listShows;
    private JTable table;

    public void clear() {
        ((DefaultListListModel<?>) this.listShows.getModel()).clear();
        ((DefaultListListModel<?>) this.listMovies.getModel()).clear();
    }

    public void fill(final List<Genre> data) {
        getTableModel().addAll(data);

        if (data != null && !data.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    @Override
    public Component init(final Controller controller, final ResourceBundle resourceBundle) {
        super.init(controller, resourceBundle);

        final JPanel parentPanel = new JPanel();
        parentPanel.setLayout(new BorderLayout());

        final JButton reloadButton = new JButton(getTranslation("reload"));
        reloadButton.addActionListener(event -> getController().reload());
        parentPanel.add(reloadButton, BorderLayout.NORTH);

        final JSplitPane splitPane = new JSplitPane();
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(500);

        this.table = new JTable(new GenreTableModel());
        this.table.getColumnModel().getColumn(0).setMinWidth(160);
        this.table.getColumnModel().getColumn(1).setMinWidth(60);
        this.table.getColumnModel().getColumn(2).setMinWidth(60);
        this.table.getSelectionModel().addListSelectionListener(event -> {
            if (event.getValueIsAdjusting()) {
                return;
            }

            getController().clear();

            final int viewRow = this.table.getSelectedRow();

            if (viewRow == -1) {
                return;
            }

            final int modelRow = this.table.convertRowIndexToModel(viewRow);

            final Genre genre = getTableModel().getObjectAt(modelRow);

            getController().setSelected(genre);

            getLogger().debug("{}", genre);
        });

        JScrollPane scrollPane = new JScrollPane(this.table);
        splitPane.setLeftComponent(scrollPane);

        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        splitPane.setRightComponent(panel);

        this.listMovies = new JList<>(new DefaultListListModel<>());
        this.listMovies.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.listMovies.setCellRenderer(new MovieListCellRenderer());
        scrollPane = new JScrollPane(this.listMovies);
        scrollPane.setBorder(new TitledBorder(getTranslation("movies")));
        panel.add(scrollPane, GbcBuilder.of(0, 0).fillBoth());

        this.listShows = new JList<>(new DefaultListListModel<>());
        this.listShows.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.listShows.setCellRenderer(new ShowListCellRenderer());
        scrollPane = new JScrollPane(this.listShows);
        scrollPane.setBorder(new TitledBorder(getTranslation("shows")));
        panel.add(scrollPane, GbcBuilder.of(1, 0).fillBoth());

        // Push all up.
        parentPanel.add(splitPane, BorderLayout.CENTER);

        return parentPanel;
    }

    @SuppressWarnings("unchecked")
    public void setShowsAndMovies(final List<? extends Model> shows, final List<? extends Model> movies) {
        ((DefaultListListModel<?>) this.listShows.getModel()).clear();
        ((DefaultListListModel<?>) this.listMovies.getModel()).clear();

        ((DefaultListListModel) this.listShows.getModel()).addAll(shows);
        ((DefaultListListModel) this.listMovies.getModel()).addAll(movies);
    }

    @Override
    protected GenreController getController() {
        return (GenreController) super.getController();
    }

    private GenreTableModel getTableModel() {
        return (GenreTableModel) this.table.getModel();
    }
}
