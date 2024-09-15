// Created: 01 Sept. 2024
package de.freese.player.swing.component.table;

import java.awt.Component;
import java.io.Serial;
import java.time.Duration;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import de.freese.player.ApplicationContext;
import de.freese.player.input.AudioSource;

/**
 * @author Thomas Freese
 */
public class PlayListCellRenderer extends DefaultTableCellRenderer {
    @Serial
    private static final long serialVersionUID = -1L;

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        final AudioSource audioSource = ApplicationContext.getPlayList().getAudioSource(row);
        setToolTipText(audioSource.getUri().getPath());

        if (value instanceof Number) {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }
        else {
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        if (value instanceof Duration duration) {
            final int hours = duration.toHoursPart();
            final int minutes = duration.toMinutesPart();
            final int seconds = duration.toSecondsPart();
            final int millies = duration.toMillisPart();

            if (hours != 0) {
                setText("%d:%02d:%02d.%03d".formatted(hours, minutes, seconds, millies));
            }
            else if (minutes != 0) {
                setText("%d:%02d.%03d".formatted(minutes, seconds, millies));
            }
            else {
                setText("%d.%03d".formatted(seconds, millies));
            }
        }

        return this;
    }
}
