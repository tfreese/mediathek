// Created: 08 Sept. 2024
package de.freese.player.ui.swing.component.table;

import java.io.Serial;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import de.freese.player.core.input.AudioSource;
import de.freese.player.core.player.SongCollection;
import de.freese.player.core.util.PlayerUtils;

/**
 * @author Thomas Freese
 */
public final class TableModelSongCollection extends AbstractTableModel implements SongCollection {
    @Serial
    private static final long serialVersionUID = -2452262087762037947L;

    private final transient List<AudioSource> audioSources = new ArrayList<>(1024);
    private final transient List<String> columnNames;

    private int currentIndex;
    private Duration durationTotal = Duration.ZERO;

    public TableModelSongCollection() {
        super();

        columnNames = List.of(
                "Artist",
                "Album",
                "Title",
                "Duration",
                "SampleRate",
                "BitRate",
                "Channels",
                "Format",
                "Disc",
                "Track",
                "PlayCount",
                "Genre");
    }

    @Override
    public SongCollection addAudioSource(final AudioSource audioSource) {
        audioSources.add(audioSource);

        durationTotal = durationTotal.plus(audioSource.getDuration());

        if (SwingUtilities.isEventDispatchThread()) {
            fireTableRowsInserted(getRowCount(), getRowCount() - 1);
        }
        else {
            SwingUtilities.invokeLater(() -> fireTableRowsInserted(getRowCount(), getRowCount() - 1));
        }

        return this;
    }

    @Override
    public SongCollection addAudioSources(final Collection<AudioSource> audioSources) {
        final int firstRow = getRowCount();

        this.audioSources.addAll(audioSources);

        audioSources.forEach(as ->
                durationTotal = durationTotal.plus(as.getDuration())
        );

        if (SwingUtilities.isEventDispatchThread()) {
            fireTableRowsInserted(firstRow, getRowCount() - 1);
        }
        else {
            SwingUtilities.invokeLater(() -> fireTableRowsInserted(firstRow, getRowCount() - 1));
        }

        return this;
    }

    @Override
    public void clear() {
        audioSources.clear();
        currentIndex = 0;
        durationTotal = Duration.ZERO;

        fireTableDataChanged();
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
    public int getCurrentIndex() {
        return currentIndex;
    }

    @Override
    public Duration getDurationTotal() {
        return durationTotal;
    }

    @Override
    public int getRowCount() {
        return size();
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final AudioSource audioSource = getAudioSource(rowIndex);

        return switch (columnIndex) {
            case 0 -> audioSource.getArtist() == null ? PlayerUtils.toStringForTable(audioSource.getUri()) : audioSource.getArtist();
            case 1 -> audioSource.getAlbum();
            case 2 -> audioSource.getTitle();
            case 3 -> audioSource.getDuration();
            case 4 -> audioSource.getSampleRate();
            case 5 -> audioSource.getBitRate();
            case 6 -> audioSource.getChannels();
            case 7 -> audioSource.getFormat();
            case 8 -> audioSource.getDisc();
            case 9 -> audioSource.getTrack();
            case 10 -> audioSource.getPlayCount();
            case 11 -> audioSource.getGenre();
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
    public void setCurrentIndex(final int index) {
        currentIndex = index;
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
