package de.freese.player.core.signal;

import javax.sound.sampled.AudioFormat;

/**
 * @author Thomas Freese
 * @since 03.11.2024
 */
@FunctionalInterface
public interface Signal {
    byte[] generate(AudioFormat audioFormat, double seconds);
}
