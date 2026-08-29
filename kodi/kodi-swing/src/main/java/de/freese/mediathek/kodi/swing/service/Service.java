package de.freese.mediathek.kodi.swing.service;

import org.springframework.context.ApplicationContext;

import de.freese.mediathek.kodi.api.MediaService;

/**
 * @author Thomas Freese
 * @since 28.12.2022
 */
public interface Service {
    ApplicationContext getApplicationContext();

    default MediaService getMediaService() {
        return getApplicationContext().getBean(MediaService.class);
    }
}
