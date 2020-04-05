/**
 * Created: 05.04.2020
 */

package de.freese.mediathek.musik;

import java.io.Closeable;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.util.StopWatch;
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

        DataSource dataSource = mediaReporter.createDataSource(true);

        try
        {
            STOP_WATCH.start("writeReport");

            Path path = Paths.get("/home/tommy/dokumente/linux/musik-playcount.csv");

            mediaReporter.writeReport(dataSource, path);
            // mediaReporter.updateDbFromReport(dataSource, path);

            STOP_WATCH.stop();
            // System.out.println(STOP_WATCH.prettyPrint());
            System.out.printf("Stopwatch: %s, %d ms%n", STOP_WATCH.getLastTaskName(), STOP_WATCH.getLastTaskTimeMillis());
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

        // System.out.println(STOP_WATCH.prettyPrint());
        System.out.printf("Stopwatch: %s, %d ms%n", STOP_WATCH.getLastTaskName(), STOP_WATCH.getLastTaskTimeMillis());
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
