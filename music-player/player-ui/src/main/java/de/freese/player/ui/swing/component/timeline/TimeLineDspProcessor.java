// Created: 03 Okt. 2024
package de.freese.player.ui.swing.component.timeline;

import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.dsp.DspProcessor;
import de.freese.player.core.model.Window;

/**
 * @author Thomas Freese
 */
public final class TimeLineDspProcessor implements DspProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeLineDspProcessor.class);

    private final Consumer<Double> progressConsumer;

    public TimeLineDspProcessor(final Consumer<Double> progressConsumer) {
        super();

        this.progressConsumer = Objects.requireNonNull(progressConsumer, "progressConsumer required");
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void process(final Window window) {
        Thread.startVirtualThread(() -> updateTimeLine(window));
        // PlayerSettings.getExecutorService().execute(() -> updateTimeLine(window));
        // updateTimeLine(window);
    }

    @Override
    public void reset() {
        progressConsumer.accept(0D);
    }

    private void updateTimeLine(final Window window) {
        final double progress = (double) window.getFramesRead() / (double) window.getFramesTotal();

        LOGGER.trace("Progress: {}/{} - {}%", window.getFramesRead(), window.getFramesTotal(), progress);

        if (SwingUtilities.isEventDispatchThread()) {
            progressConsumer.accept(progress);
        }
        else {
            progressConsumer.accept(progress);
        }
    }
}
