// Created: 14.09.2014
package de.freese.mediathek.kodi.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.plaf.FontUIResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.freese.mediathek.kodi.spring.AppConfigSqLite;
import de.freese.mediathek.kodi.swing.controller.Controller;
import de.freese.mediathek.kodi.swing.controller.GenreController;
import de.freese.mediathek.kodi.swing.controller.MovieController;
import de.freese.mediathek.kodi.swing.controller.ShowController;
import de.freese.mediathek.kodi.swing.service.GenreService;
import de.freese.mediathek.kodi.swing.service.MovieService;
import de.freese.mediathek.kodi.swing.service.ShowService;
import de.freese.mediathek.kodi.swing.view.GenreView;
import de.freese.mediathek.kodi.swing.view.MovieView;
import de.freese.mediathek.kodi.swing.view.ShowView;

/**
 * @author Thomas Freese
 */
public class KodiSwingClient {
    private static final Logger LOGGER = LoggerFactory.getLogger("KODI-Client");

    private static Frame frame;

    private static final class MainFrameListener extends WindowAdapter {
        @Override
        public void windowClosing(final WindowEvent event) {
            System.exit(0);
        }
    }

    public static Frame getFrame() {
        return frame;
    }

    public static void main(final String[] args) throws Exception {
        // To avoid Comparator Errors.
        //        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

        SwingUtilities.invokeLater(() -> {
            final KodiSwingClient application = new KodiSwingClient();

            try {
                application.init(args);
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        // Runnable task = () -> {
        // KodiSwingClient application = new KodiSwingClient();
        //
        // try
        // {
        // application.init(args);
        // }
        // catch (Exception ex)
        // {
        // throw new RuntimeException(ex);
        // }
        // };
        // task.run();

        // // Eigene ThreadGroup für Handling von Runtime-Exceptions.
        // ThreadGroup threadGroup = new ThreadGroup("jsync");
        //
        // // Kein Thread des gesamten Clients kann eine höhere Prio haben.
        // threadGroup.setMaxPriority(Thread.NORM_PRIORITY + 1);
        //
        // Thread thread = new Thread(threadGroup, task, "JSyncJavaFx-Startup");
        // // thread.setDaemon(false);
        // thread.start();
    }

    private void init(final String[] args) {
        String profile = null;

        if (args == null || args.length == 0) {
            profile = "sqlite";
        }
        else {
            profile = args[0];
        }

        // AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(clazz);
        final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.getEnvironment().setActiveProfiles(profile);
        // ctx.register(AppConfigMySQL.class, AppConfigHsqlDb.class, AppConfigSqLite.class);
        ctx.register(AppConfigSqLite.class);
        ctx.refresh();
        ctx.registerShutdownHook();

        initUIDefaults();

        final ResourceBundle resourceBundle = ResourceBundle.getBundle("de.freese.mediathek.kodi.swing.bundles.MyResources", Locale.getDefault());

        final JFrame frame = new JFrame();
        frame.setTitle(resourceBundle.getString("frame.title"));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new MainFrameListener());
        frame.setLayout(new BorderLayout());

        final JTabbedPane tabbedPane = new JTabbedPane();

        Controller controller = new ShowController(new ShowService(ctx), new ShowView());
        tabbedPane.addTab(resourceBundle.getString("shows"), controller.init(resourceBundle));

        controller = new MovieController(new MovieService(ctx), new MovieView());
        tabbedPane.addTab(resourceBundle.getString("movies"), controller.init(resourceBundle));

        controller = new GenreController(new GenreService(ctx), new GenreView());
        tabbedPane.addTab(resourceBundle.getString("genres"), controller.init(resourceBundle));

        frame.add(tabbedPane, BorderLayout.CENTER);

        // frame.setSize(800, 600);
        // frame.setSize(1280, 768);
        // frame.setSize(1280, 1024);
        // frame.setSize(1680, 1050);
        frame.setSize(1920, 1080);
        // frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        KodiSwingClient.frame = frame;
    }

    private void initUIDefaults() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        UIManager.put("FileChooser.useSystemIcons", Boolean.TRUE);

        // Farben
        // Color color = new Color(215, 215, 215); // Für helles L&F
        final Color color = new Color(60, 60, 60);  // Für dunkles L&F
        UIManager.put("Table.alternatingBackground", color);
        UIManager.put("Table.alternateRowColor", color);
        UIManager.put("List.alternatingBackground", color);
        // defaults.put("Tree.alternatingBackground", color);

        // Fonts: Dialog, Monospaced, Arial, DejaVu Sans
        final Font font = new Font("DejaVu Sans", Font.PLAIN, 16);

        UIManager.getLookAndFeelDefaults().forEach((key, value) -> {
            if (value instanceof FontUIResource) {
                UIManager.put(key, new FontUIResource(font));
            }

            // String keyString = key.toString();
            //
            // if (keyString.endsWith(".font") || keyString.endsWith(".acceleratorFont"))
            // {
            // UIManager.put(key, new FontUIResource(font));
            // }
        });

        // Ausnahmen
        final Font fontBold = font.deriveFont(Font.BOLD);
        UIManager.put("TitledBorder.font", fontBold);

        // UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        //
        // SortedSet<Object> uiKeys = new TreeSet<>(new Comparator<Object>()
        // {
        // @Override
        // public int compare(final Object o1, final Object o2)
        // {
        // return o1.toString().compareTo(o2.toString());
        // }
        //
        // });
        // uiKeys.addAll(defaults.keySet());
        //
        // String format = "%1$s \t %2$s %n";
        //
        // for (Object key : uiKeys)
        // {
        // Object value = defaults.get(key);
        //
        // System.out.printf(format, key.toString(), (value != null) ? value.toString() : "NULL");
        // }
    }
}
