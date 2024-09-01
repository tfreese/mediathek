// Created: 01 Sept. 2024
package de.freese.player.swing.component.table;

import java.io.Serial;
import java.util.List;
import java.util.Objects;

import javax.swing.table.AbstractTableModel;

import de.freese.player.input.AudioSource;
import de.freese.player.player.PlayList;

/**
 * @author Thomas Freese
 */
public final class PlayListTableModel extends AbstractTableModel {
    @Serial
    private static final long serialVersionUID = -4547299766271182171L;

    private final transient List<String> columnNames;
    private final transient PlayList playList;

    public PlayListTableModel(final PlayList playList) {
        super();

        this.playList = Objects.requireNonNull(playList, "playList required");

        columnNames = List.of("URL", "BitRate", "Channels", "Duration", "Format", "SamplingRate");
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public String getColumnName(final int column) {
        return columnNames.get(column);
    }

    @Override
    public int getRowCount() {
        return playList.size();
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final AudioSource audioSource = playList.getAudioSource(rowIndex);

        return switch (columnIndex) {
            case 0 -> audioSource.getUri();
            case 1 -> audioSource.getBitRate();
            case 2 -> audioSource.getChannels();
            case 3 -> audioSource.getDuration();
            case 4 -> audioSource.getFormat();
            case 5 -> audioSource.getSamplingRate();
            default -> throw new UnsupportedOperationException("Unsupported columnIndex:" + columnIndex);
        };
    }
}
