// Created: 14.09.2014
package de.freese.mediathek.kodi.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.plaf.FontUIResource;

import de.freese.mediathek.kodi.spring.AppConfigSqLite;
import de.freese.mediathek.kodi.swing.panel.GenrePanel;
import de.freese.mediathek.kodi.swing.panel.MoviePanel;
import de.freese.mediathek.kodi.swing.panel.Panel;
import de.freese.mediathek.kodi.swing.panel.ShowPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Um Comparator Fehler zu vermeiden.<br>
 * -Djava.util.Arrays.useLegacyMergeSort=true<br>
 * System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
 *
 * @author Thomas Freese
 */
public class KodiSwingClient
{
    public static final Logger LOGGER = LoggerFactory.getLogger("KODI-Client");

    private static class MainFrameListener extends WindowAdapter
    {
        /**
         * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
         */
        @Override
        public void windowClosing(final WindowEvent e)
        {
            System.exit(0);
        }
    }

    public static Frame FRAME;

    public static void main(final String[] args) throws Exception
    {
        // Um Comparator Fehler zu vermeiden.
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

        SwingUtilities.invokeLater(() ->
        {
            KodiSwingClient application = new KodiSwingClient();

            try
            {
                application.init(args);
            }
            catch (Exception ex)
            {
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

    private void init(final String[] args) throws Exception
    {
        String profile = null;

        if (args == null || args.length == 0)
        {
            profile = "sqlite";
        }
        else
        {
            profile = args[0];
        }

        // AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(clazz);
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.getEnvironment().setActiveProfiles(profile);
        // ctx.register(AppConfigMySQL.class, AppConfigHsqlDb.class, AppConfigSqLite.class);
        ctx.register(AppConfigSqLite.class);
        ctx.refresh();
        ctx.registerShutdownHook();

        initUIDefaults();

        JFrame frame = new JFrame();
        frame.setTitle("KODI-Client");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new MainFrameListener());
        frame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        Panel panel = new ShowPanel(ctx);
        tabbedPane.addTab("Serien", panel.buildPanel());

        panel = new MoviePanel(ctx);
        tabbedPane.addTab("Filme", panel.buildPanel());

        panel = new GenrePanel(ctx);
        tabbedPane.addTab("Genres", panel.buildPanel());

        frame.add(tabbedPane, BorderLayout.CENTER);

        // frame.setSize(800, 600);
        // frame.setSize(1280, 768);
        // frame.setSize(1280, 1024);
        // frame.setSize(1680, 1050);
        frame.setSize(1920, 1080);
        // frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        FRAME = frame;
    }

    private void initUIDefaults()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            LOGGER.error(ex.getMessage(), ex);
        }

        UIManager.put("FileChooser.useSystemIcons", Boolean.TRUE);

        // Farben
        // Color color = new Color(215, 215, 215); // Für helles L&F
        Color color = new Color(60, 60, 60);  // Für dunkles L&F
        UIManager.put("Table.alternatingBackground", color);
        UIManager.put("Table.alternateRowColor", color);
        UIManager.put("List.alternatingBackground", color);
        // defaults.put("Tree.alternatingBackground", color);

        // Fonts: Dialog, Monospaced, Arial, DejaVu Sans
        Font font = new Font("DejaVu Sans", Font.PLAIN, 16);

        UIManager.getLookAndFeelDefaults().forEach((key, value) ->
        {
            if (value instanceof FontUIResource)
            {
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
        Font fontBold = font.deriveFont(Font.BOLD);
        UIManager.put("TitledBorder.font", fontBold);

        // UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        //
        // SortedSet<Object> uiKeys = new TreeSet<>(new Comparator<Object>()
        // {
        // /**
        // * @param o1 Object
        // * @param o2 Object
        // * @return int
        // */
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
