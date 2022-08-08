// Created: 25.04.2020
package de.freese.mediathek.kodi.report;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.env.PropertySource;

/**
 * @author Thomas Freese
 */
class KodiPropertySource extends PropertySource<Properties>
{
    /**
     * Erstellt ein neues {@link KodiPropertySource} Object.
     *
     * @throws IOException Falls was schiefgeht.
     */
    public KodiPropertySource() throws IOException
    {
        super("reportProperties", new Properties());

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("kodi.properties"))
        {
            getSource().load(inputStream);
        }
    }

    /**
     * @see org.springframework.core.env.PropertySource#getProperty(java.lang.String)
     */
    @Override
    public Object getProperty(final String name)
    {
        return getSource().getProperty(name);
    }
}
