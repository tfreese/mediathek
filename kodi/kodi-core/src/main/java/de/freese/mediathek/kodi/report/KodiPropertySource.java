package de.freese.mediathek.kodi.report;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jspecify.annotations.NonNull;
import org.springframework.core.env.PropertySource;

/**
 * @author Thomas Freese
 * @since 25.04.2020
 */
class KodiPropertySource extends PropertySource<Properties> {
    KodiPropertySource() throws IOException {
        super("reportProperties", new Properties());

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("kodi.properties")) {
            getSource().load(inputStream);
        }
    }

    @Override
    public Object getProperty(final @NonNull String name) {
        return getSource().getProperty(name);
    }
}
