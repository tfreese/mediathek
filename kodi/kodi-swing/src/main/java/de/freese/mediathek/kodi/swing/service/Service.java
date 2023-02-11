// Created: 28.12.22
package de.freese.mediathek.kodi.swing.service;

import org.springframework.context.ApplicationContext;

import de.freese.mediathek.kodi.api.MediaService;

/**
 * @author Thomas Freese
 */
public interface Service {
    ApplicationContext getApplicationContext();

    default MediaService getMediaService() {
        return getApplicationContext().getBean(MediaService.class);
    }
}
