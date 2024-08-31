// Created: 18 Aug. 2024
package de.freese.player.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.PlayerSettings;
import de.freese.player.player.DefaultDspPlayer;
import de.freese.player.player.DspPlayer;
import de.freese.player.player.PlayList;
import de.freese.player.swing.spectrum.SpectrumDspProcessor;
import de.freese.player.swing.spectrum.SpectrumPanel;
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

    static void init() throws Exception {
        LOGGER.info("initialize application");

        final JFrame jFrame = new JFrame();
        jFrame.setTitle("Music-Player");
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.addWindowListener(new MainFrameListener());
        jFrame.setLayout(new BorderLayout());

        // jFrame.add(new JLabel(ImageFactory.getIcon("images/media-play-white.svg")), BorderLayout.NORTH);
        // jFrame.add(new JLabel(ImageFactory.getIcon("images/media-play-black.svg")), BorderLayout.SOUTH);

        final PlayList playList = new PlayList()
                .addAudioSource(Path.of("samples/sample.wav").toUri())
                .addAudioSource(Path.of("samples/sample.flac").toUri())
                // .addAudioSource(Path.of("samples/sample.aif").toUri())
                // .addAudioSource(Path.of("samples/sample.au").toUri())
                ;

        final DspPlayer player = new DefaultDspPlayer();
        player.setAudioSource(playList.next());

        final JButton buttonPlay = new JButton(ImageFactory.getIcon("images/media-play-white.svg"));
        buttonPlay.setToolTipText("Play");
        buttonPlay.addActionListener(event -> player.play());

        final JButton buttonPause = new JButton(ImageFactory.getIcon("images/media-pause-white.svg"));
        buttonPause.setToolTipText("Pause");
        buttonPause.addActionListener(event -> player.pause());

        final JButton buttonResume = new JButton("Resume");
        buttonResume.setToolTipText("Resume");
        buttonResume.addActionListener(event -> player.resume());

        final JButton buttonStop = new JButton(ImageFactory.getIcon("images/media-stop-white.svg"));
        buttonStop.setToolTipText("Stop");
        buttonStop.addActionListener(event -> player.stop());

        final JPanel playerControls = new JPanel(new FlowLayout());
        playerControls.add(buttonPlay);
        playerControls.add(buttonPause);
        playerControls.add(buttonResume);
        playerControls.add(buttonStop);
        jFrame.add(playerControls, BorderLayout.CENTER);

        final SpectrumPanel spectrumPanel = new SpectrumPanel();
        player.addProcessor(new SpectrumDspProcessor(spectrumPanel::updateChartData));
        jFrame.add(spectrumPanel.getPanel(), BorderLayout.SOUTH);

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
