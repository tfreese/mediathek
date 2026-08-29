package de.freese.player.core.dsp;

import de.freese.player.core.model.Window;

/**
 * Digital Sound Processor.
 *
 * @author Thomas Freese
 * @since 12.08.2024
 */
public interface DspProcessor {
    default String getName() {
        return getClass().getSimpleName();
    }

    boolean isEnabled();

    void process(Window window);

    void reset();
}
