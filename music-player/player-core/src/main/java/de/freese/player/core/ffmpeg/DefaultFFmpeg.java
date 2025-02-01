// Created: 15 Juli 2024
package de.freese.player.core.ffmpeg;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Executor;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import de.freese.player.core.exception.PlayerException;
import de.freese.player.core.input.AudioSource;
import de.freese.player.core.util.PlayerUtils;

/**
 * @author Thomas Freese
 */
final class DefaultFFmpeg extends AbstractFF implements FFmpeg {
    private static AudioFormat getTargetAudioFormat(final AudioSource audioSource) {
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                audioSource.getSampleRate(),
                16,
                audioSource.getChannels(),
                audioSource.getChannels() * 2,
                audioSource.getSampleRate(),
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

    private final Executor executor;
    private final Path tempDir;

    DefaultFFmpeg(final String ffmpegExecutable, final Executor executor, final Path tempDir) {
        super(ffmpegExecutable);

        this.executor = Objects.requireNonNull(executor, "executor required");
        this.tempDir = Objects.requireNonNull(tempDir, "tempDir required");

        if (!Files.exists(this.tempDir)) {
            try {
                Files.createDirectories(this.tempDir);
            }
            catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }

    @Override
    public Path encodeToWav(final AudioSource audioSource) throws Exception {
        addArguments(audioSource);

        // Overwrite existing
        addArgument("-y");

        final Path tmpFile = Files.createTempFile(tempDir, null, ".wav");
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

        final Process process = processBuilder.start();

        // buffer = 1/4 second of audio.
        final int bufferSize = audioSource.getSampleRate() / 4;
        final InputStream inputStream = new BufferedInputStream(process.getInputStream(), bufferSize);
        // final InputStream inputStream = new BufferedInputStream(process.getInputStream());

        // Read and ignore the WAV header, only pipe the PCM samples to the AudioInputStream.
        final long bytesRead = inputStream.skip(44L);

        if (bytesRead != 44L) {
            throw new PlayerException("Could not read complete WAV-header from pipe. This could result in mis-aligned frames!");
        }

        executor.execute(() -> {
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

        final AudioFormat audioFormatTarget = getTargetAudioFormat(audioSource);

        return new AudioInputStream(inputStream, audioFormatTarget, AudioSystem.NOT_SPECIFIED);
    }

    private void addArguments(final AudioSource audioSource) {
        Objects.requireNonNull(audioSource, "audioSource required");
        Objects.requireNonNull(audioSource.getUri(), "uri required");

        // final AudioFormat targetAudioFormat = DefaultAudioPlayerSink.getTargetAudioFormat();

        addArgument("-hide_banner");
        addArgument("-i");
        addArgument(PlayerUtils.toFileName(audioSource.getUri()));

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
            // addArgument(String.valueOf(targetAudioFormat.getChannels()));
        }

        final int samplingRate = audioSource.getSampleRate();
        if (samplingRate > 0) {
            addArgument("-ar");
            addArgument(String.valueOf(samplingRate));
            // addArgument(String.valueOf(targetAudioFormat.getSampleRate()));
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
}
