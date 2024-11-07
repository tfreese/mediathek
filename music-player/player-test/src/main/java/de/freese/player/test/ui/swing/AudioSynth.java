// Created: 21 Juli 2024
package de.freese.player.test.ui.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import de.freese.player.core.signal.DecayPulse;
import de.freese.player.core.signal.EchoPulse;
import de.freese.player.core.signal.FmSweep;
import de.freese.player.core.signal.SawWave;
import de.freese.player.core.signal.SineWave;
import de.freese.player.core.signal.SquareWave;
import de.freese.player.core.signal.StereoPanning;
import de.freese.player.core.signal.StereoPingPong;
import de.freese.player.core.signal.Tones;
import de.freese.player.core.signal.WhiteNoise;

/**
 * @author Thomas Freese
 */
public final class AudioSynth {
    private static final AudioFormat AUDIO_FORMAT = new AudioFormat(8_000.0F,
            16,
            2,
            true,
            true);

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(AudioSynth::new);
    }

    private final JLabel elapsedTimeMeter = new JLabel("Duration:");

    public AudioSynth() {
        super();

        final JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createEtchedBorder());
        controlPanel.add(elapsedTimeMeter);

        final List<Supplier<JButton>> buttons = new ArrayList<>();
        buttons.add(() -> {
            final JButton button = new JButton("Decay Pulse");
            button.addActionListener(event -> playSignalDecayPulse());
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("Echo Pulse");
            button.addActionListener(event -> playSignalEchoPulse());
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("FM Sweep");
            button.addActionListener(event -> playSignalFmSweep());
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("Stereo Panning");
            button.addActionListener(event -> playSignalStereoPanning());
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("Stereo Pingpong");
            button.addActionListener(event -> playSignalStereoPingPong());
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("Tones");
            button.addActionListener(event -> playSignalTones());
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("SineWave");
            button.addActionListener(event -> playSignalSineWave());
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("SquareWave");
            button.addActionListener(event -> playSignalSquareWave());
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("SawWave");
            button.addActionListener(event -> playSignalSawWave());
            return button;
        });
        buttons.add(() -> {
            final JButton button = new JButton("WhiteNoise");
            button.addActionListener(event -> playSignalWhiteNoise());
            return button;
        });

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1));

        buttons.forEach(s -> buttonPanel.add(s.get()));

        // final JPanel centerPanel = new JPanel();
        // centerPanel.add(buttonPanel);

        final JFrame frame = new JFrame();
        frame.getContentPane().add(controlPanel, BorderLayout.NORTH);
        frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);
        // frame.getContentPane().add(centerPanel, BorderLayout.CENTER);

        frame.setTitle("Audio-Synth");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(250, 275);
        // frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private void play(final byte[] audioData, final AudioFormat audioFormat) {
        elapsedTimeMeter.setText("Duration:");

        // Write the data to an output file.
        // AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(fileName.getText() + ".wav"));

        final DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);

        // InputStream inputStream = new ByteArrayInputStream(audioData);
        // AudioInputStream audioInputStream = new AudioInputStream(inputStream, audioFormat, AudioSystem.NOT_SPECIFIED);
        // audioData.length / (long) audioFormat.getFrameSize()
        // AudioSystem.NOT_SPECIFIED

        // Thread.ofPlatform().daemon().name("player-", 1).start(new Player(sourceDataLine));
        Thread.ofVirtual().name("player-", 1).start(() -> {
            try (SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo)) {
                sourceDataLine.open(audioFormat);
                sourceDataLine.start();

                final long startTime = System.currentTimeMillis();

                sourceDataLine.write(audioData, 0, audioData.length);
                // final byte[] playBuffer = new byte[8196];
                // int read;
                //
                // while ((read = audioInputStream.read(playBuffer, 0, playBuffer.length)) > 0) {
                //     sourceDataLine.write(playBuffer, 0, read);
                // }

                sourceDataLine.drain();

                final long endTime = System.currentTimeMillis() - startTime;
                SwingUtilities.invokeLater(() -> elapsedTimeMeter.setText("Duration: %d ms".formatted(endTime)));

                sourceDataLine.stop();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void playSignalDecayPulse() {
        final byte[] audioData = new DecayPulse(Set.of(500D, 750D, 1000D)).generate(AUDIO_FORMAT, 2);

        play(audioData, AUDIO_FORMAT);
    }

    private void playSignalEchoPulse() {
        final byte[] audioData = new EchoPulse(Set.of(500D, 750D, 1000D)).generate(AUDIO_FORMAT, 2);

        play(audioData, AUDIO_FORMAT);
    }

    private void playSignalFmSweep() {
        final byte[] audioData = new FmSweep().generate(AUDIO_FORMAT, 2D);

        play(audioData, AUDIO_FORMAT);
    }

    private void playSignalSawWave() {
        final byte[] audioData = new SawWave(1000D).generate(AUDIO_FORMAT, 2D);

        play(audioData, AUDIO_FORMAT);
    }

    private void playSignalSineWave() {
        final byte[] audioData = new SineWave(1000D).generate(AUDIO_FORMAT, 2D);

        play(audioData, AUDIO_FORMAT);
    }

    private void playSignalSquareWave() {
        final byte[] audioData = new SquareWave(1000D).generate(AUDIO_FORMAT, 2D);

        play(audioData, AUDIO_FORMAT);
    }

    private void playSignalStereoPanning() {
        final byte[] audioData = new StereoPanning(1000D).generate(AUDIO_FORMAT, 2D);

        play(audioData, AUDIO_FORMAT);
    }

    private void playSignalStereoPingPong() {
        final byte[] audioData = new StereoPingPong(1000D).generate(AUDIO_FORMAT, 2D);

        play(audioData, AUDIO_FORMAT);
    }

    private void playSignalTones() {
        final byte[] audioData = new Tones(Set.of(500D, 1000D)).generate(AUDIO_FORMAT, 2D);

        play(audioData, AUDIO_FORMAT);
    }

    private void playSignalWhiteNoise() {
        final byte[] audioData = new WhiteNoise().generate(AUDIO_FORMAT, 2D);

        play(audioData, AUDIO_FORMAT);
    }
}
