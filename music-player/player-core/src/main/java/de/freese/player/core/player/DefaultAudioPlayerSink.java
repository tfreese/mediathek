// Created: 29 Jan. 2025
package de.freese.player.core.player;

import java.nio.ByteOrder;
import java.util.Objects;
import java.util.function.Consumer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import de.freese.player.core.input.AudioSource;

/**
 * @author Thomas Freese
 */
public class DefaultAudioPlayerSink implements AudioPlayerSink {
    public static AudioFormat getTargetAudioFormat(final AudioSource audioSource) {
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                audioSource.getSampleRate(),
                16,
                audioSource.getChannels(),
                audioSource.getChannels() * 2,
                audioSource.getSampleRate(),
                ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder()) // false
        );
    }

    private final AudioFormat audioFormat;
    private final SourceDataLine sourceDataLine;

    public DefaultAudioPlayerSink(final AudioFormat audioFormat) throws LineUnavailableException {
        super();

        this.audioFormat = Objects.requireNonNull(audioFormat, "audioFormat required");

        // final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        // sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);

        sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();
    }

    @Override
    public void close() {
        // Continues data line I/O until its buffer is drained.
        sourceDataLine.drain();

        sourceDataLine.stop();
        sourceDataLine.close();
    }

    // public void stop() {
    //     // Continues data line I/O until its buffer is drained.
    //     sourceDataLine.drain();
    //
    //     sourceDataLine.stop();
    // }

    @Override
    public void configureVolumeControl(final Consumer<FloatControl> consumer) {
        if (sourceDataLine.isOpen() && sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            consumer.accept((FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN));
        }
    }

    @Override
    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    @Override
    public void play(final byte[] audioData, final int length) {
        sourceDataLine.write(audioData, 0, length);
    }
}
