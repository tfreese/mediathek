// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.components;

import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.swing.GbcBuilder;
import de.freese.mediathek.kodi.swing.components.list.DefaultListListModel;
import de.freese.mediathek.kodi.swing.components.list.GenreListCellRenderer;

/**
 * @author Thomas Freese
 */
public class GenreDialog extends JDialog {
    @Serial
    private static final long serialVersionUID = -4384289197484325624L;

    /**
     * @author Thomas Freese
     */
    private final class CancelAction extends AbstractAction {
        @Serial
        private static final long serialVersionUID = -5694877960473295271L;

        private CancelAction() {
            super("Cancel");
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            GenreDialog.this.canceled = true;
            close();
        }
    }

    /**
     * @author Thomas Freese
     */
    private final class OKAction extends AbstractAction {
        @Serial
        private static final long serialVersionUID = -7913222539942539301L;

        private OKAction() {
            super("OK");
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            GenreDialog.this.canceled = false;
            close();
        }
    }

    /**
     * @author Thomas Freese
     */
    private final class ToLeftAction extends AbstractAction {
        @Serial
        private static final long serialVersionUID = 3818570430470000410L;

        ToLeftAction() {
            super("<");
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            final Genre genre = GenreDialog.this.listRechts.getSelectedValue();

            if (genre == null) {
                return;
            }

            final DefaultListListModel<Genre> listModel = (DefaultListListModel<Genre>) GenreDialog.this.listRechts.getModel();

            if (listModel.getSize() == 1) {
                return;
            }

            listModel.remove(genre);
        }
    }

    /**
     * @author Thomas Freese
     */
    private final class ToRightAction extends AbstractAction {
        @Serial
        private static final long serialVersionUID = 3818570430470000410L;

        ToRightAction() {
            super(">");
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            final Genre genre = GenreDialog.this.listLinks.getSelectedValue();

            if (genre == null) {
                return;
            }

            final DefaultListListModel<Genre> listModel = (DefaultListListModel<Genre>) GenreDialog.this.listRechts.getModel();

            if (listModel.contains(genre)) {
                return;
            }

            listModel.add(genre);
        }
    }

    private boolean canceled;
    private JList<Genre> listLinks;
    private JList<Genre> listRechts;

    public GenreDialog(final Window owner) {
        super(owner);

        setTitle("Genre Auswahl");
        setModal(true);
        // setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    public void close() {
        dispose();
    }

    public List<Genre> getSelectedGenres() {
        if (hasBeenCanceled()) {
            return Collections.emptyList();
        }

        final DefaultListListModel<Genre> defaultListListModel = (DefaultListListModel<Genre>) this.listRechts.getModel();

        return defaultListListModel.getStream().toList();
    }

    public boolean hasBeenCanceled() {
        return this.canceled;
    }

    public void open(final List<Genre> links, final List<Genre> rechts) {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // Links
        this.listLinks = new JList<>(new DefaultListListModel<>(links));
        this.listLinks.setCellRenderer(new GenreListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(this.listLinks);
        scrollPane.setBorder(new TitledBorder("Verfügbar"));
        panel.add(scrollPane, GbcBuilder.of(0, 0).gridHeight(10).gridWidth(2).fillBoth());

        // Buttons
        JButton button = new JButton(new ToRightAction());
        panel.add(button, GbcBuilder.of(2, 3).fillVertical().anchorCenter());
        button = new JButton(new ToLeftAction());
        panel.add(button, GbcBuilder.of(2, 7).fillVertical().anchorCenter());

        // Rechts
        this.listRechts = new JList<>(new DefaultListListModel<>(rechts));
        this.listRechts.setCellRenderer(new GenreListCellRenderer());
        scrollPane = new JScrollPane(this.listRechts);
        scrollPane.setBorder(new TitledBorder("Auswahl"));
        panel.add(scrollPane, GbcBuilder.of(3, 0).gridHeight(10).gridWidth(2).fillBoth());

        button = new JButton(new OKAction());
        panel.add(button, GbcBuilder.of(0, 11).gridWidth(2).fillHorizontal());
        button = new JButton(new CancelAction());
        panel.add(button, GbcBuilder.of(3, 11).gridWidth(2).fillHorizontal());

        setContentPane(panel);
        setSize(600, 600);
        // pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
