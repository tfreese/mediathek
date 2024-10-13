// Created: 21 Sept. 2024
package de.freese.player.ui.equalizer;

import de.freese.player.core.dsp.DspProcessor;
import de.freese.player.core.model.Window;

/**
 * @author Thomas Freese
 */
public final class EqualizerDspProcessor implements DspProcessor {
    // private static final Logger LOGGER = LoggerFactory.getLogger(EqualizerDspProcessor.class);

    private final IIR iir = new IIR();

    public EqualizerControls getControls() {
        return iir.getControls();
    }

    @Override
    public boolean isEnabled() {
        return getControls().isEnabled();
    }

    @Override
    public void process(final Window window) {
        doEqualize(window);
    }

    @Override
    public void reset() {
        iir.cleanHistory();
    }

    private void doEqualize(final Window window) {
        iir.iir(window.getSamplesLeft(), window.getSamplesRight());
    }
}
