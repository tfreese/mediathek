// Created: 21 Sept. 2024
package de.freese.player.ui.equalizer;

import de.freese.player.core.dsp.DspProcessor;
import de.freese.player.core.model.Window;
import de.freese.player.equalizer.EqualizerControls;
import de.freese.player.equalizer.IIR;

/**
 * @author Thomas Freese
 */
public final class EqualizerDspProcessor implements DspProcessor {

    private final IIR iir = new IIR();
    private boolean enabled = true;

    public EqualizerControls getControls() {
        return iir.getControls();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void process(final Window window) {
        doEqualize(window);
    }

    @Override
    public void reset() {
        iir.cleanHistory();
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    private void doEqualize(final Window window) {
        iir.iir(window.getSamplesLeft(), window.getSamplesRight());
    }
}
