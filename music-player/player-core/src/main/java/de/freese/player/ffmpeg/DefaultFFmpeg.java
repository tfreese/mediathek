// Created: 15 Juli 2024
package de.freese.player.ffmpeg;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import de.freese.player.PlayerSettings;
import de.freese.player.exception.PlayerException;
import de.freese.player.input.AudioSource;

/**
 * @author Thomas Freese
 */
final class DefaultFFmpeg extends AbstractFF implements FFmpeg {
    private static AudioFormat getTargetAudioFormat(final AudioSource audioSource) {
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                audioSource.getSamplingRate(),
                16,
                audioSource.getChannels(),
                audioSource.getChannels() * 2,
                audioSource.getSamplingRate(),
                ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder()) // false
        );
        // return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
        //         44100,
        //         16,
        //         2,
        //         2 * 2,
        //         44100,
        //         ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder()) // false
        // );
    }

    DefaultFFmpeg(final String ffmpegExecutable) {
        super(ffmpegExecutable);
    }

    @Override
    public Path encodeToWav(final AudioSource audioSource) throws Exception {
        addArguments(audioSource);

        // Overwrite existing
        addArgument("-y");

        final Path tmpDir = Path.of(System.getProperty("java.io.tmpdir"), "musicPlayer");

        if (!Files.exists(tmpDir)) {
            Files.createDirectories(tmpDir);
        }

        // final Path tmpDir = Files.createTempDirectory("musicPlayer");

        final Path tmpFile = Files.createTempFile(tmpDir, null, ".wav");
        tmpFile.toFile().deleteOnExit();

        addArgument(tmpFile.toAbsolutePath().toString());

        final String command = createCommand();
        final ProcessBuilder processBuilder = createProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        getLogger().debug("execute: {}", command);

        final Process process = processBuilder.start();
        final int exitValue = process.waitFor();

        if (exitValue == 0) {
            getLogger().debug("Finished piped decoding process: {}", command);
        }
        else {
            getLogger().error("Finished piped decoding process with exitValue '{}': {}", exitValue, command);
        }

        return tmpFile;
    }

    @Override
    public String getVersion() throws Exception {
        return super.getVersion();
    }

    @Override
    public AudioInputStream toAudioStreamWav(final AudioSource audioSource) throws Exception {
        addArguments(audioSource);

        // Pipe the output to stdout
        addArgument("pipe:1");

        final String command = createCommand();
        final ProcessBuilder processBuilder = createProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        getLogger().debug("execute: {}", command);

        final AudioFormat audioFormatTarget = getTargetAudioFormat(audioSource);

        final Process process = processBuilder.start();

        // buffer = 1/4 second of audio.
        // final int bufferSize = sourceAttributes.getSamplingRate() / 4;
        // final InputStream inputStream = new BufferedInputStream(process.getInputStream(), bufferSize);
        final InputStream inputStream = new BufferedInputStream(process.getInputStream());

        // Read and ignore the WAV header, only pipe the PCM samples to the AudioInputStream.
        final long bytesRead = inputStream.skip(44L);
        // final byte[] header = new byte[44];
        // final int bytesRead = inputStream.read(header);

        if (bytesRead != 44L) {
            throw new PlayerException("Could not read complete WAV-header from pipe. This could result in mis-aligned frames!");
        }

        // if (getLogger().isDebugEnabled()) {
        //     debugWavHeader(header);
        // }

        PlayerSettings.getExecutorServicePipeReader().execute(() -> {
            try {
                final int exitValue = process.waitFor();

                if (exitValue == 0) {
                    getLogger().debug("Finished piped decoding process: {}", command);
                }
                else {
                    getLogger().error("Finished piped decoding process with exitValue '{}': {}", exitValue, command);
                }
            }
            catch (InterruptedException ex) {
                getLogger().error(ex.getMessage(), ex);

                // Restore interrupted state.
                Thread.currentThread().interrupt();
            }
        });

        return new AudioInputStream(inputStream, audioFormatTarget, AudioSystem.NOT_SPECIFIED);
    }

    private void addArguments(final AudioSource audioSource) {
        Objects.requireNonNull(audioSource, "audioSource required");
        Objects.requireNonNull(audioSource.getUri(), "uri required");

        addArgument("-hide_banner");
        addArgument("-i");
        addArgument(audioSource.getUri().getPath());
        // addArgument(audioSource.getUri().toString()); // Create white noise

        // final Integer seekTime = attributes.getSeekTime();
        // if (seekTime != null) {
        //     addArgument("-ss");
        //     final int S = seekTime; // ms
        //     final int s = S / 1000;   // sec
        //     final int m = s / 60;     // min
        //     final int h = m / 60;     // hr
        //     addArgument("%02d:%02d:%02d.%03d".formatted(h % 100, m % 60, s % 60, S % 1000));
        // }

        // no video streams
        addArgument("-vn");

        // no data streams
        addArgument("-dn");

        // no subtitle streams
        addArgument("-sn");

        // only 1st audio stream
        addArgument("-map 0:a:0");

        // only 1st audio stream for englisch
        // addArgument("-map 0:a:m:language:eng");

        final int bitRate = audioSource.getBitRate();
        if (bitRate > 0) {
            addArgument("-ab"); // -ab, -b:a
            addArgument(bitRate + "k");
        }

        final int channels = audioSource.getChannels();
        if (channels > 0) {
            addArgument("-ac");
            addArgument(String.valueOf(channels));
        }

        final int samplingRate = audioSource.getSamplingRate();
        if (samplingRate > 0) {
            addArgument("-ar");
            addArgument(String.valueOf(samplingRate));
        }

        // final int volume = audioSource.getVolume();
        // if (volume > 0) {
        //     addArgument("-vol");
        //     addArgument(String.valueOf(volume));
        // }

        // Required for WAV Output.
        addArgument("-acodec pcm_s16le");

        // Output-Format.
        addArgument("-f wav");
    }

    // private void debugWavHeader(final byte[] header) {
    //     getLogger().debug("TYPE: {}", new String(Arrays.copyOfRange(header, 0, 4)));
    //     getLogger().debug("FileSize[b]: {}", toInt(Arrays.copyOfRange(header, 4, 8)));
    //     getLogger().debug("WAVE: {}", new String(Arrays.copyOfRange(header, 8, 12)));
    //     getLogger().debug("FMT: {}", new String(Arrays.copyOfRange(header, 12, 16)));
    //     getLogger().debug("FMT-Length: {}", new String(Arrays.copyOfRange(header, 16, 20)));
    //     getLogger().debug("FORMAT: {}", toShort(Arrays.copyOfRange(header, 20, 22)));
    //     getLogger().debug("Channels: {}", toShort(Arrays.copyOfRange(header, 22, 24)));
    //     getLogger().debug("SampleRate: {}", toInt(Arrays.copyOfRange(header, 24, 28)));
    //     getLogger().debug("Bytes/Second: {}", toInt(Arrays.copyOfRange(header, 28, 32)));
    //     getLogger().debug("FrameSize: {}", toShort(Arrays.copyOfRange(header, 32, 34)));
    //     getLogger().debug("Bits/Sample: {}", toShort(Arrays.copyOfRange(header, 34, 36)));
    //     getLogger().debug("Signature: {}", toInt(Arrays.copyOfRange(header, 36, 40)));
    //     getLogger().debug("Data-Length: {}", toInt(Arrays.copyOfRange(header, 40, 44)));
    // }
    //
    // private int toInt(final byte[] value) {
    //     return ((value[0] & 0xFF) << 24)
    //             + ((value[1] & 0xFF) << 16)
    //             + ((value[2] & 0xFF) << 8)
    //             + (value[3] & 0xFF);
    // }
    //
    // public short toShort(final byte[] value) {
    //     return (short) (((value[0] & 0xFF) << 8) + (value[1] & 0xFF));
    // }
}
