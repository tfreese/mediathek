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
import de.freese.player.util.PlayerUtils;

/**
 * @author Thomas Freese
 */
public class PlayListCellRenderer extends DefaultTableCellRenderer {
    @Serial
    private static final long serialVersionUID = -1L;

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        final AudioSource audioSource = ApplicationContext.getSongCollection().getAudioSource(row);
        setToolTipText(audioSource.getUri().getPath());

        if (value instanceof Number) {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }
        else {
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        if (value instanceof Duration duration) {
            setText(PlayerUtils.toString(duration));
        }

        return this;
    }
}
