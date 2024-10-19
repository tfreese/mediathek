// Created: 19 Okt. 2024
package de.freese.player.ui.swing.component.equalizer;

import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.util.PlayerUtils;
import de.freese.player.ui.equalizer.EqualizerControls;
import de.freese.player.ui.swing.component.GbcBuilder;

/**
 * @author Thomas Freese
 */
public class EqualizerView {
    private static final Logger LOGGER = LoggerFactory.getLogger(EqualizerView.class);

    private static JPanel createSliderPanel(final String title, final Consumer<JSlider> sliderConfigurer, final IntConsumer valueConsumer) {
        final JPanel jPanel = new JPanel(new GridBagLayout());

        final JLabel jLabel = new JLabel(title);
        jPanel.add(jLabel, GbcBuilder.of(0, 0).anchorWest());

        final JSlider jSlider = new JSlider(SwingConstants.VERTICAL);

        sliderConfigurer.accept(jSlider);

        jSlider.setLabelTable(new Hashtable<>(Map.of(-12, new JLabel("-12"), 0, new JLabel("0"), +12, new JLabel("+12"))));
        // jSlider.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        jSlider.setMinorTickSpacing(2);
        jSlider.setPaintLabels(true);
        jSlider.setPaintTicks(true);
        jPanel.add(jSlider, GbcBuilder.of(0, 1).fillVertical());

        final JLabel jLabelDb = new JLabel("%d dB".formatted(jSlider.getValue()));
        // jLabelDb.setBorder(BorderFactory.createLineBorder(Color.RED));
        jPanel.add(jLabelDb, GbcBuilder.of(0, 2).anchorWest());

        jSlider.addChangeListener(event -> {
            final JSlider source = (JSlider) event.getSource();

            if (source.getValueIsAdjusting()) {
                return;
            }

            final int value = source.getValue();

            jLabelDb.setText("%d dB".formatted(value));

            valueConsumer.accept(value);
        });

        return jPanel;
    }

    private final JPanel panel = new JPanel(new GridBagLayout());

    public EqualizerView(final EqualizerControls equalizerControls) {
        super();

        Objects.requireNonNull(equalizerControls, "equalizerControls required");

        final int row = 0;
        int column = 0;

        // final JButton jButtonReset = new JButton("Reset");
        // jButtonReset.addActionListener(event -> {
        //     equalizerControls.setPreampDbValue(1D);
        //
        //     for (int band = 0; band < equalizerControls.getBands().length; band++) {
        //         equalizerControls.setBandDbValue(band, 0D);
        //     }
        // });
        // panel.add(jButtonReset, GbcBuilder.of(column, row).gridwidth(20));
        //
        // row++;

        panel.add(createSliderPanel("PreAmp.", slider -> {
            slider.setMinimum((int) equalizerControls.getMinimumPreampDbValue());
            slider.setMaximum((int) equalizerControls.getMaximumPreampDbValue());
            slider.setValue((int) PlayerUtils.linearToDB(equalizerControls.getPreampValue()));
        }, value -> {
            equalizerControls.setPreampDbValue(value);
            LOGGER.debug("PreAmp value: {} - dbValue: {}", value, equalizerControls.getPreampValue());
        }), GbcBuilder.of(column, row));

        column++;

        final JSeparator jSeparator = new JSeparator(SwingConstants.VERTICAL);
        // jSeparator.setBorder(BorderFactory.createLineBorder(Color.RED));
        panel.add(jSeparator, GbcBuilder.of(column, row).fillVertical());

        final double[] bands = equalizerControls.getBands();

        for (int band = 0; band < bands.length; band++) {
            column++;

            final int bandIndex = band;

            panel.add(createSliderPanel("Band " + bandIndex, slider -> {
                slider.setMinimum((int) equalizerControls.getMinimumBandDbValue());
                slider.setMaximum((int) equalizerControls.getMaximumBandDbValue());
                slider.setValue((int) PlayerUtils.linearToDB(bands[bandIndex]));
            }, value -> {
                equalizerControls.setBandDbValue(bandIndex, value);
                LOGGER.debug("Band {} value: {} - dbValue: {}", bandIndex, value, equalizerControls.getBand(bandIndex));
            }), GbcBuilder.of(column, row));
        }
    }

    public JComponent getComponent() {
        return panel;
    }
}
