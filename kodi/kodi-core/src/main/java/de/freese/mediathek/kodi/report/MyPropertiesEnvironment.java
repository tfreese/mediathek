/**
 * Created: 05.04.2020
 */

package de.freese.mediathek.kodi.report;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.PropertyPlaceholderHelper;
import de.freese.mediathek.kodi.spring.AbstractAppConfig;

/**
 * Manuelles {@link Environment} für die Implementierungen von {@link AbstractAppConfig}.<br>
 * Siehe: org.springframework.mock.env.MockEnvironment
 *
 * @author Thomas Freese
 */
class MyPropertiesEnvironment extends AbstractEnvironment
{
    /**
     *
     */
    private final PropertyPlaceholderHelper pph = new PropertyPlaceholderHelper("${", "}");

    /**
     *
     */
    private final Properties properties = new Properties();

    /**
     * Erstellt ein neues {@link MyPropertiesEnvironment} Object.
     *
     * @throws IOException Falls was schief geht.
     */
    public MyPropertiesEnvironment() throws IOException
    {
        super();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("kodi.properties"))
        {
            this.properties.load(inputStream);
        }
    }

    /**
     * @see org.springframework.core.env.AbstractEnvironment#getProperty(java.lang.String)
     */
    @Override
    public String getProperty(final String key)
    {
        String property = this.properties.getProperty(key);
        property = this.pph.replacePlaceholders(property, this.properties);

        return property;
    }

    /**
     * @see org.springframework.core.env.AbstractEnvironment#getProperty(java.lang.String, java.lang.String)
     */
    @Override
    public String getProperty(final String key, final String defaultValue)
    {
        String property = this.properties.getProperty(key, defaultValue);
        property = this.pph.replacePlaceholders(property, this.properties);

        return property;
    }

    /**
     * @param key String
     * @param value String
     */
    @SuppressWarnings("unused")
    public void setProperty(final String key, final String value)
    {
        this.properties.setProperty(key, value);
    }
}