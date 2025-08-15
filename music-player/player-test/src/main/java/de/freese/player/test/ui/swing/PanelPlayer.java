// Created: 15 Aug. 2025
package de.freese.player.test.ui.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.model.Window;
import de.freese.player.core.player.AudioPlayerSink;
import de.freese.player.core.player.AudioPlayerSource;
import de.freese.player.core.player.DefaultAudioPlayerSink;

/**
 * @author Thomas Freese
 */
public final class PanelPlayer extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(PanelPlayer.class);

    @Serial
    private static final long serialVersionUID = 896585557622293595L;
    private final Map<Path, AudioPlayerSource> audioPlayerSourceMap = new HashMap<>();
    private final AtomicBoolean runner = new AtomicBoolean(false);
    private Future<?> future;

    PanelPlayer(final ExecutorService executorService, final Path tempPath) {
        super();

        setLayout(new GridBagLayout());

        List<Path> samples = List.of();

        try (Stream<Path> stream = Files.walk(Path.of("samples"), 1)) {
            samples = stream.filter(p -> p.toString().contains("sample."))
                    .sorted()
                    .toList();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        for (int i = 0; i < samples.size(); i++) {
            final Path sample = samples.get(i);
            // final Path sample = Path.of("samples/sample." + audioCodec.getFileExtension());

            final JLabel jLabel = new JLabel(sample.toString());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(5, 0, 0, 5);
            add(jLabel, gbc);

            final JToggleButton jButtonPlayPause = new JToggleButton("Play/Pause");
            final JButton jButtonStop = new JButton("Stop");
            jButtonStop.setEnabled(false);

            jButtonPlayPause.addActionListener(event -> {
                if (jButtonPlayPause.isSelected()) {
                    LOGGER.info("play");

                    final AudioPlayerSource audioPlayerSource = audioPlayerSourceMap.computeIfAbsent(sample, key -> {
                        try {
                            return AudioPlayerSource.of(sample, tempPath);
                        }
                        catch (Exception ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }

                        return null;
                    });
                    play(executorService, audioPlayerSource);

                    jButtonStop.setEnabled(true);
                }
                else {
                    LOGGER.info("pause");
                    pause();

                    jButtonStop.setEnabled(true);
                }
            });
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = i;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(5, 0, 0, 5);
            add(jButtonPlayPause, gbc);

            jButtonStop.addActionListener(event -> {
                LOGGER.info("stop");
                pause();

                jButtonPlayPause.setSelected(false);
                jButtonStop.setEnabled(false);
            });
            gbc = new GridBagConstraints();
            gbc.gridx = 2;
            gbc.gridy = i;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(5, 0, 0, 5);
            add(jButtonStop, gbc);
        }
    }

    private void pause() {
        runner.set(false);

        if (future != null) {
            future.cancel(true);
            future = null;
        }
    }

    private void play(final ExecutorService executorService, final AudioPlayerSource audioPlayerSource) {
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
