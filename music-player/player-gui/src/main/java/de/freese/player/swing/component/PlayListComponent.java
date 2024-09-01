// Created: 01 Sept. 2024
package de.freese.player.swing.component;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import de.freese.player.input.AudioSource;
import de.freese.player.player.PlayList;
import de.freese.player.swing.component.table.PlayListCellRenderer;
import de.freese.player.swing.component.table.PlayListTableModel;

/**
 * @author Thomas Freese
 */
public final class PlayListComponent {
    private final JComponent component;
    private final JTable jTable;
    private final PlayList playList;
    private Consumer<AudioSource> listener;

    public PlayListComponent(final PlayList playList) {
        super();

        this.playList = Objects.requireNonNull(playList, "playList required");

        final PlayListTableModel tableModel = new PlayListTableModel(playList);
        jTable = new JTable(tableModel);
        jTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.setDefaultRenderer(Object.class, new PlayListCellRenderer());
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        jTable.getColumnModel().getColumn(0).setMinWidth(250);

        // jTable.getSelectionModel().addListSelectionListener(event -> {
        //     if (event.getValueIsAdjusting()) {
        //         return;
        //     }
        //
        //     final int selectedRow = jTable.convertRowIndexToModel(jTable.getSelectedRow());
        //     final AudioSource audioSource = playList.getAudioSource(selectedRow);
        //     System.out.println(audioSource);
        // });

        jTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent event) {
                if (event.getClickCount() != 2) {
                    return;
                }

                final Point point = event.getPoint();
                final int row = jTable.rowAtPoint(point);
                final int selectedRow = jTable.convertRowIndexToModel(row);

                final AudioSource audioSource = playList.getAudioSource(selectedRow);

                if (listener != null) {
                    listener.accept(audioSource);
                }
            }
        });

        final JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(jTable);

        this.component = jScrollPane;
    }

    public JComponent getComponent() {
        return component;
    }

    public void selectAudioSource(final AudioSource audioSource) {
        final int intervall = playList.indexOf(audioSource);

        jTable.getSelectionModel().setSelectionInterval(intervall, intervall);
    }

    public void setPlayListSelectionListener(final Consumer<AudioSource> listener) {
        this.listener = Objects.requireNonNull(listener, "listener required");
    }
}
