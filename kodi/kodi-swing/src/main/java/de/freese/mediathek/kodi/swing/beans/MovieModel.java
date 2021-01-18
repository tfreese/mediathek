/**
 * Created: 28.09.2014
 */
package de.freese.mediathek.kodi.swing.beans;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import de.freese.base.core.cache.FileResourceCache;
import de.freese.base.core.cache.ResourceCache;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.swing.KODISwingClient;
import de.freese.mediathek.kodi.swing.components.rowfilter.RegExRowFilter;

/**
 * {@link PresentationModel} der {@link ShowBean}.
 *
 * @author Thomas Freese
 */
public class MovieModel extends PresentationModel<MovieBean>
{
    /**
     *
     */
    private static final long serialVersionUID = 5768855611560857610L;

    /**
     *
     */
    private JTable jTable;

    /**
     *
     */
    private final SelectionInList<Movie> movieSelection;

    /**
     *
     */
    private final ResourceCache resourceCache;

    /**
     *
     */
    private final ValueModel valueModelFilter;

    /**
     *
     */
    private final ValueModel valueModelPoster;

    /**
     * Erstellt ein neues {@link MovieModel} Object.
     */
    public MovieModel()
    {
        super();

        this.movieSelection = new SelectionInList<>();
        this.valueModelPoster = new ValueHolder();
        this.valueModelFilter = new ValueHolder();
        this.resourceCache = new FileResourceCache();
    }

    /**
     * @param jLabel {@link JLabel}
     */
    public void bindGenreLabel(final JLabel jLabel)
    {
        Bindings.bind(jLabel, getModel(MovieBean.PROPERTY_GENRES));
    }

    /**
     * @param jLabel {@link JLabel}
     */
    public void bindIMDBIDLabel(final JLabel jLabel)
    {
        Bindings.bind(jLabel, getModel(MovieBean.PROPERTY_IMDB_ID));
    }

    /**
     * @param jTable {@link JTable}
     * @param listSelectionListener {@link ListSelectionListener}
     */
    public void bindMovieTable(final JTable jTable, final ListSelectionListener listSelectionListener)
    {
        this.jTable = jTable; // Wird in #getSelectedMovie benötigt

        Bindings.bind(jTable, this.movieSelection);

        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // jTable.setCellRenderer(new MovieListCellRenderer());
        jTable.getSelectionModel().addListSelectionListener(listSelectionListener);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(jTable.getModel());
        jTable.setRowSorter(sorter);

        this.valueModelFilter.addValueChangeListener(event -> {
            String text = (String) event.getNewValue();

            if (StringUtils.isBlank(text))
            {
                sorter.setRowFilter(null);
            }
            else
            {
                // sorter.setRowFilter(RowFilter.regexFilter("(?i)" +text)); // ignore case
                sorter.setRowFilter(new RegExRowFilter(Pattern.CASE_INSENSITIVE, text, 1));
            }
        });
    }

    /**
     * @param jLabel {@link JLabel}
     */
    public void bindPosterLabel(final JLabel jLabel)
    {
        Bindings.bind(jLabel, "icon", this.valueModelPoster);
    }

    /**
     * @param textFieldFilter {@link JTextField}
     */
    public void bindTextFieldFilter(final JTextField textFieldFilter)
    {
        Bindings.bind(textFieldFilter, this.valueModelFilter);
    }

    /**
     * @return {@link Movie}
     */
    public Movie getSelectedMovie()
    {
        // RowIndex wegen Filter gerade rücken.
        int selectedIndex = this.movieSelection.getSelectionIndex();

        if (selectedIndex < 0)
        {
            return null;
        }

        int modelIndex = this.jTable.convertRowIndexToModel(selectedIndex);

        return this.movieSelection.getElementAt(modelIndex);
        // return this.movieSelection.getSelection();
    }

    /**
     * @see com.jgoodies.binding.PresentationModel#setBean(java.lang.Object)
     */
    @Override
    public void setBean(final MovieBean newBean)
    {
        super.setBean(newBean);

        this.valueModelPoster.setValue(null);

        if (newBean == null)
        {
            return;
        }

        // Banner Laden
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected ImageIcon doInBackground() throws Exception
            {
                // if (StringUtils.isBlank(getBean().getPoster()))
                // {
                // List<Image> poster = MovieModel.this.movieService.getPoster(getBean().getImdbID());
                // getBean().setPoster(poster.get(0).getPath());
                // }
                //
                // BufferedImage image = MovieModel.this.movieService.getImage(getBean().getPoster(), Size.w342);
                //
                // if (image == null)
                // {
                // return null;
                // }
                //
                // return new ImageIcon(image);

                String url = StringUtils.substringBetween(getBean().getPosters(), "preview=\"", "\">");

                if (StringUtils.isBlank(url))
                {
                    url = StringUtils.substringBetween(getBean().getPosters(), ">", "<");
                }

                url = StringUtils.replace(url, "t/p/w500", "t/p/w342"); // w92, w154, w185, w342, w500

                if (StringUtils.isNotBlank(url))
                {
                    Optional<InputStream> optional = MovieModel.this.resourceCache.getResource(url);

                    if (optional.isPresent())
                    {
                        try (InputStream inputStream = optional.get())
                        {
                            BufferedImage image = ImageIO.read(inputStream);

                            if (image == null)
                            {
                                return null;
                            }

                            return new ImageIcon(image);
                        }
                    }
                }
                else
                {
                    KODISwingClient.LOGGER.error("{}: No valid url: {}", getBean().getName(), getBean().getPosters());
                }

                return null;
            }

            /**
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done()
            {
                try
                {
                    MovieModel.this.valueModelPoster.setValue(get());
                }
                catch (Exception ex)
                {
                    KODISwingClient.LOGGER.error(null, ex);
                }
            }
        };
        worker.execute();
    }

    /**
     * @param movies {@link List}
     */
    public void setList(final List<Movie> movies)
    {
        this.movieSelection.setList(movies);

        if (CollectionUtils.isNotEmpty(movies) && !this.movieSelection.isEmpty())
        {
            this.movieSelection.setSelectionIndex(0);
        }
    }
}
