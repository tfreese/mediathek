// Created: 15 Aug. 2025
package de.freese.player.test.ui.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import javax.sound.sampled.AudioFormat;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.player.AudioPlayerSink;
import de.freese.player.core.player.DefaultAudioPlayerSink;
import de.freese.player.core.signal.DecayPulse;
import de.freese.player.core.signal.EchoPulse;
import de.freese.player.core.signal.FmSweep;
import de.freese.player.core.signal.SawWave;
import de.freese.player.core.signal.Signal;
import de.freese.player.core.signal.SineWave;
import de.freese.player.core.signal.SquareWave;
import de.freese.player.core.signal.StereoPanning;
import de.freese.player.core.signal.StereoPingPong;
import de.freese.player.core.signal.Tones;
import de.freese.player.core.signal.WhiteNoise;

/**
 * @author Thomas Freese
 */
public final class PanelSignal extends JPanel {
    private static final AudioFormat AUDIO_FORMAT = new AudioFormat(8_000.0F,
            16,
            2,
            true,
            true);
    private static final Logger LOGGER = LoggerFactory.getLogger(PanelSignal.class);

    @Serial
    private static final long serialVersionUID = -637609813587161972L;
    private final JLabel elapsedTimeMeter = new JLabel("Duration:");

    PanelSignal(final ExecutorService executorService) {
        super();

        setLayout(new GridBagLayout());

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 0, 0, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        add(elapsedTimeMeter, gbc);

        final List<Supplier<JButton>> buttons = new ArrayList<>();
        buttons.add(() -> {
            final JButton button = new JButton("Decay Pulse");
            button.addActionListener(event -> play(new DecayPulse(Set.of(500D, 750D, 1000D)), AUDIO_FORMAT, executorService));
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("Echo Pulse");
            button.addActionListener(event -> play(new EchoPulse(Set.of(500D, 750D, 1000D)), AUDIO_FORMAT, executorService));
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("FM Sweep");
            button.addActionListener(event -> play(new FmSweep(), AUDIO_FORMAT, executorService));
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("Stereo Panning");
            button.addActionListener(event -> play(new StereoPanning(1000D), AUDIO_FORMAT, executorService));
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("Stereo Pingpong");
            button.addActionListener(event -> play(new StereoPingPong(1000D), AUDIO_FORMAT, executorService));
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("Tones");
            button.addActionListener(event -> play(new Tones(Set.of(500D, 1000D)), AUDIO_FORMAT, executorService));
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("SineWave");
            button.addActionListener(event -> play(new SineWave(1000D), AUDIO_FORMAT, executorService));
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("SquareWave");
            button.addActionListener(event -> play(new SquareWave(1000D), AUDIO_FORMAT, executorService));
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("SawWave");
            button.addActionListener(event -> play(new SawWave(1000D), AUDIO_FORMAT, executorService));
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("WhiteNoise");
            button.addActionListener(event -> play(new WhiteNoise(), AUDIO_FORMAT, executorService));
            return button;
        });

        for (int i = 1; i <= buttons.size(); i++) {
            final Supplier<JButton> buttonSupplier = buttons.get(i - 1);
            final GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = i;
            c.fill = GridBagConstraints.BOTH;
            c.insets = new Insets(5, 0, 0, 5);
            // c.anchor = GridBagConstraints.CENTER;
            add(buttonSupplier.get(), c);
        }
    }

    private void play(final Signal signal, final AudioFormat audioFormat, final ExecutorService executorService) {
        elapsedTimeMeter.setText("Duration:");

        // Write the data to an output file.
        // AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(fileName.getText() + ".wav"));

        // InputStream inputStream = new ByteArrayInputStream(audioData);
        // AudioInputStream audioInputStream = new AudioInputStream(inputStream, audioFormat, AudioSystem.NOT_SPECIFIED);
        // audioData.length / (long) audioFormat.getFrameSize()
        // AudioSystem.NOT_SPECIFIED

        final Runnable task = () -> {
            try {

                final AudioPlayerSink audioPlayerSink = new DefaultAudioPlayerSink(audioFormat);

                final byte[] audioData = signal.generate(audioFormat, 2D);

                final long startTime = System.currentTimeMillis();
                audioPlayerSink.play(audioData, audioData.length);
                audioPlayerSink.stop();

                final long endTime = System.currentTimeMillis() - startTime;
                SwingUtilities.invokeLater(() -> elapsedTimeMeter.setText("Duration: %d ms".formatted(endTime)));
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        };
        executorService.execute(task);

        // Thread.ofPlatform().daemon().name("player-", 1).start(new Player(sourceDataLine));
        // Thread.ofVirtual().name("player-", 1).start(task);
    }
}
