// Created: 28.12.22
package de.freese.mediathek.kodi.swing.view;

import java.awt.Component;
import java.util.ResourceBundle;

import de.freese.mediathek.kodi.swing.controller.Controller;

/**
 * @author Thomas Freese
 */
public interface View
{
    Component init(Controller controller, ResourceBundle resourceBundle);
}
