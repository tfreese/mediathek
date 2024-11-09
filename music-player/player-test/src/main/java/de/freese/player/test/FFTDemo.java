// Created: 05 Aug. 2024
package de.freese.player.test;

import java.util.Set;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

import de.freese.player.core.model.Window;
import de.freese.player.core.player.SourceDataLinePlayer;
import de.freese.player.core.signal.Tones;
import de.freese.player.core.util.PlayerUtils;
import de.freese.player.fft.config.FFTConfig;
import de.freese.player.fft.math.FFTComputationWrapper;
import de.freese.player.fft.output.Spectrum;
import de.freese.player.fft.sampling.WindowFunction;

/**
 * @author Thomas Freese
 */
public final class FFTDemo {
    private static final boolean BIG_ENDIAN = false; // java.nio.ByteOrder.BIG_ENDIAN.equals(java.nio.ByteOrder.nativeOrder())
    private static final int CHANNELS = 2;
    private static final double SAMPLE_RATE = 16_000.0D;
    private static final int SAMPLE_SIZE_IN_BITS = 16;
    private static final boolean SIGNED = true;

    public static void main(final String[] args) throws LineUnavailableException {
        final AudioFormat audioFormat = new AudioFormat((float) SAMPLE_RATE,
                SAMPLE_SIZE_IN_BITS,
                CHANNELS,
                SIGNED, // audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)
                BIG_ENDIAN);

        final SourceDataLinePlayer sourceDataLinePlayer = new SourceDataLinePlayer(audioFormat);

        final byte[] audioBytes = new Tones(Set.of(1000D, 2000D)).generate(audioFormat, 1);
        // sourceDataLinePlayer.play(audioBytes, audioBytes.length);
        sourceDataLinePlayer.play(PlayerUtils.extractWindow(audioFormat, audioBytes, 16_000));

        sourceDataLinePlayer.close();

        final Window window = PlayerUtils.extractWindow(audioFormat, audioBytes, 4096);
        final int[] samples = window.getMergedSamples();

        // try {
        //     final Complex[] complexes = Arrays.stream(samples).mapToObj(Complex::new).toArray(Complex[]::new);
        //     FFT.fft(complexes);
        //     for (int c = 0; c < complexes.length; c++) {
        //         System.out.println(complexes[c]);
        //     }
        // }
        // catch (Exception ex) {
        //     System.out.println(ex.getMessage());
        // }

        // try {
        //     final Complex[] complexes = Arrays.stream(samples).mapToObj(Complex::new).toArray(Complex[]::new);
        //     BasicFFT.fft(complexes);
        //     for (int c = 0; c < complexes.length / 2; c++) {
        //         System.out.println(complexes[c]);
        //         // System.out.printf("freq: %f - amp: %f%n", c * audioFormat.getSampleRate() / complexes.length, c * complexes[c].abs());
        //     }
        // }
        // catch (Exception ex) {
        //     System.out.println(ex.getMessage());
        // }

        // final double[] outputReal = new double[samples.length];
        // final double[] outputImaginary = new double[samples.length];
        // BasicFFT.transform(Arrays.stream(samples).mapToDouble(s -> (double) s).toArray(), outputReal, outputImaginary);
        //
        // for (int c = 0; c < samples.length; c++) {
        //     System.out.printf("%f + %fi%n", outputReal[c], outputImaginary[c]);
        // }

        // final BestFastFourier fastFourier = new BestFastFourier();
        // fastFourier.transform(Arrays.stream(samples).mapToDouble(s -> (double) s).toArray(),
        //         new double[samples.length],
        //         outputReal,
        //         outputImaginary);
        // for (int c = 0; c < samples.length; c++) {
        //     System.out.printf("%f + %fi%n", outputReal[c], outputImaginary[c]);
        // }

        final FFTConfig fftConfig = new FFTConfig()
                .windowSize(samples.length)
                .windowFunction(WindowFunction.RECTANGULAR);

        final Spectrum spectrum = FFTComputationWrapper.createSpectrum(samples, audioFormat.getSampleRate(), fftConfig);
        // final double maxAmplitude = FFTMath.findMaxAmplitude(fftFrame);
        // FFTMath.normalize(spectrum, maxAmplitude);

        spectrum.forEach(frequency ->
                System.out.printf("%f + %fi%n", frequency.getFrequency(), frequency.getAmplitude())
        );
    }

    private FFTDemo() {
        super();
    }
}
