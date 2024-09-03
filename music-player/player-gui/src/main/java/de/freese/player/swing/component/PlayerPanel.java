// Created: 03 Sept. 2024
package de.freese.player.swing.component;

import java.awt.GridBagLayout;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;

import de.freese.player.player.PlayList;
import de.freese.player.swing.component.spectrum.SpectrumComponent;
import de.freese.player.swing.component.table.PlayListCellRenderer;
import de.freese.player.swing.component.table.PlayListTableModel;
import de.freese.player.utils.image.ImageFactory;

/**
 * @author Thomas Freese
 */
public final class PlayerPanel {
    // @Serial
    // private static final long serialVersionUID = -1L;

    private static final Icon ICON_PAUSE = ImageFactory.getIcon("images/media-pause-white.svg");
    private static final Icon ICON_PLAY = ImageFactory.getIcon("images/media-play-white.svg");
    private static final Icon ICON_STOP = ImageFactory.getIcon("images/media-stop-white.svg");

    private final JPanel panel;
    private final SpectrumComponent spectrumComponent;

    private JToggleButton buttonPlayPause;
    private JButton buttonStop;
    private JTable tablePlayList;

    public PlayerPanel() {
        super();

        panel = new JPanel(new GridBagLayout());
        spectrumComponent = new SpectrumComponent();
    }

    public JComponent getComponent() {
        return panel;
    }

    public void init(final PlayList playList) {
        initPlayerControl();

        final JScrollPane scrollPanePlayList = initTablePlayList(playList);

        // buttonPlayPause.setBorder(LineBorder.createBlackLineBorder());
        // buttonStop.setBorder(LineBorder.createBlackLineBorder());
        final JPanel playerControlPanel = new JPanel(new GridBagLayout());
        playerControlPanel.add(buttonPlayPause, GbcBuilder.of(0, 0).insets(5, 0, 0, 5));
        playerControlPanel.add(buttonStop, GbcBuilder.of(1, 0).insets(5, 5, 0, 0));

        panel.add(playerControlPanel, GbcBuilder.of(0, 0).gridwidth(2).anchorCenter().insets(0, 5, 0, 5));
        // panel.add(buttonPlayPause, GbcBuilder.of(0, 0).anchorEast().fillHorizontal());
        // panel.add(buttonStop, GbcBuilder.of(1, 0).anchorWest().fillHorizontal());
        panel.add(scrollPanePlayList, GbcBuilder.of(0, 1).gridwidth(2).weighty(10).fillBoth().insets(5, 5, 0, 5));
        panel.add(spectrumComponent.getComponent(), GbcBuilder.of(0, 2).gridwidth(2).fillHorizontal().insets(0, 5, 0, 5));
    }

    private void initPlayerControl() {
        buttonPlayPause = new JToggleButton(ICON_PLAY);
        buttonStop = new JButton(ICON_STOP);
    }

    private JScrollPane initTablePlayList(final PlayList playList) {
        final PlayListTableModel tableModel = new PlayListTableModel(playList);
        tablePlayList = new JTable(tableModel);
        tablePlayList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePlayList.setDefaultRenderer(Object.class, new PlayListCellRenderer());
        tablePlayList.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tablePlayList.getColumnModel().getColumn(0).setMinWidth(250);

        final JScrollPane scrollPanePlayList = new JScrollPane();
        scrollPanePlayList.setViewportView(tablePlayList);

        return scrollPanePlayList;
    }
}
