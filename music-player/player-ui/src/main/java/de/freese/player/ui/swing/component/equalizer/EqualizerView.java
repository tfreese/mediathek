// Created: 19 Okt. 2024
package de.freese.player.ui.swing.component.equalizer;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.ui.equalizer.EqualizerControls;
import de.freese.player.ui.equalizer.EqualizerDspProcessor;
import de.freese.player.ui.swing.component.GbcBuilder;

/**
 * @author Thomas Freese
 */
public class EqualizerView {
    private static final Logger LOGGER = LoggerFactory.getLogger(EqualizerView.class);

    private static JPanel createSliderPanel(final String title, final String valueFormat, final Consumer<JSlider> configurer, final IntConsumer valueConsumer) {
        final JPanel jPanel = new JPanel(new GridBagLayout());

        final JLabel jLabel = new JLabel(title);
        jPanel.add(jLabel, GbcBuilder.of(0, 0).anchorWest());

        final JSlider jSlider = new JSlider(SwingConstants.VERTICAL);

        // jSlider.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        configurer.accept(jSlider);

        final JLabel jLabelValue = new JLabel(valueFormat.formatted(jSlider.getValue()));
        // jLabelValue.setBorder(BorderFactory.createLineBorder(Color.RED));

        jSlider.setPaintLabels(true);
        jSlider.setPaintTicks(true);
        // jSlider.setMinimumSize(new Dimension(80, 400));
        jSlider.setPreferredSize(new Dimension(80, 400));
        jSlider.setFont(jLabelValue.getFont().deriveFont(10F));

        jPanel.add(jSlider, GbcBuilder.of(0, 1).fillVertical());
        // jPanel.add(jLabelValue, GbcBuilder.of(0, 2).insets(0, 0, 5, 5));
        jPanel.add(jLabelValue, GbcBuilder.of(0, 2).anchorWest());

        jSlider.addChangeListener(event -> {
            final JSlider source = (JSlider) event.getSource();

            if (source.getValueIsAdjusting()) {
                return;
            }

            final int value = source.getValue();

            jLabelValue.setText(valueFormat.formatted(value));

            valueConsumer.accept(value);
        });

        return jPanel;
    }

    private final JPanel panel = new JPanel(new GridBagLayout());
    private final List<JSlider> sliders = new ArrayList<>();

    public EqualizerView(final EqualizerDspProcessor equalizerDspProcessor) {
        super();

        Objects.requireNonNull(equalizerDspProcessor, "equalizerDspProcessor required");
        final EqualizerControls equalizerControls = equalizerDspProcessor.getControls();

        int row = 0;
        int column = 0;

        final JButton jButtonReset = new JButton("Reset");
        jButtonReset.addActionListener(event -> {
            // PreAmp
            sliders.getFirst().setValue(100);

            // Bands
            sliders.stream().skip(1).forEach(slider -> slider.setValue(0));
        });
        panel.add(jButtonReset, GbcBuilder.of(column, row).anchorWest().gridwidth(20));

        row++;

        final JCheckBox jCheckBoxEnabled = new JCheckBox("Equalizer enabled");
        jCheckBoxEnabled.setSelected(equalizerDspProcessor.isEnabled());
        jCheckBoxEnabled.addActionListener(event -> {
            equalizerDspProcessor.setEnabled(jCheckBoxEnabled.isSelected());
            sliders.forEach(slider -> slider.setEnabled(jCheckBoxEnabled.isSelected()));
        });
        panel.add(jCheckBoxEnabled, GbcBuilder.of(column, row).anchorWest().gridwidth(20));

        row++;

        panel.add(createSliderPanel("PreAmp.", "%d %%", slider -> {
            sliders.add(slider);
            slider.setMinimum((int) (equalizerControls.getMinimumPreampValue() * 100D));
            slider.setMaximum((int) (equalizerControls.getMaximumPreampValue() * 100D));
            slider.setValue((int) (equalizerControls.getPreampValue() * 100D));
            slider.setMajorTickSpacing(25);
            slider.setMinorTickSpacing(5);
        }, value -> {
            equalizerControls.setPreampValue(value / 100D);
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

            panel.add(createSliderPanel("Band " + bandIndex, "%d %%", slider -> {
                sliders.add(slider);
                slider.setMinimum((int) (equalizerControls.getMinimumBandValue() * 100D));
                slider.setMaximum((int) (equalizerControls.getMaximumBandValue() * 100D));
                slider.setValue((int) (bands[bandIndex] * 100D));
                // slider.setLabelTable(new Hashtable<>(Map.of(-12, new JLabel("-12"), 0, new JLabel("0"), +12, new JLabel("+12"))));
                slider.setMajorTickSpacing(25);
                slider.setMinorTickSpacing(5);
            }, value -> {
                equalizerControls.setBandValue(bandIndex, value / 100D);
                LOGGER.debug("Band {} value: {} - dbValue: {}", bandIndex, value, equalizerControls.getBand(bandIndex));
            }), GbcBuilder.of(column, row));
        }
    }

    public JComponent getComponent() {
        return panel;
    }
}
