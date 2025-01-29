// Created: 12 Aug. 2024
package de.freese.player.core.dsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.freese.player.core.model.Window;

/**
 * Chain for Digital Sound Processors.
 *
 * @author Thomas Freese
 */
public final class DspChain implements DspProcessor {
    private final List<DspProcessor> processors = new ArrayList<>();

    public void addProcessor(final DspProcessor processor) {
        Objects.requireNonNull(processor, "processor required");

        this.processors.add(processor);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void process(final Window window) {
        for (DspProcessor processor : processors) {
            if (!processor.isEnabled()) {
                continue;
            }

            processor.process(window);
        }
    }

    @Override
    public void reset() {
        this.processors.forEach(DspProcessor::reset);
    }
}
