// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.components.list;

import java.awt.Component;
import java.io.Serial;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import de.freese.mediathek.kodi.model.Show;

/**
 * @author Thomas Freese
 */
public class ShowListCellRenderer extends DefaultListCellRenderer {
    @Serial
    private static final long serialVersionUID = 3721555812207440061L;

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    @Override
    public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Show show = (Show) value;
        setText(String.format("%s (%d)", show.getName(), show.getPk()));

        return this;
    }
}
