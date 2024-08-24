// Created: 24 Aug. 2024
package de.freese.player.player;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.input.AudioSource;

/**
 * @author Thomas Freese
 */
public abstract class AbstractPlayer implements Player {
    private final List<AudioSource> audioSources = new ArrayList<>(1024);
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private AudioInputStream audioInputStream;
    private AudioSource currentAudioSource;
    private int playerIndex;

    protected AbstractPlayer() {
        super();
    }

    @Override
    public Player addAudioSource(final AudioSource audioSource) {
        audioSources.add(audioSource);

        return this;
    }

    @Override
    public void backward() {
        if (getCurrentAudioSource() != null) {
            stop();
        }

        if (getPlayerIndex() == 0) {
            return;
        }

        decrementPlayerIndex();

        play();
    }

    @Override
    public void forward() {
        if (getCurrentAudioSource() != null) {
            stop();
        }

        if (getPlayerIndex() == audioSources.size() - 1) {
            return;
        }

        incrementPlayerIndex();

        play();
    }

    protected abstract void close();

    protected synchronized void decrementPlayerIndex() {
        playerIndex--;
    }

    protected AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }

    protected AudioSource getAudioSource(final int index) {
        return audioSources.get(index);
    }

    protected AudioSource getCurrentAudioSource() {
        return currentAudioSource;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected int getPlayerIndex() {
        return playerIndex;
    }

    protected synchronized void incrementPlayerIndex() {
        playerIndex++;
    }

    protected void setAudioInputStream(final AudioInputStream audioInputStream) {
        this.audioInputStream = audioInputStream;
    }

    protected void setCurrentAudioSource(final AudioSource currentAudioSource) {
        this.currentAudioSource = currentAudioSource;
    }
}
