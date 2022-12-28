// Created: 28.12.22
package de.freese.mediathek.kodi.swing.view;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.util.List;

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

/**
 * @author Thomas Freese
 */
public class GenreView extends AbstractView<Genre>
{
    private JList<Movie> listMovies;
    private JList<Show> listShows;
    private JTable table;

    public GenreView()
    {
        super();
    }

    @Override
    public void clear()
    {
        ((DefaultListListModel<?>) this.listShows.getModel()).clear();
        ((DefaultListListModel<?>) this.listMovies.getModel()).clear();

        getTableModel().clear();
    }

    @Override
    public void fill(final List<Genre> data)
    {
        getTableModel().addAll(data);

        if (data != null && !data.isEmpty())
        {
            this.table.setRowSelectionInterval(0, 0);
        }
    }

    @Override
    public Genre getSelected()
    {
        int viewRow = this.table.getSelectedRow();

        if (viewRow < 0)
        {
            return null;
        }

        int modelRow = this.table.convertRowIndexToModel(viewRow);

        return getTableModel().getObjectAt(modelRow);
    }

    public void setShowsAndMovies(List<? extends Model> shows, List<? extends Model> movies)
    {
        ((DefaultListListModel<?>) this.listShows.getModel()).clear();
        ((DefaultListListModel<?>) this.listMovies.getModel()).clear();

        ((DefaultListListModel) listShows.getModel()).addAll(shows);
        ((DefaultListListModel) listMovies.getModel()).addAll(movies);
    }

    @Override
    public void updateWithSelection(final Genre entity)
    {
        ((DefaultListListModel<?>) this.listShows.getModel()).clear();
        ((DefaultListListModel<?>) this.listMovies.getModel()).clear();
    }

    @Override
    protected void init(final JPanel parentPanel)
    {
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(340);

        this.table = new JTable(new GenreTableModel());
        this.table.getColumnModel().getColumn(0).setMinWidth(160);
        this.table.getColumnModel().getColumn(1).setMinWidth(60);
        this.table.getColumnModel().getColumn(2).setMinWidth(60);
        this.table.getSelectionModel().addListSelectionListener(event ->
        {
            if (event.getValueIsAdjusting())
            {
                return;
            }

            int viewRow = this.table.getSelectedRow();

            if (viewRow == -1)
            {
                getConsumerOnSelection().accept(null);
                return;
            }

            int modelRow = this.table.convertRowIndexToModel(viewRow);

            Genre genre = getTableModel().getObjectAt(modelRow);

            getConsumerOnSelection().accept(genre);

            getLogger().debug("{}", genre);
        });

        JScrollPane scrollPane = new JScrollPane(this.table);
        splitPane.setLeftComponent(scrollPane);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        splitPane.setRightComponent(panel);

        this.listMovies = new JList<>(new DefaultListListModel<>());
        this.listMovies.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.listMovies.setCellRenderer(new MovieListCellRenderer());
        scrollPane = new JScrollPane(this.listMovies);
        scrollPane.setBorder(new TitledBorder("Filme"));
        panel.add(scrollPane, new GbcBuilder(0, 0).fillBoth());

        this.listShows = new JList<>(new DefaultListListModel<>());
        this.listShows.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.listShows.setCellRenderer(new ShowListCellRenderer());
        scrollPane = new JScrollPane(this.listShows);
        scrollPane.setBorder(new TitledBorder("Serien"));
        panel.add(scrollPane, new GbcBuilder(1, 0).fillBoth());

        // Push all up.
        parentPanel.add(splitPane, BorderLayout.CENTER);
    }

    private GenreTableModel getTableModel()
    {
        return (GenreTableModel) this.table.getModel();
    }
}
