// Created: 27.12.22
package de.freese.mediathek.kodi.swing.view;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.freese.mediathek.kodi.swing.GbcBuilder;
import de.freese.mediathek.kodi.swing.components.rowfilter.RegExRowFilter;
import de.freese.mediathek.kodi.swing.components.table.AbstractListTableModel;

/**
 * @author Thomas Freese
 */
public abstract class AbstractShowAndMovieView<T> extends AbstractView<T>
{
    private JButton genreButton;
    private JLabel genreLabel;
    private JLabel idLabel;
    private JLabel imageLabel;
    private JTable table;

    protected AbstractShowAndMovieView()
    {
        super();
    }

    @Override
    public void clear()
    {
        getTableModel().clear();
    }

    public void doOnGenres(Consumer<JButton> consumer)
    {
        consumer.accept(this.genreButton);
    }

    @Override
    public void fill(final List<T> data)
    {
        getTableModel().addAll(data);

        if (data != null && !data.isEmpty())
        {
            getTable().setRowSelectionInterval(0, 0);
        }
    }

    @Override
    public T getSelected()
    {
        int viewRow = this.table.getSelectedRow();

        if (viewRow < 0)
        {
            return null;
        }

        int modelRow = this.table.convertRowIndexToModel(viewRow);

        return getTableModel().getObjectAt(modelRow);
    }

    public void setImage(ImageIcon imageIcon)
    {
        getImageLabel().setIcon(imageIcon);
    }

    protected JLabel getGenreLabel()
    {
        return genreLabel;
    }

    protected JLabel getIdLabel()
    {
        return idLabel;
    }

    protected JLabel getImageLabel()
    {
        return imageLabel;
    }

    protected JTable getTable()
    {
        return table;
    }

    protected AbstractListTableModel<T> getTableModel()
    {
        return (AbstractListTableModel<T>) table.getModel();
    }

    @Override
    protected void init(final JPanel parentPanel)
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
        leftPanel.add(textFieldFilter, new GbcBuilder(1, 0).fillHorizontal());

        this.table = new JTable();
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        initTable(this.table, textFieldFilter);
        this.table.getColumnModel().getColumn(0).setMinWidth(50);
        this.table.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scrollPane = new JScrollPane(this.table);

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
        detailPanel.add(new JLabel("Genres:"), new GbcBuilder(0, 0));
        this.genreLabel = new JLabel();
        detailPanel.add(this.genreLabel, new GbcBuilder(1, 0));

        // Details TvDb Id, ImDb Id
        label = new JLabel();
        translateIdLabel(label);
        detailPanel.add(label, new GbcBuilder(0, 1));
        this.idLabel = new JLabel();
        detailPanel.add(this.idLabel, new GbcBuilder(1, 1));

        // Details Image
        this.imageLabel = new JLabel();
        detailPanel.add(this.imageLabel, new GbcBuilder(0, 2).gridWidth(2).weightX(1.0D).fillHorizontal().anchorCenter());

        rightPanel.add(detailPanel, new GbcBuilder(0, 0).weightX(1.0D).fillHorizontal());

        // Genres
        this.genreButton = new JButton("Edit Genres");
        rightPanel.add(this.genreButton, new GbcBuilder(0, 1));

        // Push all up.
        rightPanel.add(Box.createGlue(), new GbcBuilder(0, 2).fillBoth());

        parentPanel.add(splitPane, BorderLayout.CENTER);
    }

    protected void initTable(JTable table, JTextField textFieldFilter)
    {
        AbstractListTableModel<T> tableModel = getTableModel();

        table.getSelectionModel().addListSelectionListener(event ->
        {
            if (event.getValueIsAdjusting())
            {
                return;
            }

            int viewRow = table.getSelectedRow();

            if (viewRow == -1)
            {
                getConsumerOnSelection().accept(null);
                return;
            }

            int modelRow = table.convertRowIndexToModel(viewRow);

            T entity = tableModel.getObjectAt(modelRow);

            getConsumerOnSelection().accept(entity);

            getLogger().debug("{}", entity);
        });

        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(tableModel);
        rowSorter.addRowSorterListener(event ->
        {
            if (rowSorter.getViewRowCount() > 0 && table.getSelectedRowCount() == 0)
            {
                table.setRowSelectionInterval(0, 0);
            }
        });

        table.setRowSorter(rowSorter);

        textFieldFilter.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void changedUpdate(final DocumentEvent event)
            {
                updateFilter();
            }

            @Override
            public void insertUpdate(final DocumentEvent event)
            {
                updateFilter();
            }

            @Override
            public void removeUpdate(final DocumentEvent event)
            {
                updateFilter();
            }

            private void updateFilter()
            {
                String text = textFieldFilter.getText();

                if (text == null || text.isBlank())
                {
                    rowSorter.setRowFilter(null);
                }
                else
                {
                    // rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" +text)); // ignore case
                    rowSorter.setRowFilter(new RegExRowFilter(text, Pattern.CASE_INSENSITIVE, List.of(1)));
                }
            }
        });
    }

    /**
     * Shows: TvDb Id<br/>
     * Movies: ImDb Id
     */
    protected abstract void translateIdLabel(JLabel label);
}
