// Created: 03 Nov. 2024
package de.freese.player.demo;

import java.util.Arrays;

import de.freese.player.fft.math.Complex;
import de.freese.player.fft.math.FFT;

/**
 * @author Thomas Freese
 */
public final class FFTDemo {
    static void main() {
        final int[] values = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Complex[] c = Complex.toComplex(values);

        FFT.fft(c);
        Arrays.stream(c).forEach(v -> System.out.printf("%f - %s%n", v.abs(), v));

        System.out.println();

        // Equalizer: Adjust imaginary Part.
        c = Arrays.stream(c).map(v -> v.times(new Complex(1D, 0.8D))).toArray(Complex[]::new);
        Arrays.stream(c).forEach(v -> System.out.printf("%f - %s%n", v.abs(), v));

        System.out.println();

        c = FFT.ifft(c);
        Arrays.stream(c).forEach(v -> System.out.printf("%f - %s%n", v.abs(), v));
    }

    private FFTDemo() {
        super();
    }
}
