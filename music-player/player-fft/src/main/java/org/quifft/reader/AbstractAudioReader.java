package org.quifft.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.NoSuchElementException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.quifft.config.FFTConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads audio files into {@code int[]} waveforms<br>
 * In a waveform array, each value represents a sample of the sound wave at discrete time steps.
 */
public abstract class AbstractAudioReader implements AudioReader {
    /**
     * Decodes audio reader's input stream to a target format with bit depth of 16.<br>
     * This is used when the input file is an 8-bit WAV or an MP3.
     */
    static AudioInputStream wrapTo16Bit(final InputStream inputStream) throws IOException, UnsupportedAudioFileException {
        final AudioInputStream in = AudioSystem.getAudioInputStream(inputStream);
        final AudioFormat baseFormat = in.getFormat();
        final AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false);

        return AudioSystem.getAudioInputStream(decodedFormat, in);
    }

    private final AudioInputStream audioInputStream;
    private final long fileLength;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * Indicates whether all bytes in the input stream have been read yet (for FFTStream's hasNext() method).
     */
    private boolean areMoreBytesToRead = true;
    /**
     * FFT Parameters only used by FFTStream.
     */
    private FFTConfig fftConfig;
    /**
     * Keep count of how many frames have been read (how many times FFTStream's next() has been called).
     */
    private int framesReadCount;
    /**
     * The number of FFT frames that should be extractable; not known until entire input stream has been read.
     */
    private int numExpectedFrames;
    /**
     * Buffer used to store bytes as they are requested by FFTStream and replace them when they're no longer needed.
     */
    private int[] sampleBuffer;
    /**
     * Keep count of how many samples there are in the full-length waveform as bytes are incrementally read.
     */
    private int waveLength;

    protected AbstractAudioReader(final AudioInputStream audioInputStream, final long fileLength) {
        super();

        this.audioInputStream = audioInputStream;
        this.fileLength = fileLength;
    }

    @Override
    public AudioFormat getAudioFormat() {
        return getAudioInputStream().getFormat();
    }

    @Override
    public AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }

    @Override
    public long getLength() {
        return fileLength;
    }

    @Override
    public int[] getWaveform() {
        try {
            final byte[] bytes = getAudioInputStream().readAllBytes();

            return convertBytesToSamples(bytes);
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public boolean hasNext() {
        return areMoreBytesToRead || framesReadCount < numExpectedFrames;
    }

    @Override
    public int[] next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        final int windowSize = fftConfig.getWindowSize() * (isStereo() ? 2 : 1);
        final double windowOverlap = fftConfig.getWindowOverlap();
        final byte[] newBytes;

        try {
            final int numBytesRead; // number of bytes actually read from input stream

            // if first window taken, simply copy window size worth of samples into buffer array
            if (sampleBuffer == null) {
                newBytes = new byte[windowSize * 2]; // 16-bit audio = 2 bytes per sample
                numBytesRead = readBytesToFillArray(newBytes);

                sampleBuffer = convertBytesToSamples(newBytes);
            }
            else {
                // if previous samples exist in buffer, copy them into next buffer and append newly read bytes
                final int samplesToKeep = (int) Math.round(windowSize * windowOverlap);
                final int prevSamplesCopyStartIndex = windowSize - samplesToKeep;
                final int numMoreBytesToRead = (windowSize - samplesToKeep) * 2;

                // copy overlapped samples into new buffer
                final int[] newSampleBuffer = new int[windowSize];
                System.arraycopy(sampleBuffer, prevSamplesCopyStartIndex, newSampleBuffer, 0, samplesToKeep);

                // read new bytes (if there are any)
                if (areMoreBytesToRead) {
                    newBytes = new byte[numMoreBytesToRead];
                    numBytesRead = readBytesToFillArray(newBytes);

                    final int[] newSamples = convertBytesToSamples(newBytes);
                    System.arraycopy(newSamples, 0, newSampleBuffer, samplesToKeep, newSamples.length);
                }
                else {
                    newBytes = new byte[0];
                    numBytesRead = 0;
                }

                sampleBuffer = newSampleBuffer;
            }

            // accumulate length of wave as bytes are read
            waveLength += numBytesRead / 2;

            // whenever fewer bytes are read than can fit in newBytes, it means we've reached the end of
            // the input stream.  at this point, we can compute the number of expected FFT frames
            if (areMoreBytesToRead && numBytesRead < newBytes.length) {
                areMoreBytesToRead = false;

                // now that we know the length of the entire wave, we can compute how many frames there should be
                final int lengthOfWave = waveLength / (isStereo() ? 2 : 1);
                final double frameOverlapMultiplier = 1 / (1 - windowOverlap);

                numExpectedFrames = (int) Math.ceil(((double) lengthOfWave / fftConfig.getWindowSize()) * frameOverlapMultiplier);
            }
        }
        catch (IOException ex) {
            getLogger().error(ex.getMessage(), ex);

            return new int[windowSize];
        }

        framesReadCount++;

        return sampleBuffer;
    }

    public void setFFTConfig(final FFTConfig fftConfig) {
        this.fftConfig = fftConfig;
    }

    protected Logger getLogger() {
        return logger;
    }

    /**
     * Converts a byte array consisting of 16-bit audio into a list of samples half as long
     * (each sample represented by 2 bytes)
     *
     * @param bytes byte array to be converted to samples
     *
     * @return an int[] representing the samples present in the input byte array
     */
    private int[] convertBytesToSamples(final byte[] bytes) {
        final int BYTES_PER_SAMPLE = 2;
        final int[] samples = new int[bytes.length / BYTES_PER_SAMPLE];

        for (int i = 0; i < samples.length; i++) {
            final int sample;

            if (getAudioFormat().isBigEndian()) {
                sample = bytes[2 * i + 1] & 0xFF | bytes[2 * i] << 8;
            }
            else {
                sample = bytes[2 * i] & 0xFF | bytes[2 * i + 1] << 8;
            }

            samples[i] = sample;
        }

        return samples;
    }

    /**
     * Reads from the input stream until enough bytes have been read to fill given byte array
     * This method acts as a wrapper for the inputStream.read() method because it doesn't guarantee that it'll
     * read enough bytes to fill the array.
     *
     * @param b byte array to fill with read bytes
     *
     * @return number of bytes actually read
     */
    private int readBytesToFillArray(final byte[] b) throws IOException {
        int numBytesRead = 0;
        int lastBytesRead = 0;

        while (numBytesRead < b.length && lastBytesRead != -1) {
            lastBytesRead = getAudioInputStream().read(b, numBytesRead, b.length - numBytesRead);

            if (lastBytesRead != -1) {
                numBytesRead += lastBytesRead;
            }
        }

        return numBytesRead;
    }
}
