// Created: 18 Aug. 2024
package de.freese.player.swing;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.PlayerSettings;
import de.freese.player.player.DefaultDspPlayer;
import de.freese.player.player.DefaultPlayList;
import de.freese.player.player.DspPlayer;
import de.freese.player.player.PlayList;
import de.freese.player.swing.component.PlayerPanel;

/**
 * @author Thomas Freese
 */
public final class PlayerFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerFrame.class);

    private static JFrame frame;
    private static PlayList playList;
    private static DspPlayer player;

    private static final class MainFrameListener extends WindowAdapter {
        @Override
        public void windowClosing(final WindowEvent event) {
            stop();
        }
    }

    public static JFrame getFrame() {
        return frame;
    }

    static void init() throws Exception {
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

        playList = new DefaultPlayList();
        player = new DefaultDspPlayer();

        final PlayerPanel playerPanel = new PlayerPanel();
        playerPanel.init(playList, player);
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

    static void start() throws Exception {
        LOGGER.info("starting application");

        ((DefaultPlayList) playList)
                .addAudioSource(Path.of("samples/sample.wav").toUri())
                .addAudioSource(Path.of("samples/sample.flac").toUri())
                .addAudioSource(Path.of("samples/sample.aif").toUri())
                .addAudioSource(Path.of("samples/sample.au").toUri())
        ;

        frame.setVisible(true);
    }

    static void stop() {
        LOGGER.info("stopping application");

        if (player.isPlaying()) {
            player.stop();
        }

        PlayerSettings.getExecutorService().close();
        PlayerSettings.getExecutorServicePipeReader().close();

        frame = null;

        System.exit(0);
    }

    private PlayerFrame() {
        super();
    }
}
