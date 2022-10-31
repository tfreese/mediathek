// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.components;

import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.Serial;
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
import de.freese.mediathek.kodi.swing.GbcBuilder;
import de.freese.mediathek.kodi.swing.components.list.GenreListCellRenderer;

/**
 * Dialog zur Auswahl der Genres.
 *
 * @author Thomas Freese
 */
public class GenreAuswahlDialog extends JDialog
{
    @Serial
    private static final long serialVersionUID = -4384289197484325624L;

    /**
     * @author Thomas Freese
     */
    private final class CancelAction extends AbstractAction
    {
        @Serial
        private static final long serialVersionUID = -5694877960473295271L;

        private CancelAction()
        {
            super("Cancel");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(final ActionEvent event)
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
        @Serial
        private static final long serialVersionUID = -7913222539942539301L;

        private OKAction()
        {
            super("OK");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(final ActionEvent event)
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
        @Serial
        private static final long serialVersionUID = 3818570430470000410L;

        public ToLeftAction()
        {
            super("<");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(final ActionEvent event)
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
        @Serial
        private static final long serialVersionUID = 3818570430470000410L;

        public ToRightAction()
        {
            super(">");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(final ActionEvent event)
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

    private boolean canceled;

    private JList<Genre> listLinks;

    private JList<Genre> listRechts;

    public GenreAuswahlDialog(final Window owner)
    {
        super(owner);

        setTitle("Genre Auswahl");
        setModal(true);
        // setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    public void close()
    {
        dispose();
    }

    public List<Genre> getSelectedGenres()
    {
        if (hasBeenCanceled())
        {
            return null;
        }

        return (List<Genre>) this.listRechts.getModel();
    }

    public boolean hasBeenCanceled()
    {
        return this.canceled;
    }

    public void open(final List<Genre> links, final List<Genre> rechts)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // Links
        this.listLinks = new JList<>(new ArrayListModel<>(links));
        this.listLinks.setCellRenderer(new GenreListCellRenderer());
        JScrollPane scrollPane = new JScrollPane(this.listLinks);
        scrollPane.setBorder(new TitledBorder("Verfügbar"));
        panel.add(scrollPane, new GbcBuilder(0, 0).gridHeight(10).gridWidth(2).fillBoth());

        // Buttons
        JButton button = new JButton(new ToRightAction());
        panel.add(button, new GbcBuilder(2, 3).fillVertical().anchorCenter());
        button = new JButton(new ToLeftAction());
        panel.add(button, new GbcBuilder(2, 7).fillVertical().anchorCenter());

        // Rechts
        this.listRechts = new JList<>(new ArrayListModel<>(rechts));
        this.listRechts.setCellRenderer(new GenreListCellRenderer());
        scrollPane = new JScrollPane(this.listRechts);
        scrollPane.setBorder(new TitledBorder("Auswahl"));
        panel.add(scrollPane, new GbcBuilder(3, 0).gridHeight(10).gridWidth(2).fillBoth());

        button = new JButton(new OKAction());
        panel.add(button, new GbcBuilder(0, 11).gridWidth(2).fillHorizontal());
        button = new JButton(new CancelAction());
        panel.add(button, new GbcBuilder(3, 11).gridWidth(2).fillHorizontal());

        setContentPane(panel);
        setSize(600, 600);
        // pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
