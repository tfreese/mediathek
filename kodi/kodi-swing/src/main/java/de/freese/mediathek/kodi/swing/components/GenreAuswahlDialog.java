// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.components;

import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import com.jgoodies.common.collect.ArrayListModel;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.swing.GBCBuilder;
import de.freese.mediathek.kodi.swing.components.list.GenreListCellRenderer;

/**
 * Dialog zum Auswahl der Genres.
 *
 * @author Thomas Freese
 */
public class GenreAuswahlDialog extends JDialog
{
    /**
     * @author Thomas Freese
     */
    private final class CancelAction extends AbstractAction
    {
        /**
         *
         */
        private static final long serialVersionUID = -5694877960473295271L;

        /**
         * Erstellt ein neues {@link CancelAction} Object.
         */
        private CancelAction()
        {
            super("Cancel");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(final ActionEvent e)
        {
            // AlbumEditorDialog.this.albumPresentationModel.triggerFlush();
            GenreAuswahlDialog.this.canceled = true;
            close();
        }
    }

    /**
     * @author Thomas Freese
     */
    private final class OKAction extends AbstractAction
    {
        /**
         *
         */
        private static final long serialVersionUID = -7913222539942539301L;

        /**
         * Erstellt ein neues {@link OKAction} Object.
         */
        private OKAction()
        {
            super("OK");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(final ActionEvent e)
        {
            // AlbumEditorDialog.this.albumPresentationModel.triggerCommit();
            GenreAuswahlDialog.this.canceled = false;
            close();
        }
    }

    /**
     * @author Thomas Freese
     */
    private final class ToLeftAction extends AbstractAction
    {
        /**
         *
         */
        private static final long serialVersionUID = 3818570430470000410L;

        /**
         * Erstellt ein neues {@link ToLeftAction} Object.
         */
        public ToLeftAction()
        {
            super("<");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(final ActionEvent e)
        {
            Genre genre = GenreAuswahlDialog.this.listRechts.getSelectedValue();

            if ((genre == null))
            {
                return;
            }

            ArrayListModel<Genre> listModel = (ArrayListModel<Genre>) GenreAuswahlDialog.this.listRechts.getModel();

            if (listModel.size() == 1)
            {
                return;
            }

            listModel.remove(genre);
        }
    }

    /**
     * @author Thomas Freese
     */
    private final class ToRightAction extends AbstractAction
    {
        /**
         *
         */
        private static final long serialVersionUID = 3818570430470000410L;

        /**
         * Erstellt ein neues {@link ToRightAction} Object.
         */
        public ToRightAction()
        {
            super(">");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(final ActionEvent e)
        {
            Genre genre = GenreAuswahlDialog.this.listLinks.getSelectedValue();

            if (genre == null)
            {
                return;
            }

            ArrayListModel<Genre> listModel = (ArrayListModel<Genre>) GenreAuswahlDialog.this.listRechts.getModel();

            if (listModel.contains(genre))
            {
                return;
            }

            listModel.add(genre);
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -4384289197484325624L;
    /**
     *
     */
    private boolean canceled;
    /**
     *
     */
    private JList<Genre> listLinks;
    /**
     *
     */
    private JList<Genre> listRechts;

    /**
     * Erstellt ein neues {@link GenreAuswahlDialog} Object.
     *
     * @param owner {@link Window}
     */
    public GenreAuswahlDialog(final Window owner)
    {
        super(owner);

        setTitle("Genre Auswahl");
        setModal(true);
        // setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Schließt den Dialog.
     */
    public void close()
    {
        dispose();
    }

    /**
     * Liefert die gewählten Genres.
     *
     * @return {@link List}; null wenn canceled
     */
    @SuppressWarnings("unchecked")
    public List<Genre> getSelectedGenres()
    {
        if (hasBeenCanceled())
        {
            return null;
        }

        return (List<Genre>) this.listRechts.getModel();
    }

    /**
     * Liefert TRUE/FALSE, ob der Dialog gecanceled wurde.
     *
     * @return boolean
     */
    public boolean hasBeenCanceled()
    {
        return this.canceled;
    }

    /**
     * Öffnet den Dialog.
     *
     * @param links {@link List}
     * @param rechts {@link List}
     */
    @SuppressWarnings("unchecked")
    public void open(final List<Genre> links, final List<Genre> rechts)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // Links
        this.listLinks = new JList<>(new ArrayListModel<>(links));
        this.listLinks.setCellRenderer(new GenreListCellRenderer());
        JScrollPane scrollPane = new JScrollPane(this.listLinks);
        scrollPane.setBorder(new TitledBorder("Verfügbar"));
        panel.add(scrollPane, new GBCBuilder(0, 0).gridheight(10).gridwidth(2).fillBoth());

        // Buttons
        JButton button = new JButton(new ToRightAction());
        panel.add(button, new GBCBuilder(2, 3).fillVertical().anchorCenter());
        button = new JButton(new ToLeftAction());
        panel.add(button, new GBCBuilder(2, 7).fillVertical().anchorCenter());

        // Rechts
        this.listRechts = new JList<>(new ArrayListModel<>(rechts));
        this.listRechts.setCellRenderer(new GenreListCellRenderer());
        scrollPane = new JScrollPane(this.listRechts);
        scrollPane.setBorder(new TitledBorder("Auswahl"));
        panel.add(scrollPane, new GBCBuilder(3, 0).gridheight(10).gridwidth(2).fillBoth());

        button = new JButton(new OKAction());
        panel.add(button, new GBCBuilder(0, 11).gridwidth(2).fillHorizontal());
        button = new JButton(new CancelAction());
        panel.add(button, new GBCBuilder(3, 11).gridwidth(2).fillHorizontal());

        setContentPane(panel);
        setSize(600, 600);
        // pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
