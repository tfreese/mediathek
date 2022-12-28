// Created: 28.12.22
package de.freese.mediathek.kodi.swing.view;

import java.awt.Component;

import de.freese.mediathek.kodi.swing.controller.Controller;

/**
 * @author Thomas Freese
 */
public interface View
{
    Component init();

    void link(Controller controller);
}
