// Created: 11 Aug. 2024
package de.freese.player.fft.reader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Objects;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.freese.player.fft.config.FFTConfig;

/**
 * @author Thomas Freese
 */
final class DefaultAudioReader implements AudioReader {
    /**
     * Decodes audio reader's input stream to a target format with bit depth of 16.<br>
     * This is used when the input file is an 8-bit WAV or an MP3.
     */
    private static AudioInputStream wrapTo16Bit(final InputStream inputStream) throws IOException, UnsupportedAudioFileException {
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
    private final FFTConfig fftConfig;
    private final long fileLength;

    /**
     * Indicates whether all bytes in the input stream have been read yet (for SpectrumStream's hasNext() method).
     */
    private boolean areMoreBytesToRead = true;
    /**
     * Keep count of how many frames have been read (how many times SpectrumStream's next() has been called).
     */
    private int framesReadCount;
    /**
     * The number of FFT frames that should be extractable; not known until entire input stream has been read.
     */
    private int numExpectedFrames;
    private int[] sampleBuffer;
    /**
     * Keep count of how many samples there are in the full-length waveform as bytes are incrementally read.
     */
    private int waveLength;

    DefaultAudioReader(final Path audioFile, final FFTConfig fftConfig) throws UnsupportedAudioFileException, IOException {
        super();

        Objects.requireNonNull(audioFile, "audioFile required");

        final InputStream inputStream = new BufferedInputStream(Files.newInputStream(audioFile));
        AudioInputStream ais = AudioSystem.getAudioInputStream(inputStream);

        if (ais.getFormat().getSampleSizeInBits() == 8) {
            // convert 8-bit audio into 16-bit
            ais = wrapTo16Bit(inputStream);
        }

        audioInputStream = ais;
        fileLength = Files.size(audioFile);
        this.fftConfig = Objects.requireNonNull(fftConfig, "fftConfig required");
    }

    DefaultAudioReader(final AudioInputStream audioInputStream, final FFTConfig fftConfig) throws IOException {
        super();

        this.audioInputStream = Objects.requireNonNull(audioInputStream, "audioInputStream required");
        fileLength = audioInputStream.available();
        this.fftConfig = Objects.requireNonNull(fftConfig, "fftConfig required");
    }

    @Override
    public AudioFormat getAudioFormat() {
        return audioInputStream.getFormat();
    }

    @Override
    public long getDurationMs() {
        final AudioFormat format = getAudioFormat();
        final int frameSize = format.getFrameSize();
        final double frameRate = format.getFrameRate();

        return (long) Math.ceil((getLength() / (frameSize * frameRate)) * 1000D);
    }

    @Override
    public FFTConfig getFFTConfig() {
        return fftConfig;
    }

    @Override
    public long getLength() {
        return fileLength;
    }

    @Override
    public int[] getWaveform() {
        try {
            final byte[] audioBytes = audioInputStream.readAllBytes();
            return convertBytesToSamples(audioBytes);
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
            final int numBytesRead;

            if (sampleBuffer == null) {
                newBytes = new byte[windowSize * 2]; // 16-bit audio = 2 bytes per sample
                numBytesRead = audioInputStream.read(newBytes);
                // numBytesRead = readBytesToFillArray(newBytes);

                sampleBuffer = convertBytesToSamples(newBytes);
            }
            else {
                final int samplesToKeep = (int) Math.round(windowSize * windowOverlap);
                final int prevSamplesCopyStartIndex = windowSize - samplesToKeep;
                final int numMoreBytesToRead = (windowSize - samplesToKeep) * 2;

                final int[] newSampleBuffer = new int[windowSize];
                System.arraycopy(sampleBuffer, prevSamplesCopyStartIndex, newSampleBuffer, 0, samplesToKeep);

                if (areMoreBytesToRead) {
                    newBytes = new byte[numMoreBytesToRead];
                    numBytesRead = audioInputStream.read(newBytes);
                    // numBytesRead = readBytesToFillArray(newBytes);

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
            throw new UncheckedIOException(ex);
        }

        framesReadCount++;

        return sampleBuffer;
    }

    /**
     * Converts a byte array consisting of 16-bit audio into a list of samples half as long
     * (each sample represented by 2 bytes).
     */
    private int[] convertBytesToSamples(final byte[] audioBytes) {
        final int[] samples = new int[audioBytes.length / 2];
        final boolean isBigEndian = getAudioFormat().isBigEndian();

        for (int i = 0; i < samples.length; i++) {
            final int sample;

            if (isBigEndian) {
                sample = audioBytes[2 * i + 1] & 0xFF | audioBytes[2 * i] << 8;
            }
            else {
                sample = audioBytes[2 * i] & 0xFF | audioBytes[2 * i + 1] << 8;
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
     * @param bytes byte array to fill with read bytes
     *
     * @return number of bytes actually read
     */
    private int readBytesToFillArray(final byte[] bytes) throws IOException {
        int numBytesRead = 0;
        int lastBytesRead = 0;

        while (numBytesRead < bytes.length && lastBytesRead != -1) {
            lastBytesRead = audioInputStream.read(bytes, numBytesRead, bytes.length - numBytesRead);

            if (lastBytesRead != -1) {
                numBytesRead += lastBytesRead;
            }
        }

        return numBytesRead;
    }
}
