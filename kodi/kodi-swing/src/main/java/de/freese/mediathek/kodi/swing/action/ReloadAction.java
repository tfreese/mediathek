// Created: 28.09.2014
package de.freese.mediathek.kodi.swing.action;

import java.awt.event.ActionEvent;
import java.io.Serial;

import javax.swing.AbstractAction;

import de.freese.mediathek.kodi.swing.panel.Panel;

/**
 * @author Thomas Freese
 */
public class ReloadAction extends AbstractAction
{
    @Serial
    private static final long serialVersionUID = -8049703427783670993L;

    private final Panel panel;

    public ReloadAction(final Panel panel)
    {
        super("Reload");

        this.panel = panel;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent event)
    {
        this.panel.reload();
    }
}
