// Created: 03 Sept. 2024
package de.freese.player.ui.swing.component;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import de.freese.player.core.input.AudioSource;
import de.freese.player.core.player.DspPlayer;
import de.freese.player.core.player.SongCollection;
import de.freese.player.core.util.PlayerUtils;
import de.freese.player.ui.ApplicationContext;
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
    private SpectrumView spectrumView;
    private JTable tableSongSollection;

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

        initSpectrum();
        initTimeLine();

        // buttonPlayPause.setBorder(LineBorder.createBlackLineBorder());
        // buttonForward.setBorder(LineBorder.createBlackLineBorder());

        panel.add(scrollPaneSongCollection, GbcBuilder.of(0, 0).gridwidth(10).weighty(10D).fillBoth().insets(5, 5, 0, 5));

        panel.add(buttonBackward, GbcBuilder.of(0, 1).insets(0, 5, 0, 0));
        panel.add(buttonPlayPause, GbcBuilder.of(1, 1).insets(0, 0, 0, 0));
        panel.add(buttonStop, GbcBuilder.of(2, 1).insets(0, 0, 0, 0));
        panel.add(buttonForward, GbcBuilder.of(3, 1).insets(0, 0, 0, 0));
        panel.add(spectrumView.getComponent(), GbcBuilder.of(4, 1).gridwidth(6).fillHorizontal().insets(0, 0, 0, 5));

        panel.add(labelSongsTotal, GbcBuilder.of(0, 2).anchorCenter().gridwidth(4).insets(5, 0, 0, 0));

        panel.add(labelSongTimePlayed, GbcBuilder.of(4, 2).anchorEast().insets(5, 5, 0, 0));
        panel.add(sliderTimeLine, GbcBuilder.of(5, 2).anchorCenter().gridwidth(4).fillHorizontal().insets(5, 0, 0, 0));
        panel.add(labelSongTimeTotal, GbcBuilder.of(9, 2).anchorWest().insets(5, 0, 0, 0));

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
        tableSongSollection.getColumnModel().getColumn(0).setMinWidth(250);

        // Duration
        tableSongSollection.getColumnModel().getColumn(3).setMinWidth(85);
        tableSongSollection.getColumnModel().getColumn(3).setMaxWidth(85);

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
        sliderTimeLine.setSnapToTicks(false);
        sliderTimeLine.setMinorTickSpacing(5);
        // sliderTimeLine.setLabelTable(jSlider.createStandardLabels(100));
        // sliderTimeLine.setPaintLabels(false);
        // sliderTimeLine.setPaintTicks(false);
        sliderTimeLine.setValueIsAdjusting(true);
        // sliderTimeLine.addChangeListener(event -> {
        //             // Runs every time the Knob moves. :-/
        //             final AudioSource audioSource = ApplicationContext.getSongCollection().getCurrentAudioSource();
        //             final Duration timeTotal = audioSource.getDuration();
        //             final Duration timeTarget = Duration.ofMillis(timeTotal.toMillis() * (sliderTimeLine.getValue() / 100));
        //
        //             ApplicationContext.getPlayer().jumpTo(timeTarget);
        //         }
        // );        // sliderTimeLine.addChangeListener(event -> {
        //             // Runs every time the Knob moves. :-/
        //             final AudioSource audioSource = ApplicationContext.getSongCollection().getCurrentAudioSource();
        //             final Duration timeTotal = audioSource.getDuration();
        //             final Duration timeTarget = Duration.ofMillis(timeTotal.toMillis() * (sliderTimeLine.getValue() / 100));
        //
        //             ApplicationContext.getPlayer().jumpTo(timeTarget);
        //         }
        // );

        labelSongTimePlayed = new JLabel();
        labelSongTimePlayed.setHorizontalAlignment(SwingConstants.RIGHT);
        labelSongTimePlayed.setPreferredSize(new Dimension(65, 25));

        labelSongTimeTotal = new JLabel();
        labelSongTimeTotal.setPreferredSize(new Dimension(65, 25));

        final TimeLineDspProcessor timeLineDspProcessor = new TimeLineDspProcessor(progress -> {
            sliderTimeLine.setValue((int) Math.round(progress * 100D));

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
}
