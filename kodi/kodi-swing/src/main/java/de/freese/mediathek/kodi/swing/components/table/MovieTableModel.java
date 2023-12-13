// Created: 26.12.22
package de.freese.mediathek.kodi.swing.components.table;

import java.io.Serial;
import java.util.List;

import de.freese.mediathek.kodi.model.Movie;

/**
 * @author Thomas Freese
 */
public class MovieTableModel extends AbstractListTableModel<Movie> {
    @Serial
    private static final long serialVersionUID = 6190752753086781062L;

    public MovieTableModel() {
        super(List.of("ID", "Name"));
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final Movie movie = getObjectAt(rowIndex);

        return switch (columnIndex) {
            case 0 -> movie.getPk();
            case 1 -> movie.getName();
            default -> null;
        };
    }
}
