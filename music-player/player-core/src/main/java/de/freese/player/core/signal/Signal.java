// Created: 03 Nov. 2024
package de.freese.player.core.signal;

import javax.sound.sampled.AudioFormat;

/**
 * @author Thomas Freese
 */
public interface Signal {
    byte[] generate(AudioFormat audioFormat, int seconds, double frequency);
}
