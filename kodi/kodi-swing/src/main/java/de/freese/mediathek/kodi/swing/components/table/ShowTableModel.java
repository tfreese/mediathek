package de.freese.mediathek.kodi.swing.components.table;

import java.io.Serial;
import java.util.List;

import de.freese.mediathek.kodi.model.Show;

/**
 * @author Thomas Freese
 * @since 26.12.2022
 */
public class ShowTableModel extends AbstractListTableModel<Show> {
    @Serial
    private static final long serialVersionUID = -876434629539382491L;

    public ShowTableModel() {
        super(List.of("ID", "Name"));
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final Show show = getObjectAt(rowIndex);

        return switch (columnIndex) {
            case 0 -> show.getPk();
            case 1 -> show.getName();
            default -> null;
        };
    }
}
