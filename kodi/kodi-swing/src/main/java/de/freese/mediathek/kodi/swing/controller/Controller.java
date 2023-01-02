// Created: 28.12.22
package de.freese.mediathek.kodi.swing.controller;

import de.freese.mediathek.kodi.swing.service.Service;
import de.freese.mediathek.kodi.swing.view.View;

/**
 * @author Thomas Freese
 */
public interface Controller
{
    Service getService();

    View getView();
}
