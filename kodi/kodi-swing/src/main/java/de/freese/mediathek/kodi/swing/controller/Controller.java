package de.freese.mediathek.kodi.swing.controller;

import java.awt.Component;
import java.util.ResourceBundle;

/**
 * @author Thomas Freese
 * @since 28.12.2022
 */
@FunctionalInterface
public interface Controller {
    Component init(ResourceBundle resourceBundle);
}
