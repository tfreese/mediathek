// Created: 24 Aug. 2024
package de.freese.player.player;

import java.util.concurrent.Executor;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.PlayerSettings;
import de.freese.player.input.AudioSource;

/**
 * @author Thomas Freese
 */
public abstract class AbstractPlayer implements Player {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private AudioInputStream audioInputStream;
    private AudioSource audioSource;

    protected AbstractPlayer() {
        super();
    }

    @Override
    public void setAudioSource(final AudioSource audioSource) {
        stop();

        this.audioSource = audioSource;
    }

    protected abstract void close();

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
