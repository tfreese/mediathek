/**
 * Created: 05.04.2020
 */

package de.freese.mediathek.musik;

import java.io.Closeable;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import de.freese.base.utils.StopWatch;
import de.freese.mediathek.report.MediaReporter;

/**
 * @author Thomas Freese
 */
public final class MusikReporter
{
    /**
    *
    */
    private static final StopWatch STOP_WATCH = new StopWatch();

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // MediaReporter mediaReporter = new BansheeMediaReporter();
        MediaReporter mediaReporter = new ClementineMediaReporter();
        // MediaReporter mediaReporter = new KodiMediaReporter();
        // MediaReporter mediaReporter = new PlexMediaReporter();

        STOP_WATCH.start("connect");
        DataSource dataSource = mediaReporter.createDataSource(true);
        STOP_WATCH.stop();

        try
        {
            STOP_WATCH.start("writeReport");

            Path path = Paths.get("/home/tommy/dokumente/linux/musik-playcount.csv");

            mediaReporter.writeReport(dataSource, path);
            // mediaReporter.updateDbFromReport(dataSource, path);

            STOP_WATCH.stop();
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
            System.exit(-1);
        }
        finally
        {
            STOP_WATCH.start("disconnect");

            if (dataSource instanceof SingleConnectionDataSource)
            {
                ((SingleConnectionDataSource) dataSource).destroy();
            }
            else if (dataSource instanceof Closeable)
            {
                ((Closeable) dataSource).close();
            }

            STOP_WATCH.stop();
        }

        STOP_WATCH.prettyPrint(System.out);
        System.exit(0);
    }

    /**
     * Erstellt ein neues {@link MusikReporter} Object.
     */
    private MusikReporter()
    {
        super();
    }
}
