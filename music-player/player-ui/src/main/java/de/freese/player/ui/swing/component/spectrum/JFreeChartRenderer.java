// Created: 07 Sept. 2024
package de.freese.player.ui.swing.component.spectrum;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serial;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.freese.player.fft.math.FFTMath;
import de.freese.player.fft.output.Frequency;
import de.freese.player.fft.output.Spectrum;
import de.freese.player.ui.spectrum.SpectrumRenderer;

/**
 * @author Thomas Freese
 */
// @SuppressWarnings("serial")
public final class JFreeChartRenderer implements SpectrumRenderer {
    private final XYSeriesCollection data = new XYSeriesCollection();
    private final ChartPanel panel;

    private double maxAmp;

    public JFreeChartRenderer() {
        super();

        data.addSeries(new XYSeries("f", true, false));

        // final TickUnits tickUnits = new TickUnits();
        // IntStream.rangeClosed(1, 23).forEach(i -> tickUnits.add(new NumberTickUnit(i)));

        // DomainAxis
        // NumberAxis xAxis = new LogarithmicAxis("Frequency (kHz)");
        // final NumberAxis xAxis = new NumberAxis("Frequency (kHz)");
        final NumberAxis xAxis = new NumberAxis() {
            @Serial
            private static final long serialVersionUID = -8124556122933281369L;

            @Override
            @SuppressWarnings("rawtypes")
            public List refreshTicks(final Graphics2D g2, final AxisState state, final Rectangle2D dataArea, final RectangleEdge edge) {
                final List allTicks = super.refreshTicks(g2, state, dataArea, edge);

                allTicks.removeFirst(); // 0 KHz
                allTicks.removeLast();  // 22 kHz

                return allTicks;
            }
        };
        xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        xAxis.setRange(0D, 22D);
        xAxis.setNumberFormatOverride(new DecimalFormat("# kHz"));
        xAxis.setTickLabelFont(Axis.DEFAULT_TICK_LABEL_FONT.deriveFont(14F));

        // RangeAxis
        // final NumberAxis yAxis = new NumberAxis("Amplitude");
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setRange(0D, 1D);
        yAxis.setTickLabelsVisible(false);

        final XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, new Color(0, 128, 0));
        renderer.setSeriesStroke(0, new BasicStroke(1F));

        final XYPlot plot = new XYPlot(data, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);

        final JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);

        // final JFreeChart chart = ChartFactory.createXYLineChart("", "Frequency", "Amplitude", data, PlotOrientation.VERTICAL, false, true, false);
        // final XYPlot plot = (XYPlot) chart.getPlot();

        plot.setBackgroundPaint(UIManager.getColor("Panel.background"));

        panel = new ChartPanel(chart);
    }

    @Override
    public Component getComponent() {
        return panel;
    }

    @Override
    public void updateChartData(final Spectrum spectrum) {
        final Runnable runnable = () -> {
            final XYSeries series = data.getSeries("f");
            series.clear();

            if (spectrum == null) {
                maxAmp = 0D;
                return;
            }

            // Normalize
            final Frequency frequency = FFTMath.findMaxAmplitude(spectrum);

            maxAmp = frequency.getAmplitude();
            // maxAmp = Math.max(maxAmp, frequency.getAmplitude());
            FFTMath.normalize(spectrum, maxAmp);

            // spectrum.forEach(f -> series.addOrUpdate(f.getFrequency() / 1000D, frequency.getAmplitude()));
            spectrum.forEach(f -> series.add(f.getFrequency() / 1000D, f.getAmplitude(), false));

            series.fireSeriesChanged();
        };

        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        }
        else {
            SwingUtilities.invokeLater(runnable);
        }
    }
}
