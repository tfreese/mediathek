// Created: 28.12.22
package de.freese.mediathek.kodi.swing.view;

import javax.swing.JTable;
import javax.swing.JTextField;

import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.components.table.ShowTableModel;

/**
 * @author Thomas Freese
 */
public class ShowView extends AbstractShowAndMovieView<Show>
{
    @Override
    protected String getKeyForIdLabel()
    {
        return "id.label.show";
    }

    @Override
    protected void initTable(final JTable table, final JTextField textFieldFilter)
    {
        table.setModel(new ShowTableModel());

        super.initTable(table, textFieldFilter);
    }
}
