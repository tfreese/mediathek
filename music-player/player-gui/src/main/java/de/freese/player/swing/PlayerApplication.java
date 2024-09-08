// Created: 18 Aug. 2024
package de.freese.player.swing;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.ApplicationContext;
import de.freese.player.library.LibraryRepository;
import de.freese.player.library.LibraryScanner;
import de.freese.player.swing.component.PlayerPanel;

/**
 * @author Thomas Freese
 */
public final class PlayerApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerApplication.class);

    private static JFrame frame;

    private static final class MainFrameListener extends WindowAdapter {
        @Override
        public void windowClosing(final WindowEvent event) {
            stop();
        }
    }

    public static JFrame getFrame() {
        return frame;
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

        final PlayerPanel playerPanel = new PlayerPanel();
        playerPanel.init();
        jFrame.setContentPane(playerPanel.getComponent());

        // frame.setSize(800, 600);
        // frame.setSize(1280, 768);
        // frame.setSize(1280, 1024);
        // frame.setSize(1680, 1050);
        jFrame.setSize(1920, 1080);
        // jFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        jFrame.setLocationRelativeTo(null);

        frame = jFrame;
    }

    public static void start() throws Exception {
        LOGGER.info("starting application");

        ApplicationContext.getExecutorService().execute(() -> {
            // try {
            //     TimeUnit.SECONDS.sleep(3);
            // }
            // catch (InterruptedException ex) {
            //     // Restore interrupted state.
            //     Thread.currentThread().interrupt();
            // }

            final LibraryRepository libraryRepository = ApplicationContext.getLibraryRepository();
            final LibraryScanner libraryScanner = new LibraryScanner(libraryRepository);
            libraryScanner.scan(Set.of(Path.of("samples")));

            // libraryRepository.load(ApplicationContext.getPlayList()::addAudioSource);
            libraryRepository.load(audioSource -> {
                // try {
                //     TimeUnit.SECONDS.sleep(1);
                // }
                // catch (InterruptedException ex) {
                //     // Restore interrupted state.
                //     Thread.currentThread().interrupt();
                // }

                ApplicationContext.getPlayList().addAudioSource(audioSource);
            });
        });

        frame.setVisible(true);
    }

    private static void stop() {
        LOGGER.info("stopping application");

        ApplicationContext.stop();

        frame = null;

        System.exit(0);
    }

    private PlayerApplication() {
        super();
    }
}
