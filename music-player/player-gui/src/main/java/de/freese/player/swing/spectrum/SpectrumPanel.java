// Created: 29 Aug. 2024
package de.freese.player.swing.spectrum;

import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnitSource;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.freese.player.fft.output.Spectrum;

/**
 * @author Thomas Freese
 */
public final class SpectrumPanel {
    // @Serial
    // private static final long serialVersionUID = -1L;

    private final XYSeriesCollection data = new XYSeriesCollection();
    private final ChartPanel panel;

    public SpectrumPanel() {
        super();

        data.addSeries(new XYSeries("f", true, false));

        // DomainAxis
        // NumberAxis xAxis = new LogarithmicAxis("Frequency (kHz)");
        final NumberAxis xAxis = new NumberAxis("Frequency (kHz)");
        xAxis.setAutoRangeIncludesZero(false);
        // xAxis.setRange(0D, 22_000D);
        xAxis.setRange(0D, 22D);
        xAxis.setStandardTickUnits(new NumberTickUnitSource(true));
        // xAxis.setNumberFormatOverride(new DecimalFormat("0 k"));

        // RangeAxis
        final NumberAxis yAxis = new NumberAxis("Amplitude");
        yAxis.setRange(0D, 1D);
        // yAxis.setRange(-80D, 0D);

        final XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, new Color(0, 128, 0));
        renderer.setSeriesStroke(0, new BasicStroke(1F));

        final XYPlot plot = new XYPlot(data, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);

        final JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);

        // final JFreeChart chart = ChartFactory.createXYLineChart("", "Frequency", "Amplitude", data, PlotOrientation.VERTICAL, false, true, false);
        // final XYPlot plot = (XYPlot) chart.getPlot();

        // Create panel
        panel = new ChartPanel(chart);
    }

    public ChartPanel getPanel() {
        return panel;
    }

    public void updateChartData(final Spectrum spectrum) {
        final XYSeries series = data.getSeries("f");

        if (spectrum == null) {
            series.clear();
        }
        else {
            spectrum.forEach(frequency -> series.addOrUpdate(frequency.getFrequency() / 1000D, frequency.getAmplitude()));
        }
    }
}
