// Created: 03 Sept. 2024
package de.freese.player.ui.swing.component;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.freese.player.core.input.AudioSource;
import de.freese.player.core.player.DspPlayer;
import de.freese.player.core.player.SongCollection;
import de.freese.player.core.util.PlayerUtils;
import de.freese.player.ui.ApplicationContext;
import de.freese.player.ui.equalizer.EqualizerDspProcessor;
import de.freese.player.ui.spectrum.SpectrumDspProcessor;
import de.freese.player.ui.swing.component.spectrum.SpectrumView;
import de.freese.player.ui.swing.component.table.TableCellRendererSongCollection;
import de.freese.player.ui.swing.component.table.TableModelSongCollection;
import de.freese.player.ui.swing.component.timeline.TimeLineDspProcessor;
import de.freese.player.ui.utils.image.ImageFactory;

/**
 * @author Thomas Freese
 */
public final class PlayerView {
    // @Serial
    // private static final long serialVersionUID = -1L;

    private static final Icon ICON_PAUSE = ImageFactory.getIcon("images/media-pause-white.svg");
    private static final Icon ICON_PLAY = ImageFactory.getIcon("images/media-play-white.svg");
    private static final Icon ICON_STOP = ImageFactory.getIcon("images/media-stop-white.svg");

    private final JLabel labelSongsTotal = new JLabel();
    private final JPanel panel;

    private JButton buttonBackward;
    private JButton buttonForward;
    private JToggleButton buttonPlayPause;
    private JButton buttonStop;
    private JLabel labelSongTimePlayed;
    private JLabel labelSongTimeTotal;
    private JSlider sliderTimeLine;
    private JSlider sliderVolumeControl;
    private SpectrumView spectrumView;
    private JTable tableSongSollection;
    private JTextField textFieldSearch;

    public PlayerView() {
        super();

        panel = new JPanel(new GridBagLayout());
    }

    public JComponent getComponent() {
        return panel;
    }

    public void init() {
        initPlayerControl();

        final JScrollPane scrollPaneSongCollection = initTableSongCollection();

        ApplicationContext.getPlayer().addProcessor(new EqualizerDspProcessor());

        initSearch();
        initSpectrum();
        initTimeLine();
        initVolumeControl();

        // buttonPlayPause.setBorder(LineBorder.createBlackLineBorder());
        // buttonForward.setBorder(LineBorder.createBlackLineBorder());

        int row = 0;
        panel.add(new JLabel("Search"), GbcBuilder.of(0, row).insets(5, 5, 0, 5));
        panel.add(textFieldSearch, GbcBuilder.of(1, row).gridwidth(9).fillHorizontal().insets(5, 0, 0, 5));

        row++;
        panel.add(scrollPaneSongCollection, GbcBuilder.of(0, row).gridwidth(10).weighty(10D).fillBoth().insets(5, 5, 0, 5));

        row++;
        panel.add(buttonBackward, GbcBuilder.of(0, row).insets(0, 5, 0, 0));
        panel.add(buttonPlayPause, GbcBuilder.of(1, row).insets(0, 0, 0, 0));
        panel.add(buttonStop, GbcBuilder.of(2, row).insets(0, 0, 0, 0));
        panel.add(buttonForward, GbcBuilder.of(3, row).insets(0, 0, 0, 0));
        panel.add(spectrumView.getComponent(), GbcBuilder.of(4, row).gridwidth(5).fillHorizontal().insets(0, 0, 0, 0));

        final JPanel volumePanel = new JPanel(new GridBagLayout());
        volumePanel.add(new JLabel("Volume"), GbcBuilder.of(0, 0).insets(0, 0, 0, 0));
        volumePanel.add(sliderVolumeControl, GbcBuilder.of(0, 1).fillHorizontal().insets(0, 0, 0, 0));
        volumePanel.setPreferredSize(new Dimension(140, 75));
        panel.add(volumePanel, GbcBuilder.of(9, row).anchorCenter().insets(0, 0, 0, 5));

        row++;
        panel.add(labelSongsTotal, GbcBuilder.of(0, row).anchorWest().gridwidth(4).insets(5, 5, 0, 0));
        panel.add(labelSongTimePlayed, GbcBuilder.of(4, row).anchorEast().insets(5, 5, 0, 0));
        panel.add(sliderTimeLine, GbcBuilder.of(5, row).anchorCenter().gridwidth(4).fillHorizontal().insets(5, 0, 0, 0));
        // panel.add(labelSongTimeTotal, GbcBuilder.of(9, row).anchorWest().insets(5, 0, 0, 0));

        final JPanel timePanel = new JPanel(new GridBagLayout());
        timePanel.add(labelSongTimeTotal, GbcBuilder.of(0, 0).anchorWest().insets(5, 0, 0, 0));

        final JButton buttonFastBackward = new JButton("<<");
        buttonFastBackward.setToolTipText("Backward 10 Sec.");
        buttonFastBackward.addActionListener(event -> {
            final Duration timeTotal = ApplicationContext.getSongCollection().getCurrentAudioSource().getDuration();

            final long newTimeIndex = (long) (timeTotal.toMillis() * (sliderTimeLine.getValue() / 100D));
            final Duration timeTarget = Duration.ofMillis(newTimeIndex).minusSeconds(10);

            if (timeTarget.toMillis() <= 0) {
                return;
            }

            ApplicationContext.getPlayer().jumpTo(timeTarget);
        });
        timePanel.add(buttonFastBackward, GbcBuilder.of(1, 0).insets(0, 0, 5, 0));

        final JButton buttonFastForward = new JButton(">>");
        buttonFastForward.setToolTipText("Forward 10 Sec.");
        buttonFastForward.addActionListener(event -> {
            final Duration timeTotal = ApplicationContext.getSongCollection().getCurrentAudioSource().getDuration();

            final long newTimeIndex = (long) (timeTotal.toMillis() * (sliderTimeLine.getValue() / 100D));
            final Duration timeTarget = Duration.ofMillis(newTimeIndex).plusSeconds(10);

            if (timeTarget.toMillis() >= timeTotal.toMillis()) {
                return;
            }

            ApplicationContext.getPlayer().jumpTo(timeTarget);
        });
        timePanel.add(buttonFastForward, GbcBuilder.of(2, 0).insets(0, 0, 5, 5));
        panel.add(timePanel, GbcBuilder.of(9, row).insets(5, 0, 0, 0));

        initListener();
    }

    private void initListener() {
        final SongCollection songCollection = ApplicationContext.getSongCollection();
        final DspPlayer player = ApplicationContext.getPlayer();

        player.addSongFinishedListener(audioSource -> {
                    ApplicationContext.getRepository().updateSongPlayCount(audioSource.getUri(), audioSource.getPlayCount() + 1);

                    SwingUtilities.invokeLater(() -> {
                        buttonPlayPause.setSelected(false);
                        buttonPlayPause.setIcon(ICON_PLAY);
                        labelSongTimePlayed.setText(null);
                        labelSongTimeTotal.setText(null);

                        if (songCollection.hasNext()) {
                            player.setAudioSource(songCollection.next());

                            player.play();
                            buttonPlayPause.setSelected(true);
                            buttonPlayPause.setIcon(ICON_PAUSE);

                            tableSongSollection.getSelectionModel().setSelectionInterval(songCollection.getCurrentIndex(), songCollection.getCurrentIndex());
                            labelSongTimeTotal.setText(PlayerUtils.toString(songCollection.getCurrentAudioSource().getDuration()));
                        }
                    });
                }
        );

        buttonPlayPause.addActionListener(event -> {
            if (buttonPlayPause.isSelected()) {
                buttonPlayPause.setIcon(ICON_PAUSE);

                if (!player.isPlaying()) {
                    if (tableSongSollection.getSelectedRow() > -1) {
                        songCollection.setCurrentIndex(tableSongSollection.getSelectedRow());
                    }

                    player.setAudioSource(songCollection.getCurrentAudioSource());
                    player.play();
                    tableSongSollection.getSelectionModel().setSelectionInterval(songCollection.getCurrentIndex(), songCollection.getCurrentIndex());

                    labelSongTimeTotal.setText(PlayerUtils.toString(songCollection.getCurrentAudioSource().getDuration()));
                }
                else {
                    player.resume();
                }
            }

            if (!buttonPlayPause.isSelected()) {
                buttonPlayPause.setIcon(ICON_PLAY);

                if (player.isPlaying()) {
                    player.pause();
                }
            }
        });

        buttonStop.addActionListener(event -> {
            player.stop();

            buttonPlayPause.setSelected(false);
            buttonPlayPause.setIcon(ICON_PLAY);

            labelSongTimePlayed.setText(null);
            labelSongTimeTotal.setText(null);
        });

        buttonBackward.addActionListener(event -> {
            if (songCollection.hasPrevious()) {
                if (player.isPlaying()) {
                    player.stop();
                    buttonPlayPause.setSelected(false);
                    buttonPlayPause.setIcon(ICON_PLAY);
                }

                player.setAudioSource(songCollection.previous());

                player.play();
                buttonPlayPause.setSelected(true);
                buttonPlayPause.setIcon(ICON_PAUSE);

                tableSongSollection.getSelectionModel().setSelectionInterval(songCollection.getCurrentIndex(), songCollection.getCurrentIndex());
                labelSongTimeTotal.setText(PlayerUtils.toString(songCollection.getCurrentAudioSource().getDuration()));
            }
        });

        buttonForward.addActionListener(event -> {
            if (songCollection.hasNext()) {
                if (player.isPlaying()) {
                    player.stop();
                    buttonPlayPause.setSelected(false);
                    buttonPlayPause.setIcon(ICON_PLAY);
                }

                player.setAudioSource(songCollection.next());

                player.play();
                buttonPlayPause.setSelected(true);
                buttonPlayPause.setIcon(ICON_PAUSE);

                tableSongSollection.getSelectionModel().setSelectionInterval(songCollection.getCurrentIndex(), songCollection.getCurrentIndex());
                labelSongTimeTotal.setText(PlayerUtils.toString(songCollection.getCurrentAudioSource().getDuration()));
            }
        });

        tableSongSollection.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent event) {
                if (event.getClickCount() != 2) {
                    return;
                }

                final Point point = event.getPoint();
                final int row = tableSongSollection.rowAtPoint(point);
                final int selectedRow = tableSongSollection.convertRowIndexToModel(row);

                player.stop();
                buttonPlayPause.setSelected(false);
                buttonPlayPause.setIcon(ICON_PLAY);

                songCollection.setCurrentIndex(selectedRow);
                player.setAudioSource(songCollection.getCurrentAudioSource());

                player.play();
                buttonPlayPause.setSelected(true);
                buttonPlayPause.setIcon(ICON_PAUSE);

                labelSongTimeTotal.setText(PlayerUtils.toString(songCollection.getCurrentAudioSource().getDuration()));
            }
        });
    }

    private void initPlayerControl() {
        buttonPlayPause = new JToggleButton(ICON_PLAY);
        buttonPlayPause.setFocusable(false);

        buttonStop = new JButton(ICON_STOP);
        buttonStop.setFocusable(false);

        buttonForward = new JButton(ImageFactory.getIcon("images/media-forward-white.svg"));
        buttonForward.setFocusable(false);

        buttonBackward = new JButton(ImageFactory.getIcon("images/media-backward-white.svg"));
        buttonBackward.setFocusable(false);
    }

    private void initSearch() {
        textFieldSearch = new JTextField();

        final TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(tableSongSollection.getModel());
        rowSorter.addRowSorterListener(event -> {
            if (rowSorter.getViewRowCount() > 0 && tableSongSollection.getSelectedRowCount() == 0) {
                tableSongSollection.setRowSelectionInterval(0, 0);
            }
        });

        tableSongSollection.setRowSorter(rowSorter);

        textFieldSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(final DocumentEvent event) {
                updateFilter();
            }

            @Override
            public void insertUpdate(final DocumentEvent event) {
                updateFilter();
            }

            @Override
            public void removeUpdate(final DocumentEvent event) {
                updateFilter();
            }

            private void updateFilter() {
                final String text = textFieldSearch.getText();

                if (text == null || text.isBlank()) {
                    rowSorter.setRowFilter(null);
                }
                else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // ignore case
                    // rowSorter.setRowFilter(new RegExRowFilter(text, Pattern.CASE_INSENSITIVE, List.of(1)));
                }
            }
        });
    }

    private void initSpectrum() {
        spectrumView = new SpectrumView();

        final SpectrumDspProcessor spectrumDspProcessor = new SpectrumDspProcessor(spectrumView::updateChartData);
        ApplicationContext.getPlayer().addProcessor(spectrumDspProcessor);

        spectrumView.getComponent().setPreferredSize(new Dimension(1, 75));
    }

    private JScrollPane initTableSongCollection() {
        final SongCollection songCollection = ApplicationContext.getSongCollection();

        if (!(songCollection instanceof TableModel)) {
            throw new IllegalArgumentException("SongCollection must be instanceof TableModel");
        }

        final TableModelSongCollection tableModelSongCollection = (TableModelSongCollection) songCollection;

        tableSongSollection = new JTable(tableModelSongCollection) {
            @Override
            public void tableChanged(final TableModelEvent event) {
                super.tableChanged(event);

                if (event.getType() == TableModelEvent.INSERT) {
                    scrollRectToVisible(getCellRect(getRowCount() - 1, 0, true));
                }
            }
        };
        tableSongSollection.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSongSollection.setDefaultRenderer(Object.class, new TableCellRendererSongCollection());
        tableSongSollection.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        final TableColumnModel columnModel = tableSongSollection.getColumnModel();
        columnModel.getColumn(0).setMinWidth(250);
        columnModel.getColumn(0).setWidth(250);
        // columnModel.getColumn(0).setPreferredWidth(250);

        for (int col = 3; col < tableSongSollection.getColumnModel().getColumnCount(); col++) {
            switch (col) {
                case 4:
                    // SampleRate
                    columnModel.getColumn(col).setMinWidth(110);
                    columnModel.getColumn(col).setMaxWidth(110);
                    break;
                case 5, 8, 9:
                    // BitRate, Disk, Track
                    columnModel.getColumn(col).setMinWidth(80);
                    columnModel.getColumn(col).setMaxWidth(80);
                    break;
                case 11:
                    // Genre
                    columnModel.getColumn(col).setMinWidth(150);
                    columnModel.getColumn(col).setMaxWidth(1000);
                    break;
                default:
                    columnModel.getColumn(col).setMinWidth(90);
                    // columnModel.getColumn(i).setWidth(90);
                    // columnModel.getColumn(i).setPreferredWidth(90);
                    columnModel.getColumn(col).setMaxWidth(90);
            }
        }

        tableSongSollection.getTableHeader().setReorderingAllowed(false);

        tableModelSongCollection.addTableModelListener(event -> {
            final String duration = PlayerUtils.toString(songCollection.getDurationTotal());

            labelSongsTotal.setText("%d Songs, %s".formatted(songCollection.size(), duration));
        });

        final JScrollPane scrollPaneSongCollection = new JScrollPane();
        scrollPaneSongCollection.setViewportView(tableSongSollection);

        return scrollPaneSongCollection;
    }

    private void initTimeLine() {
        sliderTimeLine = new JSlider(0, 100);
        sliderTimeLine.setEnabled(false);

        labelSongTimePlayed = new JLabel();
        labelSongTimePlayed.setHorizontalAlignment(SwingConstants.RIGHT);
        labelSongTimePlayed.setPreferredSize(new Dimension(65, 25));

        labelSongTimeTotal = new JLabel();
        labelSongTimeTotal.setPreferredSize(new Dimension(65, 25));

        final TimeLineDspProcessor timeLineDspProcessor = new TimeLineDspProcessor(progress -> {
            final int value = (int) Math.round(progress * 100D);

            if (value == sliderTimeLine.getValue()) {
                return;
            }

            sliderTimeLine.setValue(value);

            final AudioSource audioSource = ApplicationContext.getSongCollection().getCurrentAudioSource();
            final Duration timeTotal = audioSource.getDuration();
            final Duration timePlayed = Duration.ofMillis((long) (timeTotal.toMillis() * progress));
            labelSongTimePlayed.setText(PlayerUtils.toString(timePlayed));

            if (progress > 0.9D && Boolean.FALSE.equals(labelSongTimePlayed.getClientProperty("playCount"))) {
                labelSongTimePlayed.putClientProperty("playCount", Boolean.TRUE);

                ApplicationContext.getRepository().updateSongPlayCount(audioSource.getUri(), audioSource.getPlayCount() + 1);
                audioSource.incrementPlayCount();
                ((AbstractTableModel) tableSongSollection.getModel()).fireTableCellUpdated(tableSongSollection.getSelectedRow(), 10);
            }
            else if (progress < 0.9D) {
                labelSongTimePlayed.putClientProperty("playCount", Boolean.FALSE);
            }
        });
        ApplicationContext.getPlayer().addProcessor(timeLineDspProcessor);
    }

    private void initVolumeControl() {
        // Min: -80.0 dB
        // Max: 6.0206 dB
        sliderVolumeControl = new JSlider(-40, 7, 0);
        sliderVolumeControl.setSnapToTicks(false);
        sliderVolumeControl.setMajorTickSpacing(5);
        sliderVolumeControl.setMinorTickSpacing(5);
        sliderVolumeControl.setLabelTable(new Hashtable<>(Map.of(-40, new JLabel("0"), 7, new JLabel("100"))));
        sliderVolumeControl.setPaintLabels(true);
        sliderVolumeControl.setPaintTicks(false);

        sliderVolumeControl.addChangeListener(event -> {
                    if (sliderTimeLine.getValueIsAdjusting()) {
                        // System.out.println("getValueIsAdjusting " + sliderVolumeControl.getValue());

                        return;
                    }

                    final int value = sliderVolumeControl.getValue();
                    // System.out.printf("%d%n", value);

                    // final double db = PlayerUtils.linearToDB(value);
                    // System.out.printf("%d - %f%n", sliderVolumeControl.getValue(), db);

                    ApplicationContext.getPlayer().configureVolumeControl(volumeControl -> {
                        if (value > volumeControl.getMaximum()) {
                            volumeControl.setValue(volumeControl.getMaximum());
                        }
                        else if (value < volumeControl.getMinimum()) {
                            volumeControl.setValue(volumeControl.getMinimum());
                        }
                        else {
                            volumeControl.setValue(value);
                        }
                    });
                }
        );
    }
}
