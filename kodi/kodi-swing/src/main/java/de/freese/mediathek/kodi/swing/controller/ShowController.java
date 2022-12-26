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

import de.freese.mediathek.kodi.model.Show;
import de.freese.mediathek.kodi.swing.components.rowfilter.RegExRowFilter;
import de.freese.mediathek.kodi.swing.components.table.ShowTableModel;
import de.freese.mediathek.utils.ImageUtils;
import de.freese.mediathek.utils.cache.ResourceCache;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Thomas Freese
 */
public class ShowController extends AbstractController
{
    private JLabel bannerLabel;

    private JLabel genreLabel;

    private JTable showTable;

    private JTextField textFieldFilter;

    private JLabel tvDbIdLabel;

    public ShowController(ResourceCache resourceCache)
    {
        super(resourceCache);
    }

    public void bindBannerLabel(JLabel bannerLabel)
    {
        this.bannerLabel = bannerLabel;
    }

    public void bindGenreLabel(JLabel genreLabel)
    {
        this.genreLabel = genreLabel;
    }

    public void bindShowTable(JTable showTable)
    {
        this.showTable = showTable;

        ShowTableModel showTableModel = new ShowTableModel();
        this.showTable.setModel(showTableModel);

        this.showTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.showTable.getSelectionModel().addListSelectionListener(event ->
        {
            if (event.getValueIsAdjusting())
            {
                return;
            }

            int viewRow = this.showTable.getSelectedRow();

            if (viewRow == -1)
            {
                updateSelectedShow(null);
                return;
            }

            int modelRow = this.showTable.convertRowIndexToModel(viewRow);

            Show show = showTableModel.getObjectAt(modelRow);

            updateSelectedShow(show);

            getLogger().debug("{}", show);
        });

        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(showTableModel);
        rowSorter.addRowSorterListener(event ->
        {
            if (rowSorter.getViewRowCount() > 0 && this.showTable.getSelectedRowCount() == 0)
            {
                this.showTable.setRowSelectionInterval(0, 0);
            }
        });

        this.showTable.setRowSorter(rowSorter);

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

    public void bindTextFieldFilter(JTextField textFieldFilter)
    {
        this.textFieldFilter = textFieldFilter;
    }

    public void bindTvDbIdLabel(JLabel tvDbIdLabel)
    {
        this.tvDbIdLabel = tvDbIdLabel;
    }

    public void clearShows()
    {
        getShowTableModel().clear();
    }

    public Show getSelectedShow()
    {
        int viewRow = this.showTable.getSelectedRow();

        if (viewRow < 0)
        {
            return null;
        }

        int modelRow = this.showTable.convertRowIndexToModel(viewRow);

        return getShowTableModel().getObjectAt(modelRow);
    }

    public void setShows(List<Show> shows)
    {
        getShowTableModel().addAll(shows);

        if (shows != null && !shows.isEmpty())
        {
            this.showTable.setRowSelectionInterval(0, 0);
        }
    }

    private ShowTableModel getShowTableModel()
    {
        return (ShowTableModel) this.showTable.getModel();
    }

    private void updateSelectedShow(Show show)
    {
        bannerLabel.setIcon(null);
        genreLabel.setText(null);
        tvDbIdLabel.setText(null);

        if (show == null)
        {
            return;
        }

        genreLabel.setText(show.getGenres());
        tvDbIdLabel.setText(show.getTvDbId());

        // Load Banner
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected ImageIcon doInBackground() throws Exception
            {
                String url = StringUtils.substringBetween(show.getBanner(), "preview=\"", "\">");

                if (url.contains("\""))
                {
                    url = url.substring(0, url.indexOf('"'));
                }

                // url = StringUtils.replace(url, "t/p/w500", "t/p/w92");

                if (url == null || url.isBlank())
                {
                    url = StringUtils.substringBetween(show.getBanner(), ">", "<");
                }

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
                    getLogger().error("{}: No valid url: {}", show.getName(), show.getBanner());
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
                    bannerLabel.setIcon(get());
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
