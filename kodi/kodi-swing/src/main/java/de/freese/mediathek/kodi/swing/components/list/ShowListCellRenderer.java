/**
 * Created: 28.09.2014
 */

package de.freese.mediathek.kodi.swing.components.list;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import de.freese.mediathek.kodi.model.Show;

/**
 * {@link ListCellRenderer} für {@link Show}.
 *
 * @author Thomas Freese
 */
public class ShowListCellRenderer extends DefaultListCellRenderer
{
    /**
     *
     */
    private static final long serialVersionUID = 3721555812207440061L;

    // /**
    // *
    // */
    // private final Font BOLD_FONT;

    /**
     * Erstellt ein neues {@link ShowListCellRenderer} Object.
     */
    public ShowListCellRenderer()
    {
        super();

        // this.BOLD_FONT = getFont().deriveFont(Font.BOLD, 20);
    }

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    @Override
    public Component getListCellRendererComponent(final JList<? extends Object> list, final Object value, final int index, final boolean isSelected,
                                                  final boolean cellHasFocus)
    {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Show show = (Show) value;
        setText(String.format("%s (%d)", show.getName(), show.getPK()));

        return this;
    }
}
