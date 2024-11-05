// Created: 04 Nov. 2024
package de.freese.player.ui.equalizer;

import de.freese.player.core.dsp.DspProcessor;
import de.freese.player.core.model.Window;
import de.freese.player.equalizer.DefaultEqualizer;
import de.freese.player.equalizer.Equalizer;
import de.freese.player.equalizer.EqualizerControls;

/**
 * @author Thomas Freese
 */
public final class EqualizerDspProcessor implements DspProcessor {
    private final Equalizer equalizer = new DefaultEqualizer();

    private boolean enabled = true;

    public EqualizerControls getControls() {
        return equalizer.getControls();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void process(final Window window) {
        doEqualize(window.getSamplesLeft(), window.getSamplesRight());
    }

    @Override
    public void reset() {
        equalizer.cleanHistory();
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    private void doEqualize(final int[] samplesLeft, final int[] samplesRight) {
        equalizer.equalize(samplesLeft, samplesRight);
    }
}
