// Created: 28.12.22
package de.freese.mediathek.kodi.swing.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.swing.Box;
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
import de.freese.mediathek.kodi.swing.controller.AbstractShowAndMovieController;
import de.freese.mediathek.kodi.swing.controller.Controller;

/**
 * @author Thomas Freese
 */
public abstract class AbstractShowAndMovieView<T> extends AbstractView {
    private JButton genreButton;

    private JLabel genreLabel;

    private JLabel idLabel;

    private JLabel imageLabel;

    private JTable table;

    public void clear() {
        getTableModel().clear();
    }

    public void fill(final List<T> data) {
        getTableModel().addAll(data);

        if (data != null && !data.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    public JLabel getGenreLabel() {
        return genreLabel;
    }

    public JLabel getIdLabel() {
        return idLabel;
    }

    public JLabel getImageLabel() {
        return imageLabel;
    }

    public T getSelected() {
        int viewRow = this.table.getSelectedRow();

        if (viewRow < 0) {
            return null;
        }

        int modelRow = this.table.convertRowIndexToModel(viewRow);

        return getTableModel().getObjectAt(modelRow);
    }

    @Override
    public Component init(final Controller controller, final ResourceBundle resourceBundle) {
        super.init(controller, resourceBundle);

        JPanel parentPanel = new JPanel();
        parentPanel.setLayout(new BorderLayout());

        JButton reloadButton = new JButton(getTranslation("reload"));
        reloadButton.addActionListener(event -> getController().reload());
        parentPanel.add(reloadButton, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(500);

        // Liste
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());

        JLabel label = new JLabel(getTranslation("filter") + ":");
        leftPanel.add(label, GbcBuilder.of(0, 0));

        JTextField textFieldFilter = new JTextField();
        leftPanel.add(textFieldFilter, GbcBuilder.of(1, 0).fillHorizontal());

        this.table = new JTable();
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        initTable(this.table, textFieldFilter);
        this.table.getColumnModel().getColumn(0).setMinWidth(30);
        this.table.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scrollPane = new JScrollPane(this.table);

        leftPanel.add(scrollPane, GbcBuilder.of(0, 1).gridWidth(2).fillBoth());
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        splitPane.setRightComponent(rightPanel);

        // Details
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new GridBagLayout());
        detailPanel.setBorder(new TitledBorder(getTranslation("details")));

        // Details Genres
        detailPanel.add(new JLabel(getTranslation("genres") + ":"), GbcBuilder.of(0, 0));
        this.genreLabel = new JLabel();
        detailPanel.add(this.genreLabel, GbcBuilder.of(1, 0));

        // Details TvDb Id, ImDb Id
        detailPanel.add(new JLabel(getTranslation(getKeyForIdLabel()) + ":"), GbcBuilder.of(0, 1));
        this.idLabel = new JLabel();
        detailPanel.add(this.idLabel, GbcBuilder.of(1, 1));

        // Details Image
        this.imageLabel = new JLabel();
        detailPanel.add(this.imageLabel, GbcBuilder.of(0, 2).gridWidth(2).weightX(1.0D).fillHorizontal().anchorCenter());

        rightPanel.add(detailPanel, GbcBuilder.of(0, 0).weightX(1.0D).fillHorizontal());

        // Genres
        this.genreButton = new JButton(getTranslation("genres.edit"));
        this.genreButton.addActionListener(event -> getController().openGenreDialog());
        this.genreButton.setEnabled(false);
        rightPanel.add(this.genreButton, GbcBuilder.of(0, 1));

        // Push all up.
        rightPanel.add(Box.createGlue(), GbcBuilder.of(0, 2).fillBoth());

        parentPanel.add(splitPane, BorderLayout.CENTER);

        return parentPanel;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected AbstractShowAndMovieController<T> getController() {
        return (AbstractShowAndMovieController) super.getController();
    }

    protected abstract String getKeyForIdLabel();

    @SuppressWarnings("unchecked")
    protected AbstractListTableModel<T> getTableModel() {
        return (AbstractListTableModel<T>) table.getModel();
    }

    protected void initTable(JTable table, JTextField textFieldFilter) {
        AbstractListTableModel<T> tableModel = getTableModel();

        table.getSelectionModel().addListSelectionListener(event -> {
            if (event.getValueIsAdjusting()) {
                return;
            }

            getController().clear();

            int viewRow = table.getSelectedRow();

            if (viewRow == -1) {
                genreButton.setEnabled(false);
                return;
            }

            genreButton.setEnabled(true);
            int modelRow = table.convertRowIndexToModel(viewRow);

            T entity = tableModel.getObjectAt(modelRow);

            getController().setSelected(entity);

            getLogger().debug("{}", entity);
        });

        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(tableModel);
        rowSorter.addRowSorterListener(event -> {
            if (rowSorter.getViewRowCount() > 0 && table.getSelectedRowCount() == 0) {
                table.setRowSelectionInterval(0, 0);
            }
        });

        table.setRowSorter(rowSorter);

        textFieldFilter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(final DocumentEvent event) {
                updateFilter();
            }

            @Override
            public void insertUpdate(final DocumentEvent event) {
                updateFilter();
            }

            @Override
            public void removeUpdate(final DocumentEvent event) {
                updateFilter();
            }

            private void updateFilter() {
                String text = textFieldFilter.getText();

                if (text == null || text.isBlank()) {
                    rowSorter.setRowFilter(null);
                }
                else {
                    // rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" +text)); // ignore case
                    rowSorter.setRowFilter(new RegExRowFilter(text, Pattern.CASE_INSENSITIVE, List.of(1)));
                }
            }
        });
    }
}
