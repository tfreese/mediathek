/**
 * Created: 14.09.2014
 */
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
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import de.freese.mediathek.kodi.spring.AppConfigSQLite;
import de.freese.mediathek.kodi.swing.panel.GenrePanel;
import de.freese.mediathek.kodi.swing.panel.IPanel;
import de.freese.mediathek.kodi.swing.panel.MoviePanel;
import de.freese.mediathek.kodi.swing.panel.ShowPanel;

/**
 * Um Comparator Fehler zu vermeiden.<br>
 * -Djava.util.Arrays.useLegacyMergeSort=true<br>
 * System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
 *
 * @author Thomas Freese
 */
public class KODISwingClient
{
    /**
     * WindowListener zum beenden.
     *
     * @author Thomas Freese
     */
    private class MainFrameListener extends WindowAdapter
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

    /**
     *
     */
    public static Frame FRAME = null;

    /**
     *
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("KODI-Client");

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // Um Comparator Fehler zu vermeiden.
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

        SwingUtilities.invokeLater(() -> {
            KODISwingClient main = new KODISwingClient();

            try
            {
                main.init(args);
            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }
        });
    }

    /**
     * Erstellt ein neues {@link KODISwingClient} Object.
     */
    public KODISwingClient()
    {
        super();
    }

    /**
     * Initialisierung der GUI.
     *
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    private void init(final String[] args) throws Exception
    {
        String profile = null;

        if (ArrayUtils.isEmpty(args))
        {
            profile = "sqlite";
        }
        else
        {
            profile = args[0];
        }

        // AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(clazz);
        @SuppressWarnings("resource")
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.getEnvironment().setActiveProfiles(profile);
        // ctx.register(AppConfigMySQL.class, AppConfigHSQLDB.class, AppConfigSQLite.class);
        ctx.register(AppConfigSQLite.class);
        ctx.refresh();
        ctx.registerShutdownHook();

        initUIDefaults();

        JFrame frame = new JFrame();
        frame.setTitle("KODI-Client");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new MainFrameListener());
        frame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        IPanel panel = new ShowPanel(ctx);
        tabbedPane.addTab("Serien", panel.buildPanel());

        panel = new MoviePanel(ctx);
        tabbedPane.addTab("Filme", panel.buildPanel());

        panel = new GenrePanel(ctx);
        tabbedPane.addTab("Genres", panel.buildPanel());

        frame.add(tabbedPane, BorderLayout.CENTER);

        // frame.setSize(800, 600);
        frame.setSize(1280, 768);
        // frame.setSize(1280, 1024);
        // frame.setSize(1680, 1050);
        // frame.setSize(1920, 1080);
        // frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        FRAME = frame;
    }

    /**
     *
     */
    private void initUIDefaults()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            LOGGER.error(null, ex);
        }

        UIManager.put("FileChooser.useSystemIcons", Boolean.TRUE);

        // Farben
        Color color = new Color(215, 215, 215);
        UIManager.put("Table.alternatingBackground", color);
        UIManager.put("Table.alternateRowColor", color);
        UIManager.put("List.alternatingBackground", color);
        // defaults.put("Tree.alternatingBackground", color);

        // Fonts: Dialog, Monospaced, Arial, DejaVu Sans
        Font font = new Font("DejaVu Sans", Font.PLAIN, 16);

        UIManager.getLookAndFeelDefaults().entrySet().stream().forEach(entry -> {
            Object key = entry.getKey();
            Object value = entry.getValue();

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
        Font font_bold = font.deriveFont(Font.BOLD);
        UIManager.put("TitledBorder.font", font_bold);

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
