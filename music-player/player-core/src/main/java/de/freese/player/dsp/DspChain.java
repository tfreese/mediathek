// Created: 12 Aug. 2024
package de.freese.player.dsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.freese.player.model.Window;

/**
 * Chain for Digital Sound Processors.
 *
 * @author Thomas Freese
 */
public final class DspChain implements DspProcessor {
    private final List<DspProcessor> processors = new ArrayList<>();

    private DspProcessor player;

    public void addProcessor(final DspProcessor processor) {
        Objects.requireNonNull(processor, "processor required");

        this.processors.add(processor);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void process(final Window window) {
        for (DspProcessor processor : this.processors) {
            if (!processor.isEnabled()) {
                continue;
            }

            processor.process(window);
        }

        player.process(window);
    }

    public void setPlayer(final DspProcessor player) {
        this.player = Objects.requireNonNull(player, "player required");
    }
}
