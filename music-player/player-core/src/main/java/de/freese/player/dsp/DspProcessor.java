// Created: 12 Aug. 2024
package de.freese.player.dsp;

import de.freese.player.model.Window;

/**
 * Digital Sound Processor.
 *
 * @author Thomas Freese
 */
public interface DspProcessor {
    default String getName() {
        return getClass().getSimpleName();
    }

    boolean isEnabled();

    void process(Window window);

    void reset();
}
