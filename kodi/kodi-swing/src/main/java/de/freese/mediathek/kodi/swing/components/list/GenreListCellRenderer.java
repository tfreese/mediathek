// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.components.list;

import java.awt.Component;
import java.io.Serial;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.freese.mediathek.kodi.model.Genre;

/**
 * {@link ListCellRenderer} für {@link Genre}.
 *
 * @author Thomas Freese
 */
public class GenreListCellRenderer extends DefaultListCellRenderer
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 4538330809572067558L;

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    @Override
    public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected,
                                                  final boolean cellHasFocus)
    {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Genre genre = (Genre) value;
        setText(String.format("%s (%d)", genre.getName(), genre.getPK()));

        return this;
    }
}
