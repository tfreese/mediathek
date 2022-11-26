// Created: 17.09.2014
package de.freese.mediathek.kodi.swing.components.table;

import java.io.Serial;

import com.jgoodies.binding.adapter.AbstractTableAdapter;
import de.freese.mediathek.kodi.model.Genre;

/**
 * TableAdapter für {@link Genre}.
 *
 * @author Thomas Freese
 */
public class GenreTableAdapter extends AbstractTableAdapter<Genre>
{
    @Serial
    private static final long serialVersionUID = -7235372423320630854L;

    public GenreTableAdapter()
    {
        super("Genre", "Filme", "Serien");
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex)
    {
        Genre genre = getRow(rowIndex);

        return switch (columnIndex)
                {
                    case 0 -> String.format("%s (%d)", genre.getName(), genre.getPK());
                    case 1 -> genre.getAnzahlFilme();
                    case 2 -> genre.getAnzahlSerien();
                    default -> null;
                };
    }
}
