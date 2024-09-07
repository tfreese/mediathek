// Created: 07 Sept. 2024
package de.freese.player.swing.component.spectrum;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import de.freese.player.fft.output.Frequency;

/**
 * Groups the Spectrum in Bands with avg. Amplitude.
 *
 * @author Thomas Freese
 */
public final class BandSpectrumCollector implements Collector<Frequency, Map<Integer, double[]>, Map<Integer, Double>> {
    private final double bandCount;

    /**
     * Example:<br>
     * bandCount = 220 -> Spectrum is 0-22kHz -> 100Hz Range per Band
     */
    public BandSpectrumCollector(final int bandCount) {
        super();

        if (bandCount <= 0) {
            throw new IllegalArgumentException("bandCount must > 0: " + bandCount);
        }

        this.bandCount = bandCount;
    }

    @Override
    public BiConsumer<Map<Integer, double[]>, Frequency> accumulator() {
        return (map, frequency) -> {
            final int band = (int) Math.round(frequency.getFrequency() / bandCount);
            final double[] values = map.computeIfAbsent(band, key -> new double[2]);
            values[0] += frequency.getAmplitude();
            values[1]++;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of();
    }

    @Override
    public BinaryOperator<Map<Integer, double[]>> combiner() {
        return (a, b) -> {
            a.forEach((key, value) -> {
                final double[] bValue = b.remove(key);

                if (bValue != null) {
                    a.merge(key, bValue, (oldValue, newValue) -> {
                        oldValue[0] += newValue[0];
                        oldValue[1] += newValue[1];

                        return oldValue;
                    });
                }
            });

            a.putAll(b);

            return a;
        };
    }

    @Override
    public Function<Map<Integer, double[]>, Map<Integer, Double>> finisher() {
        return map -> {
            final Map<Integer, Double> finish = new TreeMap<>();

            map.forEach((key, value) -> {
                final double avg = value[0] / value[1];
                finish.put(key, avg);
            });

            return finish;
        };
    }

    @Override
    public Supplier<Map<Integer, double[]>> supplier() {
        return HashMap::new;
    }
}
