package org.quifft.output;

/**
 * The result of an FFT computed over entirety of audio file.
 */
public class SpectraResult extends AbstractFFTObject {
    /**
     * Array containing all Spectra computed for entirety of audio file.
     */
    private Spectrum[] spectra;

    public SpectraResult() {
        super();

    }

    public Spectrum[] getSpectra() {
        return spectra;
    }

    public void setSpectra(final Spectrum[] spectra) {
        this.spectra = spectra;
    }
}
