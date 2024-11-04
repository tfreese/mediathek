// Created: 04 Nov. 2024
package de.freese.player.ui.equalizer;

import de.freese.player.core.dsp.DspProcessor;
import de.freese.player.equalizer.EqualizerControls;

/**
 * @author Thomas Freese
 */
public interface EqualizerDspProcessor extends DspProcessor {
    EqualizerControls getControls();

    void setEnabled(boolean enabled);
}
