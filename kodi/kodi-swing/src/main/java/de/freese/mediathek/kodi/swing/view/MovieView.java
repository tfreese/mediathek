// Created: 28.12.22
package de.freese.mediathek.kodi.swing.view;

import java.util.ResourceBundle;

import javax.swing.JTable;
import javax.swing.JTextField;

import de.freese.mediathek.kodi.swing.components.table.MovieTableModel;

/**
 * @author Thomas Freese
 */
public class MovieView extends AbstractShowAndMovieView
{
    public MovieView(final ResourceBundle resourceBundle)
    {
        super(resourceBundle);
    }

    @Override
    protected String getKeyForIdLabel()
    {
        return "id.label.movie";
    }

    @Override
    protected void initTable(final JTable table, final JTextField textFieldFilter)
    {
        table.setModel(new MovieTableModel());

        super.initTable(table, textFieldFilter);
    }
}
