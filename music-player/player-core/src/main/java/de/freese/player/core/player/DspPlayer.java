// Created: 29 Aug. 2024
package de.freese.player.core.player;

import de.freese.player.core.dsp.DspProcessor;

/**
 * @author Thomas Freese
 */
public interface DspPlayer extends Player {
    void addProcessor(DspProcessor processor);
}
