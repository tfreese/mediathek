// Created: 08 Sept. 2024
package de.freese.player.swing.component.table;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.table.AbstractTableModel;

import de.freese.player.input.AudioSource;
import de.freese.player.player.PlayList;

/**
 * @author Thomas Freese
 */
public final class TablePlayList extends AbstractTableModel implements PlayList {
    @Serial
    private static final long serialVersionUID = -2452262087762037947L;

    private final transient List<AudioSource> audioSources = new ArrayList<>(1024);
    private final transient List<String> columnNames;

    private int currentIndex;

    public TablePlayList() {
        super();

        columnNames = List.of("URL", "BitRate", "Channels", "Duration", "Format", "SamplingRate");
    }

    @Override
    public PlayList addAudioSource(final AudioSource audioSource) {
        audioSources.add(audioSource);

        fireTableRowsInserted(getRowCount(), getRowCount());

        return this;
    }

    @Override
    public int currentIndex() {
        return currentIndex;
    }

    @Override
    public AudioSource getAudioSource(final int index) {
        return audioSources.get(index);
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
        return size();
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final AudioSource audioSource = getAudioSource(rowIndex);

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

    @Override
    public int indexOf(final AudioSource audioSource) {
        return audioSources.indexOf(audioSource);
    }

    @Override
    public AudioSource next() {
        if (currentIndex >= size() - 1) {
            return null;
        }

        currentIndex++;

        return getAudioSource(currentIndex);
    }

    @Override
    public AudioSource previous() {
        if (currentIndex == 0) {
            return null;
        }

        currentIndex--;

        return getAudioSource(currentIndex);
    }

    @Override
    public int size() {
        return audioSources.size();
    }

    @Override
    public Stream<AudioSource> stream() {
        return audioSources.stream();
    }
}
