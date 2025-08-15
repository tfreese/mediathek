// Created: 15 Aug. 2025
package de.freese.player.test.ui.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.InputStream;
import java.io.Serial;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class PanelAudioSystem extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(PanelAudioSystem.class);

    @Serial
    private static final long serialVersionUID = 6362957970277773890L;

    PanelAudioSystem(final ExecutorService executorService) {
        super();

        setLayout(new GridBagLayout());

        final List<Path> samples = List.of(
                Path.of("samples/sample.au"),
                Path.of("samples/sample.wav")
        );

        for (int i = 0; i < samples.size(); i++) {
            final Path sample = samples.get(i);

            final JLabel jLabel = new JLabel(sample.toString());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(5, 0, 0, 5);
            add(jLabel, gbc);

            final JButton jButtonClip = new JButton("Play as Clip");
            final JButton jButtonDataLine = new JButton("Play as DataLine");

            jButtonClip.addActionListener(_ -> {
                jButtonClip.setEnabled(false);
                jButtonDataLine.setEnabled(false);

                final SwingWorker<Void, Void> swingWorker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        playClip(sample);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        }
                        catch (Exception ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                        finally {
                            jButtonClip.setEnabled(true);
                            jButtonDataLine.setEnabled(true);
                        }
                    }
                };
                // swingWorker.execute();
                executorService.execute(swingWorker);
            });
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = i;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(5, 0, 0, 5);
            add(jButtonClip, gbc);

            jButtonDataLine.addActionListener(_ -> {
                jButtonClip.setEnabled(false);
                jButtonDataLine.setEnabled(false);

                final SwingWorker<Void, Void> swingWorker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        playSourceDataLine(sample);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        }
                        catch (Exception ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                        finally {
                            jButtonClip.setEnabled(true);
                            jButtonDataLine.setEnabled(true);
                        }
                    }
                };
                // swingWorker.execute();
                executorService.execute(swingWorker);
            });
            gbc = new GridBagConstraints();
            gbc.gridx = 2;
            gbc.gridy = i;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(5, 0, 0, 5);
            add(jButtonDataLine, gbc);
        }
    }

    /**
     * Audio is preloaded in memory.<br>
     */
    private void playClip(final Path sample) throws Exception {
        try (InputStream inputStream = sample.toUri().toURL().openStream();
             final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream)) {
            // final EqualizerAudioInputStream equalizerAudioInputStream = new EqualizerAudioInputStream(audioInputStream, 31);
            // final IIRControls iirControls = equalizerAudioInputStream.getControls();
            // audioInputStream = equalizerAudioInputStream;
            //
            // iirControls.setBandValue(10, 1, -1.5F);
            // // iirControls.setBandValue(10, 2, -1.5F);

            final AudioFormat audioFormat = audioInputStream.getFormat();
            final DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
            final Clip audioClip = (Clip) AudioSystem.getLine(info);

            audioClip.open(audioInputStream);
            audioClip.addLineListener(event -> LOGGER.info("{}", event));

            // ((FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN)).setValue(6.0206F);
            for (Control control : audioClip.getControls()) {
                LOGGER.info("Clip controls: {}", control);
            }

            // Adjust the volume on the output line.
            if (audioClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                final FloatControl volumeControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volumeControl.getMaximum());
            }

            // pause = stop + start
            // long clipTime = audioClip.getMicrosecondPostion();
            // audioClip.stop();
            // audioClip.setMicrosecondPosition(clipTime);
            // audioClip.start();

            audioClip.start();

            // Play complete track.
            audioClip.drain();

            audioClip.stop();
            audioClip.close();
        }
    }

    /**
     * SourceDataLine API is a Buffered or Streaming sound API for Java.<br>
     */
    private void playSourceDataLine(final Path sample) throws Exception {
        try (InputStream inputStream = sample.toUri().toURL().openStream();
             AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream)) {
            final AudioFormat audioFormat = audioInputStream.getFormat();
            final SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);

            sourceDataLine.open(audioFormat);
            sourceDataLine.addLineListener(event -> LOGGER.info("{}", event));

            // ((FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN)).setValue(6.0206F);
            for (Control control : sourceDataLine.getControls()) {
                LOGGER.info("SourceDataLine controls: {}", control);
            }

            // Adjust the volume on the output line.
            if (sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                final FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volumeControl.getMaximum());
            }

            // pause = stop + start
            sourceDataLine.start();

            final byte[] bufferBytes = new byte[4096];
            int readBytes = -1;

            while ((readBytes = audioInputStream.read(bufferBytes)) != -1) {
                sourceDataLine.write(bufferBytes, 0, readBytes);
            }

            // Play complete track.
            sourceDataLine.drain();

            sourceDataLine.stop();
            sourceDataLine.close();
        }
    }
}
