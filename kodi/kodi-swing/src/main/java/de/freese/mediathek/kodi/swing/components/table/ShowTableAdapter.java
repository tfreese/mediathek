// Created:n 10.06.2016
package de.freese.mediathek.kodi.swing.components.table;

import com.jgoodies.binding.adapter.AbstractTableAdapter;

import de.freese.mediathek.kodi.model.Show;

/**
 * @author Thomas Freese
 */
public class ShowTableAdapter extends AbstractTableAdapter<Show>
{
    /**
     *
     */
    private static final long serialVersionUID = 5258240913718717061L;

    /**
     * Erstellt ein neues {@link ShowTableAdapter} Object.
     */
    public ShowTableAdapter()
    {
        super("ID", "Name");
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex)
    {
        Show show = getRow(rowIndex);

        return switch (columnIndex)
        {
            case 0 -> show.getPK();

            case 1 -> show.getName();

            default -> null;
        };
    }
}
