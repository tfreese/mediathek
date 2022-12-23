// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.beans;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.Serial;
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
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.components.rowfilter.RegExRowFilter;
import de.freese.mediathek.utils.ImageUtils;
import de.freese.mediathek.utils.cache.ResourceCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link PresentationModel} der {@link ShowBean}.
 *
 * @author Thomas Freese
 */
public class ShowModel extends PresentationModel<ShowBean>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowModel.class);
    @Serial
    private static final long serialVersionUID = -1759604850162069149L;
    private final ResourceCache resourceCache;

    private final SelectionInList<Show> showSelection;

    private final ValueModel valueModelBanner;

    private final ValueModel valueModelFilter;

    private JTable jTable;

    public ShowModel(ResourceCache resourceCache)
    {
        super();

        this.showSelection = new SelectionInList<>();
        this.valueModelBanner = new ValueHolder();
        this.valueModelFilter = new ValueHolder();
        this.resourceCache = resourceCache;
    }

    public void bindBannerLabel(final JLabel jLabel)
    {
        Bindings.bind(jLabel, "icon", this.valueModelBanner);
    }

    public void bindGenreLabel(final JLabel jLabel)
    {
        Bindings.bind(jLabel, getValueModelGenres());
    }

    public void bindShowTable(final JTable jTable, final ListSelectionListener listSelectionListener)
    {
        this.jTable = jTable; // Wird in #getSelectedShow benötigt

        Bindings.bind(jTable, this.showSelection);

        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // jTable.setCellRenderer(new ShowListCellRenderer());
        jTable.getSelectionModel().addListSelectionListener(listSelectionListener);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(jTable.getModel());
        jTable.setRowSorter(sorter);

        this.valueModelFilter.addValueChangeListener(event ->
        {
            String text = (String) event.getNewValue();

            if (text == null || text.isBlank())
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

    public void bindTVDIDLabel(final JLabel jLabel)
    {
        Bindings.bind(jLabel, getModel(ShowBean.PROPERTY_TVDB_ID));
    }

    public void bindTextFieldFilter(final JTextField textFieldFilter)
    {
        Bindings.bind(textFieldFilter, this.valueModelFilter);
    }

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
                String url = StringUtils.substringBetween(getBean().getBanner(), "preview=\"", "\">");

                if (url.contains("\""))
                {
                    url = url.substring(0, url.indexOf('"'));
                }

                // url = StringUtils.replace(url, "t/p/w500", "t/p/w92");

                if (url == null || url.isBlank())
                {
                    url = StringUtils.substringBetween(getBean().getBanner(), ">", "<");
                }

                if (url != null && !url.isBlank())
                {
                    try (InputStream inputStream = ShowModel.this.resourceCache.getResource(URI.create(url)))
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
                        LOGGER.error(ex.getMessage());
                    }
                }
                else
                {
                    LOGGER.error("{}: No valid url: {}", getBean().getName(), getBean().getBanner());
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
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        };
        worker.execute();
    }

    public void setList(final List<Show> shows)
    {
        this.showSelection.setList(shows);

        if ((shows != null) && !shows.isEmpty() && !this.showSelection.isEmpty())
        {
            this.showSelection.setSelectionIndex(0);
        }
    }
}
