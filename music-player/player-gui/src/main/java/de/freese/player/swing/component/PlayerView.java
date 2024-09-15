// Created: 03 Sept. 2024
package de.freese.player.swing.component;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

import de.freese.player.ApplicationContext;
import de.freese.player.player.DspPlayer;
import de.freese.player.player.PlayList;
import de.freese.player.spectrum.SpectrumDspProcessor;
import de.freese.player.swing.component.spectrum.SpectrumView;
import de.freese.player.swing.component.table.PlayListCellRenderer;
import de.freese.player.utils.image.ImageFactory;

/**
 * @author Thomas Freese
 */
public final class PlayerView {
    // @Serial
    // private static final long serialVersionUID = -1L;

    private static final Icon ICON_PAUSE = ImageFactory.getIcon("images/media-pause-white.svg");
    private static final Icon ICON_PLAY = ImageFactory.getIcon("images/media-play-white.svg");
    private static final Icon ICON_STOP = ImageFactory.getIcon("images/media-stop-white.svg");

    private final JPanel panel;

    private JButton buttonBackward;
    private JButton buttonForward;
    private JToggleButton buttonPlayPause;
    private JButton buttonStop;
    private SpectrumView spectrumView;
    private JTable tablePlayList;

    public PlayerView() {
        super();

        panel = new JPanel(new GridBagLayout());
    }

    public JComponent getComponent() {
        return panel;
    }

    public void init() {
        initPlayerControl();

        final JScrollPane scrollPanePlayList = initTablePlayList();

        initSpectrum();

        // buttonPlayPause.setBorder(LineBorder.createBlackLineBorder());
        // buttonForward.setBorder(LineBorder.createBlackLineBorder());

        panel.add(scrollPanePlayList, GbcBuilder.of(0, 0).gridwidth(10).weighty(10D).fillBoth().insets(5, 5, 0, 5));

        panel.add(buttonBackward, GbcBuilder.of(0, 1).insets(0, 5, 0, 0));
        panel.add(buttonPlayPause, GbcBuilder.of(1, 1).insets(0, 0, 0, 0));
        panel.add(buttonStop, GbcBuilder.of(2, 1).insets(0, 0, 0, 0));
        panel.add(buttonForward, GbcBuilder.of(3, 1).insets(0, 0, 0, 0));
        panel.add(spectrumView.getComponent(), GbcBuilder.of(4, 1).gridwidth(2).fillHorizontal().insets(0, 0, 0, 5));

        initListener();
    }

    private void initListener() {
        final PlayList playList = ApplicationContext.getPlayList();
        final DspPlayer player = ApplicationContext.getPlayer();

        player.addSongFinishedListener(audioSource ->
                SwingUtilities.invokeLater(() -> {
                    buttonPlayPause.setSelected(false);
                    buttonPlayPause.setIcon(ICON_PLAY);

                    if (playList.hasNext()) {
                        player.setAudioSource(playList.next());

                        player.play();
                        buttonPlayPause.setSelected(true);
                        buttonPlayPause.setIcon(ICON_PAUSE);

                        tablePlayList.getSelectionModel().setSelectionInterval(playList.getCurrentIndex(), playList.getCurrentIndex());
                    }
                })
        );

        buttonPlayPause.addActionListener(event -> {
            if (buttonPlayPause.isSelected()) {
                buttonPlayPause.setIcon(ICON_PAUSE);

                if (!player.isPlaying()) {
                    player.setAudioSource(playList.getCurrentAudioSource());
                    player.play();
                    tablePlayList.getSelectionModel().setSelectionInterval(playList.getCurrentIndex(), playList.getCurrentIndex());
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
        });

        buttonBackward.addActionListener(event -> {
            if (playList.hasPrevious()) {
                if (player.isPlaying()) {
                    player.stop();
                    buttonPlayPause.setSelected(false);
                    buttonPlayPause.setIcon(ICON_PLAY);
                }

                player.setAudioSource(playList.previous());

                player.play();
                buttonPlayPause.setSelected(true);
                buttonPlayPause.setIcon(ICON_PAUSE);

                tablePlayList.getSelectionModel().setSelectionInterval(playList.getCurrentIndex(), playList.getCurrentIndex());
            }
        });

        buttonForward.addActionListener(event -> {
            if (playList.hasNext()) {
                if (player.isPlaying()) {
                    player.stop();
                    buttonPlayPause.setSelected(false);
                    buttonPlayPause.setIcon(ICON_PLAY);
                }

                player.setAudioSource(playList.next());

                player.play();
                buttonPlayPause.setSelected(true);
                buttonPlayPause.setIcon(ICON_PAUSE);

                tablePlayList.getSelectionModel().setSelectionInterval(playList.getCurrentIndex(), playList.getCurrentIndex());
            }
        });

        tablePlayList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent event) {
                if (event.getClickCount() != 2) {
                    return;
                }

                final Point point = event.getPoint();
                final int row = tablePlayList.rowAtPoint(point);
                final int selectedRow = tablePlayList.convertRowIndexToModel(row);

                player.stop();
                buttonPlayPause.setSelected(false);
                buttonPlayPause.setIcon(ICON_PLAY);

                playList.setCurrentIndex(selectedRow);
                player.setAudioSource(playList.getCurrentAudioSource());

                player.play();
                buttonPlayPause.setSelected(true);
                buttonPlayPause.setIcon(ICON_PAUSE);
            }
        });
    }

    private void initPlayerControl() {
        buttonPlayPause = new JToggleButton(ICON_PLAY);
        buttonStop = new JButton(ICON_STOP);
        buttonForward = new JButton(ImageFactory.getIcon("images/media-forward-white.svg"));
        buttonBackward = new JButton(ImageFactory.getIcon("images/media-backward-white.svg"));
    }

    private void initSpectrum() {
        spectrumView = new SpectrumView();

        final SpectrumDspProcessor spectrumDspProcessor = new SpectrumDspProcessor(spectrumView::updateChartData);
        ApplicationContext.getPlayer().addProcessor(spectrumDspProcessor);

        spectrumView.getComponent().setMinimumSize(new Dimension(1, 75));
        spectrumView.getComponent().setPreferredSize(new Dimension(1, 75));
    }

    private JScrollPane initTablePlayList() {
        final PlayList playList = ApplicationContext.getPlayList();

        if (!(playList instanceof TableModel)) {
            throw new IllegalArgumentException("PlayList must be instanceof TableModel");
        }

        tablePlayList = new JTable((TableModel) playList) {
            @Override
            public void tableChanged(final TableModelEvent event) {
                super.tableChanged(event);

                if (event.getType() == TableModelEvent.INSERT) {
                    scrollRectToVisible(getCellRect(getRowCount() - 1, 0, true));
                }
            }
        };
        tablePlayList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePlayList.setDefaultRenderer(Object.class, new PlayListCellRenderer());
        tablePlayList.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        // tablePlayList.getColumnModel().getColumn(0).setMinWidth(350);

        final JScrollPane scrollPanePlayList = new JScrollPane();
        scrollPanePlayList.setViewportView(tablePlayList);

        return scrollPanePlayList;
    }
}
