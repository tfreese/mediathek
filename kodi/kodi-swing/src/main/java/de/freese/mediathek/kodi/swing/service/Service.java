// Created: 28.12.22
package de.freese.mediathek.kodi.swing.service;

import de.freese.mediathek.kodi.api.MediaService;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public interface Service
{
    ApplicationContext getApplicationContext();

    default MediaService getMediaService()
    {
        return getApplicationContext().getBean(MediaService.class);
    }
}
