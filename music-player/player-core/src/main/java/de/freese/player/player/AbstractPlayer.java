// Created: 24 Aug. 2024
package de.freese.player.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.PlayerSettings;
import de.freese.player.exception.PlayerException;
import de.freese.player.input.AudioSource;

/**
 * @author Thomas Freese
 */
public abstract class AbstractPlayer implements Player {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<Consumer<AudioSource>> songFinishedListener = new ArrayList<>();

    private AudioInputStream audioInputStream;
    private AudioSource audioSource;

    protected AbstractPlayer() {
        super();
    }

    @Override
    public void addSongFinishedListener(final Consumer<AudioSource> listener) {
        songFinishedListener.add(Objects.requireNonNull(listener, "listener required"));
    }

    @Override
    public boolean isPlaying() {
        return audioInputStream != null;
    }

    @Override
    public void setAudioSource(final AudioSource audioSource) {
        if (isPlaying()) {
            throw new PlayerException("Stop player before set new AudioSource");
        }

        this.audioSource = audioSource;
    }

    protected abstract void close();

    protected void fireSongFinished() {
        songFinishedListener.forEach(consumer -> consumer.accept(getAudioSource()));
    }

    protected AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }

    protected AudioSource getAudioSource() {
        return audioSource;
    }

    protected Executor getExecutor() {
        return PlayerSettings.getExecutorService();
    }

    protected Logger getLogger() {
        return logger;
    }

    protected void setAudioInputStream(final AudioInputStream audioInputStream) {
        this.audioInputStream = audioInputStream;
    }
}
