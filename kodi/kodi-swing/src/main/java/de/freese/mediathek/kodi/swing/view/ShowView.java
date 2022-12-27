// Created: 27.12.22
package de.freese.mediathek.kodi.swing.view;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.components.table.ShowTableModel;

/**
 * @author Thomas Freese
 */
public class ShowView extends AbstractShowAndMovieView<Show>
{
    public ShowView()
    {
        super();
    }

    @Override
    public void updateWithSelection(final Show show)
    {
        getImageLabel().setIcon(null);
        getGenreLabel().setText(null);
        getIdLabel().setText(null);

        if (show == null)
        {
            return;
        }

        getGenreLabel().setText(show.getGenres());
        getIdLabel().setText(show.getTvDbId());
    }

    @Override
    protected void initTable(final JTable table, final JTextField textFieldFilter)
    {
        table.setModel(new ShowTableModel());

        super.initTable(table, textFieldFilter);
    }

    @Override
    protected void translateIdLabel(final JLabel label)
    {
        label.setText("TvDb Id");
    }
}
