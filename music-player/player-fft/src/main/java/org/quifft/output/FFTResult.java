package org.quifft.output;

/**
 * The result of an FFT computed over entirety of audio file.
 */
public class FFTResult extends AbstractFFTObject {
    /**
     * Array containing all FFTFrames computed for entirety of audio file.
     */
    private FFTFrame[] fftFrames;

    public FFTResult() {
        super();

    }

    public FFTFrame[] getFFTFrames() {
        return fftFrames;
    }

    public void setFftFrames(final FFTFrame[] fftFrames) {
        this.fftFrames = fftFrames;
    }
}
