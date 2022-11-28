// Created: 26.04.2014
package de.freese.mediathek.services.themoviedb.api;

import de.freese.mediathek.services.themoviedb.model.Configuration;

/**
 * Interface für den allgemeinen Zugriff auf die API.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface AccountService
{
    /**
     * Liefert die Konfiguration des Accounts,<br>
     * Pfade, Bildergrößen etc.
     */
    Configuration getConfiguration();
}
