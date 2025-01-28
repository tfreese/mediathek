// Created: 01 Juli 2024
package de.freese.player.test;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class DemoPlayer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoPlayer.class);

    private static final class MyLineListener implements LineListener {
        @Override
        public void update(final LineEvent event) {
            LOGGER.info("{}", event);
        }
    }

    public static void main(final String[] args) throws Exception {
        // final URI uri = Path.of("/tmp/test.wav").toUri();

        // URI uri = Path.of("samples/sample.aif").toUri();
        // final URI uri = Path.of("samples/sample.au").toUri();
        // final URI uri = Path.of("samples/sample.flac").toUri();
        // final URI uri = Path.of("samples/sample.m4a").toUri();
        // final URI uri = Path.of("samples/sample.m4b").toUri(); // fail
        // final URI uri = Path.of("samples/sample.mp3").toUri();
        // final URI uri = Path.of("samples/sample.ogg").toUri(); // fail
        final URI uri = Path.of("samples/sample.wav").toUri();
        // final URI uri = Path.of("samples/sample.wma").toUri();

        // try (Player player = PlayerFactory.createPlayer(uri)) {
        //     // player.play();
        //
        //     TimeUnit.SECONDS.sleep(4);
        //
        //     // player.stop();
        // }

        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            executorService.submit(() -> {
                playWavClip(uri);
                // playWavSourceDataLine(uri);
                return null;
            });

            TimeUnit.SECONDS.sleep(4);
            System.exit(0);
        }
    }

    /**
     * Audio is preloaded in memory.<br>
     */
    static void playWavClip(final URI uri) throws Exception {
        final InputStream inputStream = uri.toURL().openStream();
        final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);

        // final EquilizerAudioInputStream equilizerAudioInputStream = new EquilizerAudioInputStream(audioInputStream, 31);
        // final IIRControls iirControls = equilizerAudioInputStream.getControls();
        // audioInputStream = equilizerAudioInputStream;
        //
        // iirControls.setBandValue(10, 1, -1.5F);
        // // iirControls.setBandValue(10, 2, -1.5F);

        final AudioFormat audioFormat = audioInputStream.getFormat();
        final DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
        final Clip audioClip = (Clip) AudioSystem.getLine(info);

        audioClip.open(audioInputStream);
        audioClip.addLineListener(new MyLineListener());

        // ((FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN)).setValue(6.0206F);
        for (Control control : audioClip.getControls()) {
            LOGGER.info("{}", control);
        }

        // Adjust the volume on the output line.
        // if (audioClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
        //     FloatControl volumeControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
        //     volumeControl.setValue((volumeControl.getMaximum() - volumeControl.getMinimum()) * 0.7F + volumeControl.getMinimum());
        // }

        // pause = stop + start
        // long clipTime = audioClip.getMicrosecondPostion();
        // audioClip.stop();
        // audioClip.setMicrosecondPosition(clipTime);
        // audioClip.start();

        audioClip.start();
        // audioClip.drain();
        // audioClip.stop();
        // audioClip.close();
        // audioStream.close();
    }

    /**
     * SourceDataLine API is a Buffered or Streaming sound API for Java.<br>
     */
    static void playWavSourceDataLine(final URI uri) throws Exception {
        final InputStream inputStream = uri.toURL().openStream();
        final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);

        final AudioFormat audioFormat = audioInputStream.getFormat();
        final SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);

        sourceDataLine.open(audioFormat);
        sourceDataLine.addLineListener(new MyLineListener());

        // ((FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN)).setValue(6.0206F);
        for (Control control : sourceDataLine.getControls()) {
            LOGGER.info("{}", control);
        }

        // pause = stop + start
        sourceDataLine.start();

        final byte[] bufferBytes = new byte[4096];
        int readBytes = -1;

        while ((readBytes = audioInputStream.read(bufferBytes)) != -1) {
            sourceDataLine.write(bufferBytes, 0, readBytes);
        }

        sourceDataLine.drain();
        sourceDataLine.stop();
        sourceDataLine.close();
        audioInputStream.close();
    }

    private DemoPlayer() {
        super();
    }
}
