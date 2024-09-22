// Created: 15 Juli 2024
package de.freese.player.core.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.freese.player.core.exception.PlayerException;
import de.freese.player.core.input.AudioSource;
import de.freese.player.core.input.DefaultAudioSource;

/**
 * @author Thomas Freese
 */
final class DefaultFFprobeRegEx extends AbstractFF implements FFprobe {
    private static final Pattern PATTERN_BIT_RATE = Pattern.compile("(\\d+)\\s+kb/s", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_CHANNELS = Pattern.compile("(mono|stereo|.*(\\d+).*channels)", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_DURATION =
            Pattern.compile(".*\\s*Duration: (\\d\\d):(\\d\\d):(\\d\\d).(\\d\\d),", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNIX_LINES);
    private static final Pattern PATTERN_DURATION_LINE =
            Pattern.compile("Duration:\\s*.*kb/s", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNIX_LINES);
    private static final Pattern PATTERN_INPUT = Pattern.compile(".*\\s*Input #0, (\\w+).+$\\s*.*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNIX_LINES);
    private static final Pattern PATTERN_SAMPLING_RATE = Pattern.compile("(\\d+)\\s+Hz", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_STREAM =
            Pattern.compile("Stream #.* ((?:Audio)|(?:Video)|(?:Data)): (.*)\\s*.*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNIX_LINES);

    DefaultFFprobeRegEx(final String ffprobeExecutable) {
        super(ffprobeExecutable);
    }

    @Override
    public AudioSource getMetaData(final URI uri) throws Exception {
        addArgument("-hide_banner");
        addArgument("-select_streams a");
        addArgument("-i");
        addArgument(uri.toString());

        final String command = createCommand();

        getLogger().debug("execute: {}", command);

        final ProcessBuilder processBuilder = createProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        final Process process = processBuilder.start();

        final String output;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            output = br.lines().collect(Collectors.joining(System.lineSeparator()));
        }

        final int exitValue = process.waitFor();

        if (exitValue != 0) {
            throw new IOException("command: " + command + System.lineSeparator() + String.join(System.lineSeparator(), output));
        }

        getLogger().debug("info: {}", output);

        final DefaultAudioSource audioFile = parseMetaData(output);
        audioFile.setUri(uri);

        return audioFile;
    }

    @Override
    public String getVersion() throws Exception {
        return super.getVersion();
    }

    private DefaultAudioSource parseMetaData(final String content) {
        final DefaultAudioSource audioFile = new DefaultAudioSource();

        // Format
        final String format = parseMetaDataFormat(content);

        if (format == null || format.isBlank()) {
            throw new PlayerException("MetaData Problems - Format invalid:" + System.lineSeparator() + content);
        }
        else {
            audioFile.setFormat(format);
        }

        // Duration
        final Duration duration = parseMetaDataDuration(content);

        if (duration == null || duration.toMillis() == 0L) {
            throw new PlayerException("MetaData Problems - Duration invalid:" + System.lineSeparator() + content);
        }
        else {
            audioFile.setDuration(duration);
        }

        // Channels
        final int channels = parseMetaDataChannels(content);

        if (channels < 1) {
            throw new PlayerException("MetaData Problems - Channels invalid: " + channels + System.lineSeparator() + content);
        }
        else {
            audioFile.setChannels(channels);
        }

        // Bit-Rate
        final int bitRate = parseMetaDataBitRate(content);

        if (bitRate <= 0) {
            throw new PlayerException("MetaData Problems - Bit-Rate invalid: " + bitRate + System.lineSeparator() + content);
        }
        else {
            audioFile.setBitRate(bitRate);
        }

        // Sampling-Rate
        final int samplingRate = parseMetaDataSamplingRate(content);

        if (samplingRate <= 0) {
            throw new PlayerException("MetaData Problems - Sampling-Rate invalid: " + samplingRate + System.lineSeparator() + content);
        }
        else {
            audioFile.setSamplingRate(samplingRate);
        }

        return audioFile;
    }

    private int parseMetaDataBitRate(final String content) {
        Matcher matcher = PATTERN_STREAM.matcher(content);

        if (matcher.find()) {
            final String streamType = matcher.group(1);
            final String specs = matcher.group(2);

            if ("Audio".equalsIgnoreCase(streamType)) {
                final String[] splits = specs.split(",");
                final String value = splits[splits.length - 1];

                final Matcher m2 = PATTERN_BIT_RATE.matcher(value);

                if (m2.find()) {
                    final int bitRate = Integer.parseInt(m2.group(1));

                    return bitRate;
                }
            }
        }

        // Fallback
        matcher = PATTERN_DURATION_LINE.matcher(content);

        if (matcher.find()) {
            final String durationLine = matcher.group(0);
            final String[] splits = PATTERN_SPACES.split(durationLine);

            if ("kb/s".equals(splits[splits.length - 1])) {
                final int bitRate = Integer.parseInt(splits[splits.length - 2]);

                return bitRate;
            }
        }

        return -1;
    }

    private int parseMetaDataChannels(final String content) {
        final Matcher matcher = PATTERN_STREAM.matcher(content);

        if (matcher.find()) {
            final String streamType = matcher.group(1);
            final String specs = matcher.group(2);

            if ("Audio".equalsIgnoreCase(streamType)) {
                final Matcher m2 = PATTERN_CHANNELS.matcher(specs);

                if (m2.find()) {
                    final String value = m2.group(1);

                    if ("mono".equalsIgnoreCase(value)) {
                        return 1;
                    }
                    else if ("stereo".equalsIgnoreCase(value)) {
                        return 2;
                    }
                    else {
                        return Integer.parseInt(m2.group(2));
                    }
                }
            }
        }

        return -1;
    }

    private Duration parseMetaDataDuration(final String content) {
        final Matcher matcher = PATTERN_DURATION.matcher(content);

        if (matcher.find()) {
            final Duration duration = Duration.ofHours(Long.parseLong(matcher.group(1)))
                    .plusMinutes(Long.parseLong(matcher.group(2)))
                    .plusSeconds(Long.parseLong(matcher.group(3)))
                    .plusMillis(Long.parseLong(matcher.group(4)) * 10);

            return duration;
        }

        return null;
    }

    private String parseMetaDataFormat(final String content) {
        Matcher matcher = PATTERN_INPUT.matcher(content);

        String formatInput = null;

        if (matcher.find()) {
            formatInput = matcher.group(1);
        }

        String formatStream = null;
        matcher = PATTERN_STREAM.matcher(content);

        if (matcher.find()) {
            final String streamType = matcher.group(1);
            final String specs = matcher.group(2);

            if ("Audio".equalsIgnoreCase(streamType)) {
                formatStream = PATTERN_SPACES.split(specs)[0];

                if (formatStream.endsWith(",")) {
                    formatStream = formatStream.substring(0, formatStream.length() - 1);
                }
            }
        }

        if (formatInput == null && formatStream == null) {
            return null;
        }

        if (Objects.equals(formatInput, formatStream)) {
            return formatStream;
        }

        return String.join("/", formatInput, formatStream);
    }

    private int parseMetaDataSamplingRate(final String content) {
        final Matcher matcher = PATTERN_STREAM.matcher(content);

        if (matcher.find()) {
            final String streamType = matcher.group(1);
            final String specs = matcher.group(2);

            if ("Audio".equalsIgnoreCase(streamType)) {
                final Matcher m2 = PATTERN_SAMPLING_RATE.matcher(specs);

                if (m2.find()) {
                    final int samplingRate = Integer.parseInt(m2.group(1));

                    return samplingRate;
                }
            }
        }

        return -1;
    }
}
