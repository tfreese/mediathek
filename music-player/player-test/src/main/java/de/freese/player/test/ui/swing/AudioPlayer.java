// Created: 13 Juli 2024
package de.freese.player.test.ui.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.input.AudioSource;
import de.freese.player.core.input.AudioSourceFactory;
import de.freese.player.core.model.AudioCodec;
import de.freese.player.core.model.Window;
import de.freese.player.core.player.AudioPlayerSink;
import de.freese.player.core.player.AudioPlayerSource;
import de.freese.player.core.player.DefaultAudioPlayerSink;

/**
 * @author Thomas Freese
 */
public final class AudioPlayer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AudioPlayer.class);

    public static void main(final String[] args) {
        // AudioSystem.getTargetEncodings(AudioFormat.Encoding.PCM_SIGNED);
        // AudioSystem.getTargetFormats(AudioFormat.Encoding.PCM_SIGNED, DefaultAudioPlayerSink.getTargetAudioFormat());

        SwingUtilities.invokeLater(() -> {
            try {
                final AudioPlayer application = new AudioPlayer();
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

    private final List<AudioSource> audioSources = new ArrayList<>();
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private final AtomicBoolean runner = new AtomicBoolean(false);
    private Future<?> future;

    private AudioPlayer() {
        super();
    }

    private JPanel createPlayerPanel(final AudioCodec audioCodec, final AudioPlayerSource audioPlayerSource) {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        final JToggleButton buttonPlay = new JToggleButton("Play " + audioCodec);
        buttonPlay.addActionListener(event -> {
            LOGGER.info("play");

            play(audioPlayerSource);
        });

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
                LOGGER.info("pause");
                pause();
            }
            else {
                LOGGER.info("resume");

                play(audioPlayerSource);

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

            LOGGER.info("stop");

            pause();
        });
        panel.add(buttonStop, gbc);

        return panel;
    }

    private void init(final String[] args) throws Exception {
        final JFrame frame = new JFrame("Audio Player");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                executorService.close();

                audioSources.forEach(audioSource -> {
                    if (audioSource.getTmpFile() != null) {
                        try {
                            Files.delete(audioSource.getTmpFile());
                        }
                        catch (IOException ex) {
                            LOGGER.error(ex.getMessage());
                        }
                    }
                });
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

                final AudioSource audioSource = AudioSourceFactory.createAudioSource(Path.of("samples/sample." + audioCodec.getFileExtension()));
                audioSources.add(audioSource);
                frame.add(createPlayerPanel(audioCodec, AudioPlayerSource.of(audioSource)), gbc);
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }

            row++;
        }
    }

    private void pause() {
        runner.set(false);

        if (future != null) {
            future.cancel(true);
            future = null;
        }
    }

    private void play(final AudioPlayerSource audioPlayerSource) {
        future = executorService.submit(() -> {
                    runner.set(true);
                    
                    final AudioPlayerSink audioPlayerSink = new DefaultAudioPlayerSink(audioPlayerSource.getAudioFormat());
                    audioPlayerSink.configureVolumeControl(volumeControl -> volumeControl.setValue(volumeControl.getMaximum()));

                    while (runner.get()) {
                        final Window window = audioPlayerSource.nextWindow();

                        if (window != null) {
                            audioPlayerSink.play(window);
                        }
                        else {
                            return;
                        }
                    }
                }
        );
    }
}
