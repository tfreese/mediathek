// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.controller;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.swing.components.rowfilter.RegExRowFilter;
import de.freese.mediathek.kodi.swing.components.table.MovieTableModel;
import de.freese.mediathek.utils.ImageUtils;
import de.freese.mediathek.utils.cache.ResourceCache;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Thomas Freese
 */
public class MovieController extends AbstractController
{
    private JLabel genreLabel;
    private JLabel imDbIdLabel;
    private JTable movieTable;
    private JLabel posterLabel;
    private JTextField textFieldFilter;

    public MovieController(ResourceCache resourceCache)
    {
        super(resourceCache);
    }

    public void bindGenreLabel(JLabel genreLabel)
    {
        this.genreLabel = genreLabel;
    }

    public void bindImDbIdLabel(JLabel imDbIdLabel)
    {
        this.imDbIdLabel = imDbIdLabel;
    }

    public void bindMovieTable(JTable movieTable)
    {
        this.movieTable = movieTable;

        MovieTableModel movieTableModel = new MovieTableModel();
        this.movieTable.setModel(movieTableModel);

        this.movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.movieTable.getSelectionModel().addListSelectionListener(event ->
        {
            if (event.getValueIsAdjusting())
            {
                return;
            }

            int viewRow = this.movieTable.getSelectedRow();

            if (viewRow == -1)
            {
                updateSelectedMovie(null);
                return;
            }

            int modelRow = this.movieTable.convertRowIndexToModel(viewRow);

            Movie movie = movieTableModel.getObjectAt(modelRow);

            updateSelectedMovie(movie);

            getLogger().debug("{}", movie);
        });

        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(movieTableModel);
        rowSorter.addRowSorterListener(event ->
        {
            if (rowSorter.getViewRowCount() > 0 && this.movieTable.getSelectedRowCount() == 0)
            {
                this.movieTable.setRowSelectionInterval(0, 0);
            }
        });

        this.movieTable.setRowSorter(rowSorter);

        this.textFieldFilter.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void changedUpdate(final DocumentEvent e)
            {
                updateFilter();
            }

            @Override
            public void insertUpdate(final DocumentEvent e)
            {
                updateFilter();
            }

            @Override
            public void removeUpdate(final DocumentEvent e)
            {
                updateFilter();
            }

            private void updateFilter()
            {
                String text = textFieldFilter.getText();

                if (text == null || text.isBlank())
                {
                    rowSorter.setRowFilter(null);
                }
                else
                {
                    // rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" +text)); // ignore case
                    rowSorter.setRowFilter(new RegExRowFilter(text, Pattern.CASE_INSENSITIVE, List.of(1)));
                }
            }
        });
    }

    public void bindPosterLabel(JLabel posterLabel)
    {
        this.posterLabel = posterLabel;
    }

    public void bindTextFieldFilter(JTextField textFieldFilter)
    {
        this.textFieldFilter = textFieldFilter;
    }

    public void clearMovies()
    {
        getMovieTableModel().clear();
    }

    public Movie getSelectedMovie()
    {
        int viewRow = this.movieTable.getSelectedRow();

        if (viewRow < 0)
        {
            return null;
        }

        int modelRow = this.movieTable.convertRowIndexToModel(viewRow);

        return getMovieTableModel().getObjectAt(modelRow);
    }

    public void setMovies(List<Movie> movies)
    {
        getMovieTableModel().addAll(movies);

        if (movies != null && !movies.isEmpty())
        {
            this.movieTable.setRowSelectionInterval(0, 0);
        }
    }

    private MovieTableModel getMovieTableModel()
    {
        return (MovieTableModel) this.movieTable.getModel();
    }

    private void updateSelectedMovie(Movie movie)
    {
        posterLabel.setIcon(null);
        genreLabel.setText(null);
        imDbIdLabel.setText(null);

        if (movie == null)
        {
            return;
        }

        genreLabel.setText(movie.getGenres());
        imDbIdLabel.setText(movie.getImDbId());

        // Load Banner
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>()
        {
            /**
             * @see SwingWorker#doInBackground()
             */
            @Override
            protected ImageIcon doInBackground() throws Exception
            {
                String url = StringUtils.substringBetween(movie.getPosters(), "preview=\"", "\">");

                if (url.contains("\""))
                {
                    url = url.substring(0, url.indexOf('"'));
                }

                // url = StringUtils.replace(url, "t/p/w500", "t/p/w92");

                if (url == null || url.isBlank())
                {
                    url = StringUtils.substringBetween(movie.getPosters(), ">", "<");
                }

                url = url.replace("t/p/w500", "t/p/w342"); // w92, w154, w185, w342, w500

                if (url != null && !url.isBlank())
                {
                    try (InputStream inputStream = getResourceCache().getResource(URI.create(url)))
                    {
                        BufferedImage image = ImageIO.read(inputStream);

                        if (image == null)
                        {
                            return null;
                        }

                        image = ImageUtils.scaleImageKeepRatio(image, 1024, 768);

                        return new ImageIcon(image);
                    }
                    catch (Exception ex)
                    {
                        getLogger().error(ex.getMessage());
                    }
                }
                else
                {
                    getLogger().error("{}: No valid url: {}", movie.getName(), movie.getPosters());
                }

                return null;
            }

            /**
             * @see SwingWorker#done()
             */
            @Override
            protected void done()
            {
                try
                {
                    posterLabel.setIcon(get());
                }
                catch (Exception ex)
                {
                    getLogger().error(ex.getMessage(), ex);
                }
            }
        };
        worker.execute();
    }
}
