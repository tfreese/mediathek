// Created: 18 Aug. 2024
package de.freese.player.swing;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.PlayerSettings;
import de.freese.player.utils.image.ImageFactory;

/**
 * @author Thomas Freese
 */
public final class PlayerFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerFrame.class);

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

    static void init(final String[] args) throws Exception {
        LOGGER.info("initialize application");

        final JFrame jFrame = new JFrame();
        jFrame.setTitle("Music-Player");
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.addWindowListener(new MainFrameListener());
        jFrame.setLayout(new BorderLayout());

        jFrame.add(new JLabel(ImageFactory.getIcon("images/media-play-white.svg")), BorderLayout.NORTH);
        jFrame.add(new JLabel(ImageFactory.getIcon("images/media-play-black.svg")), BorderLayout.SOUTH);

        // frame.setSize(800, 600);
        // frame.setSize(1280, 768);
        // frame.setSize(1280, 1024);
        // frame.setSize(1680, 1050);
        jFrame.setSize(1920, 1080);
        // jFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        jFrame.setLocationRelativeTo(null);

        frame = jFrame;
    }

    static void start() {
        LOGGER.info("starting application");

        frame.setVisible(true);
    }

    static void stop() {
        LOGGER.info("stopping application");

        PlayerSettings.getExecutorService().close();
        PlayerSettings.getExecutorServicePipeReader().close();

        frame = null;

        System.exit(0);
    }

    private PlayerFrame() {
        super();
    }
}
