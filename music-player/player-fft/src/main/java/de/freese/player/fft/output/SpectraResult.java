package de.freese.player.fft.output;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import de.freese.player.fft.config.FFTConfig;
import de.freese.player.fft.reader.AudioReader;

/**
 * The result of an FFT computed over entirety of audio file.
 */
public final class SpectraResult extends AbstractFFTObject {
    /**
     * Array containing all Spectra computed for entirety of audio file.
     */
    private final Spectrum[] spectra;

    public SpectraResult(final AudioReader audioReader, final FFTConfig config, final Spectrum[] spectra) {
        super(audioReader, config);

        Objects.requireNonNull(spectra, "spectra required");

        if (spectra.length == 0) {
            throw new IllegalArgumentException("spectra are empty");
        }

        this.spectra = spectra;
    }

    public Spectrum getSpectrum(final int index) {
        return spectra[index];
    }

    public Iterator<Spectrum> iterator() {
        return new Iterator<>() {
            private int index;

            @Override
            public boolean hasNext() {
                return index < length();
            }

            @Override
            public Spectrum next() {
                if (index >= length()) {
                    throw new NoSuchElementException();
                }

                final Spectrum spectrum = getSpectrum(index);

                index++;

                return spectrum;
            }
        };
    }

    public int length() {
        return spectra.length;
    }
}
