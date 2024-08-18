package org.quifft.output;

import java.text.DecimalFormat;

import javax.sound.sampled.AudioFormat;

import org.quifft.config.FFTConfig;
import org.quifft.reader.AudioReader;

/**
 * Object representing the result of a Fourier transform; superclass of two result types {@link FFTResult} and {@link FFTStream}.
 */
public abstract class AbstractFFTObject {
    /**
     * The sample rate of the audio.
     */
    private float audioSampleRate;
    /**
     * The parameters used to compute this FFT
     */
    private FFTConfig fftConfig;
    /**
     * Duration of input audio file in milliseconds
     */
    private long fileDurationMs;
    /**
     * Frequency resolution of FFT result.<br>
     * This is calculated by dividing the audio file's sampling rate by the number of points in the FFT.<br>
     * A lower frequency resolution makes it easier to distinguish between frequencies that are close together.<br>
     * By improving frequency resolution, however, some time resolution is lost because there are fewer
     * FFT calculations per unit of time (sampling windows are larger).<br>
     */
    private double frequencyResolution;
    /**
     * Length of each sampling window in milliseconds.<br>
     * This is proportional to the length of each window in terms of number of samples.
     */
    private double windowDurationMs;

    public FFTConfig getFFTConfig() {
        return fftConfig;
    }

    public long getFileDurationMs() {
        return fileDurationMs;
    }

    public double getFrequencyResolution() {
        return frequencyResolution;
    }

    public double getWindowDurationMs() {
        return windowDurationMs;
    }

    /**
     * Sets metadata to be returned by an output object ({@link FFTResult} or {@link FFTStream}).
     */
    public void setMetadata(final AudioReader audioReader, final FFTConfig config) {
        this.fileDurationMs = audioReader.getDurationMs();

        final AudioFormat format = audioReader.getAudioFormat();
        this.audioSampleRate = format.getSampleRate();
        this.frequencyResolution = (double) format.getSampleRate() / config.totalWindowLength();

        final double sampleLengthMs = 1D / format.getSampleRate() * 1000D;
        this.windowDurationMs = sampleLengthMs * config.getWindowSize();

        this.fftConfig = config;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append(" ==========================").append(System.lineSeparator());
        builder.append(String.format("Audio sample rate: %d%n", (long) audioSampleRate));
        builder.append(String.format("Frequency resolution: %.3f Hz%n", frequencyResolution));
        builder.append(String.format("Windowing function: %s%n", fftConfig.getWindowFunction().toString()));
        builder.append(String.format("Window duration: %.1f ms%n", windowDurationMs));
        builder.append("Window overlap: ");

        if (Double.compare(fftConfig.getWindowOverlap(), 0D) == 0) {
            builder.append("none").append(System.lineSeparator());
        }
        else {
            final DecimalFormat decimalFormat = new DecimalFormat("#.#");
            builder.append(String.format("%s%n", decimalFormat.format(fftConfig.getWindowOverlap() * 100)));
        }

        builder.append(String.format("Number of points in FFT: %d", fftConfig.getWindowSize()));

        if (fftConfig.getNumPoints() != null) {
            builder.append(String.format(" window size + %d zero-padding = %d", fftConfig.zeroPadLength(), fftConfig.getNumPoints()));
        }

        builder.append(" points");
        builder.append(System.lineSeparator());

        return builder.toString();
    }
}
