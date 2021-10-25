// Created: 05.04.2020
package de.freese.mediathek.kodi.report;

import java.io.Closeable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import de.freese.mediathek.report.MediaReporter;

/**
 * @author Thomas Freese
 */
public final class KodiReporter
{
    /**
     * @param args String[]
     *
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        MediaReporter mediaReporterAudio = new KodiAudioReporter();
        MediaReporter mediaReporterVideo = new KodiVideoReporter();

        DataSource dataSourceAudio = mediaReporterAudio.createDataSource(true);
        DataSource dataSourceVideo = mediaReporterVideo.createDataSource(true);

        try
        {
            // Path path = Paths.get("/home/tommy/dokumente/kodi");
            Path path = Paths.get("/tmp/kodi");

            mediaReporterAudio.writeReport(dataSourceAudio, path);
            // dataSourceAudio.updateDbFromReport(dataSourceAudio, path);

            mediaReporterVideo.writeReport(dataSourceVideo, path);
            // mediaReporterVideo.updateDbFromReport(dataSourceVideo, path);
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
            System.exit(-1);
        }
        finally
        {
            for (DataSource dataSource : Arrays.asList(dataSourceAudio, dataSourceVideo))
            {
                try
                {
                    if (dataSource instanceof SingleConnectionDataSource dc)
                    {
                        dc.destroy();
                    }
                    else if (dataSource instanceof Closeable c)
                    {
                        c.close();
                    }
                    else if (dataSource instanceof AutoCloseable ac)
                    {
                        ac.close();
                    }
                }
                catch (Exception ex2)
                {
                    System.err.println(ex2.getMessage());
                }
            }
        }

        System.exit(0);
    }

    /**
     * Erstellt ein neues {@link KodiReporter} Object.
     */
    private KodiReporter()
    {
        super();
    }
}
