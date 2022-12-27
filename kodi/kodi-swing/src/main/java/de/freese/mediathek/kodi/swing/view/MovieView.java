// Created: 27.12.22
package de.freese.mediathek.kodi.swing.view;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.swing.components.table.MovieTableModel;

/**
 * @author Thomas Freese
 */
public class MovieView extends AbstractShowAndMovieView<Movie>
{
    public MovieView()
    {
        super();
    }

    @Override
    public void updateWithSelection(final Movie movie)
    {
        getImageLabel().setIcon(null);
        getGenreLabel().setText(null);
        getIdLabel().setText(null);

        if (movie == null)
        {
            return;
        }

        getGenreLabel().setText(movie.getGenres());
        getIdLabel().setText(movie.getImDbId());
    }

    @Override
    protected void initTable(final JTable table, final JTextField textFieldFilter)
    {
        table.setModel(new MovieTableModel());

        super.initTable(table, textFieldFilter);
    }

    @Override
    protected void translateIdLabel(final JLabel label)
    {
        label.setText("ImDb Id");
    }
}
