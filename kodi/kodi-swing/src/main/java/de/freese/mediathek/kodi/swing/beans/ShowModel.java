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
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.KODISwingClient;
import de.freese.mediathek.kodi.swing.components.rowfilter.RegExRowFilter;

/**
 * {@link PresentationModel} der {@link ShowBean}.
 *
 * @author Thomas Freese
 */
public class ShowModel extends PresentationModel<ShowBean>
{
    /**
     *
     */
    private static final long serialVersionUID = -1759604850162069149L;

    /**
     *
     */
    private JTable jTable;

    /**
     *
     */
    private final ResourceCache resourceCache;

    /**
     *
     */
    private final SelectionInList<Show> showSelection;

    /**
     *
     */
    private final ValueModel valueModelBanner;

    /**
     *
     */
    private final ValueModel valueModelFilter;

    /**
     * Erstellt ein neues {@link ShowModel} Object.
     */
    public ShowModel()
    {
        super();

        this.showSelection = new SelectionInList<>();
        this.valueModelBanner = new ValueHolder();
        this.valueModelFilter = new ValueHolder();
        this.resourceCache = new FileResourceCache();
    }

    /**
     * @param jLabel {@link JLabel}
     */
    public void bindBannerLabel(final JLabel jLabel)
    {
        Bindings.bind(jLabel, "icon", this.valueModelBanner);
    }

    /**
     * @param jLabel {@link JLabel}
     */
    public void bindGenreLabel(final JLabel jLabel)
    {
        Bindings.bind(jLabel, getValueModelGenres());
    }

    /**
     * @param jTable {@link JTable}
     * @param listSelectionListener {@link ListSelectionListener}
     */
    public void bindShowTable(final JTable jTable, final ListSelectionListener listSelectionListener)
    {
        this.jTable = jTable; // Wird in #getSelectedShow benötigt

        Bindings.bind(jTable, this.showSelection);

        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // jTable.setCellRenderer(new ShowListCellRenderer());
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
     * @param textFieldFilter {@link JTextField}
     */
    public void bindTextFieldFilter(final JTextField textFieldFilter)
    {
        Bindings.bind(textFieldFilter, this.valueModelFilter);
    }

    /**
     * @param jLabel {@link JLabel}
     */
    public void bindTVDIDLabel(final JLabel jLabel)
    {
        Bindings.bind(jLabel, getModel(ShowBean.PROPERTY_TVDB_ID));
    }

    /**
     * @return {@link Show}
     */
    public Show getSelectedShow()
    {
        // RowIndex wegen Filter gerade rücken.
        int selectedIndex = this.showSelection.getSelectionIndex();

        // System.out.print(selectedIndex+ " / ");
        if (selectedIndex < 0)
        {
            // System.out.println();
            return null;
        }

        int modelIndex = this.jTable.convertRowIndexToModel(selectedIndex);
        // System.out.println(modelIndex);

        return this.showSelection.getElementAt(modelIndex);

        // return this.showSelection.getSelection();
    }

    /**
     * Liefert das {@link ValueModel} der Genres.
     *
     * @return {@link ValueModel}
     */
    public ValueModel getValueModelGenres()
    {
        return getModel(ShowBean.PROPERTY_GENRES);
    }

    /**
     * @see com.jgoodies.binding.PresentationModel#setBean(java.lang.Object)
     */
    @Override
    public void setBean(final ShowBean newBean)
    {
        super.setBean(newBean);

        this.valueModelBanner.setValue(null);

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
                // if (StringUtils.contains(getBean().getBanner(), "</thumb>"))
                // {
                // TVShow show = ShowModel.this.tvService.getDetails(getBean().getTvdbID());
                //
                // getBean().setBanner(show.getBanner());
                // }
                //
                // if (StringUtils.isNotBlank(getBean().getBanner()))
                // {
                // BufferedImage image = ShowModel.this.tvService.getImage(getBean().getBanner());
                //
                // if (image == null)
                // {
                // return null;
                // }
                //
                // return new ImageIcon(image);
                // }

                String url = StringUtils.substringBetween(getBean().getBanner(), "preview=\"", "\">");
                // url = StringUtils.replace(url, "t/p/w500", "t/p/w92");

                if (StringUtils.isBlank(url))
                {
                    url = StringUtils.substringBetween(getBean().getBanner(), ">", "<");
                }

                if (StringUtils.isNotBlank(url))
                {
                    Optional<InputStream> optional = ShowModel.this.resourceCache.getResource(url);

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
                    KODISwingClient.LOGGER.error("{}: No valid url: {}", getBean().getName(), getBean().getBanner());
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
                    ShowModel.this.valueModelBanner.setValue(get());
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
     * @param shows {@link List}
     */
    public void setList(final List<Show> shows)
    {
        this.showSelection.setList(shows);

        if (CollectionUtils.isNotEmpty(shows) && !this.showSelection.isEmpty())
        {
            this.showSelection.setSelectionIndex(0);
        }
    }
}
