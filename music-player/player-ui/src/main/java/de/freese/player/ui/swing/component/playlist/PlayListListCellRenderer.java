// Created: 23 Sept. 2024
package de.freese.player.ui.swing.component.playlist;

import java.awt.Component;
import java.io.Serial;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import de.freese.player.ui.model.PlayList;

/**
 * @author Thomas Freese
 */
public final class PlayListListCellRenderer extends DefaultListCellRenderer {
    @Serial
    private static final long serialVersionUID = -1L;
    
    @Override
    public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        final PlayList playList = (PlayList) value;

        setText(playList.getName());

        return this;
    }
}
