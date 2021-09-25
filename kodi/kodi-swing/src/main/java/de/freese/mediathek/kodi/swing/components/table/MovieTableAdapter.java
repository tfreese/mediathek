// Created: 10.06.2016
package de.freese.mediathek.kodi.swing.components.table;

import com.jgoodies.binding.adapter.AbstractTableAdapter;

import de.freese.mediathek.kodi.model.Movie;

/**
 * @author Thomas Freese
 */
public class MovieTableAdapter extends AbstractTableAdapter<Movie>
{
    /**
     *
     */
    private static final long serialVersionUID = 5500616551765142373L;

    /**
     * Erstellt ein neues {@link MovieTableAdapter} Object.
     */
    public MovieTableAdapter()
    {
        super("ID", "Name");
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex)
    {
        Movie movie = getRow(rowIndex);

        return switch (columnIndex)
        {
            case 0 -> movie.getPK();

            case 1 -> movie.getName();

            default -> null;
        };
    }
}
