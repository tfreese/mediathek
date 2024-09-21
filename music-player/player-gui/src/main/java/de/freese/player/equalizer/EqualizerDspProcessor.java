// Created: 21 Sept. 2024
package de.freese.player.equalizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.dsp.DspProcessor;
import de.freese.player.model.Window;

/**
 * @author Thomas Freese
 */
public final class EqualizerDspProcessor implements DspProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(EqualizerDspProcessor.class);

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void process(final Window window) {
        // TODO
    }

    @Override
    public void reset() {
        // Empty
    }
}
