// Created: 13 Juli 2024
package de.freese.player.test.ui.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.input.AudioSourceFactory;
import de.freese.player.core.model.AudioCodec;
import de.freese.player.core.player.DefaultClipPlayer;
import de.freese.player.core.player.Player;

/**
 * @author Thomas Freese
 */
public final class MusicPlayer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MusicPlayer.class);

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            final MusicPlayer application = new MusicPlayer();

            try {
                application.init(args);
            }
            catch (RuntimeException ex) {
                throw ex;
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private MusicPlayer() {
        super();
    }

    private JPanel createPlayerPanel(final AudioCodec audioCodec, final Player player) {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        final JToggleButton buttonPlay = new JToggleButton("Play " + audioCodec);
        buttonPlay.addActionListener(event -> player.play());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(buttonPlay, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        final JToggleButton buttonPauseResume = new JToggleButton("Pause " + audioCodec);
        buttonPauseResume.addActionListener(event -> {
            buttonPlay.setSelected(false);

            if (buttonPauseResume.isSelected()) {
                player.pause();
            }
            else {
                player.resume();
                buttonPlay.setSelected(true);
            }
        });
        panel.add(buttonPauseResume, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        final JButton buttonStop = new JButton("Stop " + audioCodec);
        buttonStop.addActionListener(event -> {
            buttonPlay.setSelected(false);
            buttonPauseResume.setSelected(false);

            player.stop();
        });
        panel.add(buttonStop, gbc);

        return panel;
    }

    private void init(final String[] args) throws Exception {
        final JFrame frame = new JFrame("Music Player");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                executorService.close();
            }
        });
        frame.setLayout(new GridBagLayout());

        frame.setSize(800, 600);
        // frame.setSize(1280, 768);
        // frame.setSize(1280, 1024);
        // frame.setSize(1680, 1050);
        // frame.setSize(1920, 1080);
        // frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        int row = 0;

        for (AudioCodec audioCodec : AudioCodec.values()) {
            try {
                final GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = row;
                gbc.fill = GridBagConstraints.BOTH;

                final Player player = new DefaultClipPlayer(executorService, Path.of(System.getProperty("java.io.tmpdir"), ".music-player"));
                player.setAudioSource(AudioSourceFactory.createAudioSource(Path.of("samples/sample." + audioCodec.getFileExtension())));
                frame.add(createPlayerPanel(audioCodec, player), gbc);
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }

            row++;
        }
    }
}
