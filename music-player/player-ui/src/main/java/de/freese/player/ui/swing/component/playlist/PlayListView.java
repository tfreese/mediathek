// Created: 14 Sept. 2024
package de.freese.player.ui.swing.component.playlist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.exception.PlayerException;
import de.freese.player.ui.ApplicationContext;
import de.freese.player.ui.model.PlayList;
import de.freese.player.ui.swing.component.GbcBuilder;
import de.freese.player.ui.utils.image.ImageFactory;

/**
 * @author Thomas Freese
 */
public final class PlayListView {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayListView.class);

    private final JList<PlayList> jList;
    private final JTextArea jTextAreaWhereClause;
    private final JTextField jTextFieldName;
    private final JPanel panel = new JPanel(new GridBagLayout());

    public PlayListView() {
        super();

        final JLabel jLabel = new JLabel("PlayList");
        jLabel.setIcon(ImageFactory.getIcon("images/info-white.svg"));
        jLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        panel.add(jLabel, GbcBuilder.of(0, 0).gridwidth(3).fillHorizontal().anchorWest());

        try {
            final URL url = Thread.currentThread().getContextClassLoader().getResource("music-player.sql");
            final String tooltip;

            final Color labelForeground = jLabel.getForeground();
            final String foregroundStyle = "rgb(%d,%d,%d)".formatted(labelForeground.getRed(), labelForeground.getGreen(), labelForeground.getBlue());

            try (Stream<String> stream = Files.lines(Path.of(url.toURI()))) {
                tooltip = stream.takeWhile(line -> !line.isBlank()).collect(Collectors.joining("<br>", "<html><body style=\"color:" + foregroundStyle + ";\">", "</body></html"));
            }

            jLabel.setToolTipText(tooltip);
        }
        catch (Exception ex) {
            throw new PlayerException(ex);
        }

        // jLabel = new JLabel(ImageFactory.getIcon("images/info-white.svg"));
        // panel.add(jLabel, GbcBuilder.of(1, 0).anchorWest());

        final JButton jButtonAdd = new JButton("Add");
        jButtonAdd.setFocusable(false);
        jButtonAdd.addActionListener(event -> addPlayList());
        panel.add(jButtonAdd, GbcBuilder.of(3, 0).fillNone().anchorEast());

        final JButton jButtonRemove = new JButton("Remove");
        jButtonRemove.setFocusable(false);
        jButtonRemove.setEnabled(false);
        jButtonRemove.addActionListener(event -> removePlayList());
        panel.add(jButtonRemove, GbcBuilder.of(4, 0).fillNone().anchorEast());

        final DefaultListModel<PlayList> listModel = new DefaultListModel<>();
        jList = new JList<>(listModel);
        jList.setCellRenderer(new PlayListListCellRenderer());
        jList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        final JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(jList);
        jScrollPane.setPreferredSize(new Dimension(500, 100));
        panel.add(jScrollPane, GbcBuilder.of(0, 1).gridwidth(5).fillBoth());

        jTextFieldName = new JTextField();
        jTextFieldName.setEnabled(false);
        jTextFieldName.setBorder(BorderFactory.createTitledBorder("Name"));
        jTextFieldName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent event) {
                jList.getSelectedValue().setName(jTextFieldName.getText());
                jList.repaint();
            }
        });
        panel.add(jTextFieldName, GbcBuilder.of(0, 2).gridwidth(5).fillBoth());

        jTextAreaWhereClause = new JTextArea();
        jTextAreaWhereClause.setEnabled(false);
        jTextAreaWhereClause.setBorder(BorderFactory.createTitledBorder("Where Clause"));
        jTextAreaWhereClause.setPreferredSize(new Dimension(500, 200));
        jTextAreaWhereClause.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent event) {
                jList.getSelectedValue().setWhereClause(jTextAreaWhereClause.getText());
            }
        });
        panel.add(jTextAreaWhereClause, GbcBuilder.of(0, 3).gridwidth(5).fillBoth());

        final JButton jButtonSave = new JButton("Save");
        jButtonSave.setFocusable(false);
        jButtonSave.setEnabled(false);
        jButtonSave.addActionListener(event -> save());
        panel.add(jButtonSave, GbcBuilder.of(0, 4).gridwidth(5).anchorCenter());

        jList.addListSelectionListener(event -> {
            if (event.getValueIsAdjusting()) {
                return;
            }

            final PlayList playList = jList.getSelectedValue();

            jButtonRemove.setEnabled(playList != null);
            jButtonSave.setEnabled(playList != null);
            jTextFieldName.setEnabled(playList != null);
            jTextAreaWhereClause.setEnabled(playList != null);

            if (playList == null) {
                jTextFieldName.setText(null);
                jTextAreaWhereClause.setText(null);
                return;
            }

            jTextFieldName.setText(playList.getName());
            jTextAreaWhereClause.setText(playList.getWhereClause());
        });

        // listModel.addListDataListener(new ListDataListener() {
        //     @Override
        //     public void contentsChanged(final ListDataEvent e) {
        //         jButtonSave.setEnabled(listModel.getSize() > 0);
        //     }
        //
        //     @Override
        //     public void intervalAdded(final ListDataEvent e) {
        //         contentsChanged(null);
        //     }
        //
        //     @Override
        //     public void intervalRemoved(final ListDataEvent e) {
        //         contentsChanged(null);
        //     }
        // });
        listModel.addAll(ApplicationContext.getRepository().getPlayLists());
    }

    public JComponent getComponent() {
        return panel;
    }

    private void addPlayList() {
        final PlayList playList = new PlayList();
        playList.setName("New PlayList");
        playList.setWhereClause("1 = 1");

        ((DefaultListModel<PlayList>) jList.getModel()).addElement(playList);

        jList.setSelectedValue(playList, true);
    }

    private void removePlayList() {
        final PlayList playList = jList.getSelectedValue();

        if (playList == null) {
            return;
        }

        ApplicationContext.getRepository().deletePlayList(playList.getName());
    }

    private void save() {
        final PlayList playList = jList.getSelectedValue();

        if (playList == null) {
            return;
        }

        ApplicationContext.getRepository().saveOrUpdatePlayList(playList);
    }
}
