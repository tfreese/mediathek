// Created: 26.12.22
package de.freese.mediathek.kodi.swing.components.table;

import java.io.Serial;
import java.util.List;

import de.freese.mediathek.kodi.model.Genre;

/**
 * @author Thomas Freese
 */
public class GenreTableModel extends AbstractListTableModel<Genre> {
    @Serial
    private static final long serialVersionUID = 253661107708523574L;

    public GenreTableModel() {
        super(List.of("Genre", "Filme", "Serien"));
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final Genre genre = getObjectAt(rowIndex);

        return switch (columnIndex) {
            case 0 -> String.format("%s (%d)", genre.getName(), genre.getPk());
            case 1 -> genre.getAnzahlFilme();
            case 2 -> genre.getAnzahlSerien();
            default -> null;
        };
    }
}
