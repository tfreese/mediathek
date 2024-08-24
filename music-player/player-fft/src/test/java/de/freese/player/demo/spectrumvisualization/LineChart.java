// Created: 08 Aug. 2024
package de.freese.player.demo.spectrumvisualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.Serial;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.quifft.output.Spectrum;

/**
 * @author Thomas Freese
 */
final class LineChart extends JFrame {
    @Serial
    private static final long serialVersionUID = -2284879212465893870L;

    private static String getMMSSTimestamp(final double seconds) {
        final int min = (int) Math.floor(seconds / 60);
        final double s = seconds % 60;

        final DecimalFormat df = new DecimalFormat("#.00");
        final StringBuilder secsBuilder = new StringBuilder();

        if (s < 1) {
            secsBuilder.append("0");
        }

        if (s < 10) {
            secsBuilder.append("0");
        }

        secsBuilder.append(df.format(s));

        return String.format("%d:%s", min, secsBuilder);
    }

    private final XYSeriesCollection data = new XYSeriesCollection();

    private JFreeChart chart;

    void init() {
        data.addSeries(new XYSeries("f", true, false));

        // DomainAxis
        // NumberAxis xAxis = new LogarithmicAxis("Frequency (Hz)");
        final NumberAxis xAxis = new NumberAxis("Frequency (Hz)");
        xAxis.setAutoRangeIncludesZero(false);
        xAxis.setRange(0D, 23_000D);

        // RangeAxis
        final NumberAxis yAxis = new NumberAxis("Amplitude");
        yAxis.setRange(0D, 1D);
        // yAxis.setRange(-80D, 0D);

        final XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, new Color(0, 128, 0));
        renderer.setSeriesStroke(0, new BasicStroke(1F));

        final XYPlot plot = new XYPlot(data, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);

        chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);

        // final JFreeChart chart = ChartFactory.createXYLineChart("", "Frequency", "Amplitude", data, PlotOrientation.VERTICAL, false, true, false);
        // final XYPlot plot = (XYPlot) chart.getPlot();

        // Create panel
        final ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);

        setTitle("Frequency Spectrum");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    void updateChartData(final Spectrum spectrum) {
        final long timestamp = (long) spectrum.getFrameStartMs() / 1000;

        chart.setTitle(getMMSSTimestamp(timestamp));

        final XYSeries series = data.getSeries("f");

        spectrum.forEach(frequency -> series.addOrUpdate(frequency.getFrequency(), frequency.getAmplitude()));
    }
}
