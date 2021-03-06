/**
 * Created: 28.09.2014
 */

package de.freese.mediathek.kodi.swing.action;

import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import de.freese.mediathek.kodi.swing.panel.IPanel;

/**
 * {@link Action} zum Neuladen der Daten.
 *
 * @author Thomas Freese
 */
public class ReloadAction extends AbstractAction
{
    /**
     *
     */
    private static final long serialVersionUID = -8049703427783670993L;

    /**
     *
     */
    private final IPanel panel;

    /**
     * Erstellt ein neues {@link ReloadAction} Object.
     * 
     * @param panel {@link IPanel}
     */
    public ReloadAction(final IPanel panel)
    {
        super("Reload");

        this.panel = panel;

    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e)
    {
        this.panel.reload();
    }
}
