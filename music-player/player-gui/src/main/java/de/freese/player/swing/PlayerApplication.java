// Created: 18 Aug. 2024
package de.freese.player.swing;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingWorker;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.ApplicationContext;
import de.freese.player.input.AudioSource;
import de.freese.player.swing.component.PlayerView;
import de.freese.player.swing.component.library.LibraryView;

/**
 * @author Thomas Freese
 */
public final class PlayerApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerApplication.class);

    private static JFrame mainFrame;

    private static final class MainFrameListener extends WindowAdapter {
        @Override
        public void windowClosing(final WindowEvent event) {
            stop();
        }
    }

    public static JFrame getMainFrame() {
        return mainFrame;
    }

    public static void init() throws Exception {
        LOGGER.info("initialize application");

        // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        final UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        // defaults.get("Table.alternateRowColor");
        defaults.putIfAbsent("Table.alternateRowColor", Color.LIGHT_GRAY);

        final JFrame jFrame = new JFrame();
        jFrame.setTitle("Music-Player");
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.addWindowListener(new MainFrameListener());
        // jFrame.setLayout(new BorderLayout());

        final PlayerView playerView = new PlayerView();
        playerView.init();
        jFrame.setContentPane(playerView.getComponent());

        initMenu(jFrame);

        // frame.setSize(800, 600);
        // frame.setSize(1280, 768);
        // frame.setSize(1280, 1024);
        // frame.setSize(1680, 1050);
        jFrame.setSize(1920, 1080);
        // jFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        jFrame.setLocationRelativeTo(null);

        mainFrame = jFrame;

        // Runtime.getRuntime().addShutdownHook(new Thread(PlayerApplication::stop, "Shutdown"));
    }

    public static void start() throws Exception {
        LOGGER.info("starting application");

        mainFrame.setVisible(true);

        final SwingWorker<Void, AudioSource> swingWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                ApplicationContext.getLibraryRepository().load(this::publish);

                return null;
            }

            @Override
            protected void process(final List<AudioSource> chunks) {
                ApplicationContext.getPlayList().addAudioSources(chunks);
            }
        };
        ApplicationContext.getExecutorService().execute(swingWorker);
    }

    private static void initMenu(final JFrame frame) {
        final JMenuItem jMenuItemLibrary = new JMenuItem("Edit");
        jMenuItemLibrary.addActionListener(event -> {
            final JDialog jDialog = new JDialog(getMainFrame(), "Library", true);
            jDialog.setContentPane(new LibraryView().getComponent());
            jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            jDialog.pack();
            jDialog.setLocationRelativeTo(null);
            jDialog.setVisible(true);
        });

        final JMenu jMenuLibrary = new JMenu("Library");
        jMenuLibrary.add(jMenuItemLibrary);

        final JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.add(jMenuLibrary);
        frame.setJMenuBar(jMenuBar);
    }

    private static void stop() {
        LOGGER.info("stopping application");

        ApplicationContext.stop();

        mainFrame = null;

        System.exit(0);
    }

    private PlayerApplication() {
        super();
    }
}
