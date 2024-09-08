// Created: 15 Juli 2024
package de.freese.player.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import de.freese.player.exception.PlayerException;
import de.freese.player.input.AudioSource;
import de.freese.player.input.DefaultAudioSource;

/**
 * @author Thomas Freese
 */
final class DefaultFFprobe extends AbstractFF implements FFprobe {

    private static DefaultAudioSource parseMetaData(final List<String> output) {
        final DefaultAudioSource audioSource = new DefaultAudioSource();

        // Format
        final String format = parseMetaDataFormat(output);

        if (format == null || format.isBlank()) {
            throw new PlayerException("MetaData Problems - Format invalid: " + System.lineSeparator() + String.join(System.lineSeparator(), output));
        }
        else {
            audioSource.setFormat(format);
        }

        // Duration
        final Duration duration = parseMetaDataDuration(output);

        if (duration == null || duration.toMillis() == 0L) {
            throw new PlayerException("MetaData Problems - Duration invalid: " + System.lineSeparator() + String.join(System.lineSeparator(), output));
        }
        else {
            audioSource.setDuration(duration);
        }

        // Channels
        final int channels = parseMetaDataChannels(output);

        if (channels < 1) {
            throw new PlayerException("MetaData Problems - Channels invalid: " + channels + System.lineSeparator() + String.join(System.lineSeparator(), output));
        }
        else {
            audioSource.setChannels(channels);
        }

        // Bit-Rate
        final int bitRate = parseMetaDataBitRate(output);

        if (bitRate <= 0) {
            throw new PlayerException("MetaData Problems - Bit-Rate invalid: " + bitRate + System.lineSeparator() + String.join(System.lineSeparator(), output));
        }
        else {
            audioSource.setBitRate(bitRate);
        }

        // Sampling-Rate
        final int samplingRate = parseMetaDataSamplingRate(output);

        if (samplingRate <= 0) {
            throw new PlayerException("MetaData Problems - Sampling-Rate invalid: " + samplingRate + System.lineSeparator() + String.join(System.lineSeparator(), output));
        }
        else {
            audioSource.setSamplingRate(samplingRate);
        }

        audioSource.setAlbum(parseMetaDataAlbum(output));
        audioSource.setArtist(parseMetaDataArtist(output));
        audioSource.setTitle(parseMetaDataTitle(output));
        audioSource.setGenre(parseMetaDataGenre(output));
        audioSource.setReleaseDate(parseMetaDataDate(output));
        audioSource.setDisc(parseMetaDataDisk(output));
        audioSource.setTrack(parseMetaDataTrack(output));
        audioSource.setCompilation(parseMetaDataCompilation(output));

        return audioSource;
    }

    private static String parseMetaDataAlbum(final List<String> output) {
        final String line = output.stream().filter(l -> l.contains("album ")).findFirst().orElse(null);

        if (line == null || line.isBlank()) {
            return null;
        }

        final String[] splits = PATTERN_DOUBLE_DOT.split(line);

        return splits[1].strip();
    }

    private static String parseMetaDataArtist(final List<String> output) {
        String line = output.stream().filter(l -> l.contains("artist")).findFirst().orElse(null);

        if (line == null || line.isBlank()) {
            line = output.stream().filter(l -> l.contains("artist-sort")).findFirst().orElse(null);
        }

        if (line == null || line.isBlank()) {
            // Compilation
            line = output.stream().filter(l -> l.contains("album_artist")).findFirst().orElse(null);
        }

        if (line == null || line.isBlank()) {
            return null;
        }

        final String[] splits = PATTERN_DOUBLE_DOT.split(line);

        return splits[1].strip();
    }

    private static int parseMetaDataBitRate(final List<String> output) {
        String line = output.stream().filter(l -> l.contains("Stream #") && l.contains("Audio:")).findFirst().orElse("");

        if (line.contains("kb/s")) {
            line = line.substring(line.lastIndexOf(','));

            final String[] splits = PATTERN_SPACES.split(line);

            return Integer.parseInt(splits[1].strip());
        }

        // Fallback
        line = output.stream().filter(l -> l.contains("Duration:")).findFirst().orElse("");
        line = line.substring(line.lastIndexOf("bitrate:"));

        final String[] splits = PATTERN_SPACES.split(line);

        return Integer.parseInt(splits[1].strip());
    }

    private static int parseMetaDataChannels(final List<String> output) {
        String line = output.stream().filter(l -> l.contains("Stream #") && l.contains("Audio:")).findFirst().orElse("");
        line = line.substring(line.indexOf("Hz,"));

        if (line.contains("channels")) {
            line = line.replace("Hz,", "").strip();

            final String[] splits = PATTERN_SPACES.split(line);

            return Integer.parseInt(splits[0].strip());
        }

        final String[] splits = PATTERN_SPACES.split(line);

        final String channels = splits[1].replace(",", "").strip();

        if ("mono".equalsIgnoreCase(channels)) {
            return 1;
        }
        else if ("stereo".equalsIgnoreCase(channels)) {
            return 2;
        }

        return -1;
    }

    private static boolean parseMetaDataCompilation(final List<String> output) {
        final String line = output.stream().filter(l -> l.contains("compilation")).findFirst().orElse(null);

        if (line == null || line.isBlank()) {
            return false;
        }

        final String[] splits = PATTERN_DOUBLE_DOT.split(line);
        final String value = splits[1].strip();

        if ("0".equals(value)) {
            return false;
        }
        else if ("1".equals(value)) {
            return true;
        }

        return Boolean.parseBoolean(value);
    }

    private static String parseMetaDataDate(final List<String> output) {
        final String line = output.stream().filter(l -> l.contains("date")).findFirst().orElse(null);

        if (line == null || line.isBlank()) {
            return null;
        }

        final String[] splits = PATTERN_DOUBLE_DOT.split(line);

        return splits[1].strip();
    }

    private static String parseMetaDataDisk(final List<String> output) {
        final String line = output.stream().filter(l -> l.contains("disc")).findFirst().orElse(null);

        if (line == null || line.isBlank()) {
            return null;
        }

        final String[] splits = PATTERN_DOUBLE_DOT.split(line);

        return splits[1].strip();
    }

    private static Duration parseMetaDataDuration(final List<String> output) {
        String line = output.stream().filter(l -> l.contains("Duration:")).findFirst().orElse("");
        line = line.replace("Duration:", "");
        line = line.substring(0, line.indexOf(','));
        line = line.replace('.', ':').strip();

        final String[] splits = PATTERN_DOUBLE_DOT.split(line);

        return Duration.ofHours(Long.parseLong(splits[0]))
                .plusMinutes(Long.parseLong(splits[1]))
                .plusSeconds(Long.parseLong(splits[2]))
                .plusMillis(Long.parseLong(splits[3]) * 10);
    }

    private static String parseMetaDataFormat(final List<String> output) {
        String line = output.stream().filter(l -> l.contains("Input #0,")).findFirst().orElse("");
        String[] splits = PATTERN_COMMA.split(line);

        final String formatInput = splits[1].strip();

        line = output.stream().filter(l -> l.contains("Stream #") && l.contains("Audio:")).findFirst().orElse("");
        line = line.substring(line.indexOf("Audio:"));
        splits = PATTERN_SPACES.split(line);

        final String formatStream = splits[1].replace(",", "").strip();

        if (Objects.equals(formatInput, formatStream)) {
            return formatStream;
        }

        return String.join("/", formatInput, formatStream);
    }

    private static String parseMetaDataGenre(final List<String> output) {
        final String line = output.stream().filter(l -> l.contains("genre")).findFirst().orElse(null);

        if (line == null || line.isBlank()) {
            return null;
        }

        final String[] splits = PATTERN_DOUBLE_DOT.split(line);

        return splits[1].strip();
    }

    private static int parseMetaDataSamplingRate(final List<String> output) {
        String line = output.stream().filter(l -> l.contains("Stream #") && l.contains("Audio:")).findFirst().orElse("");

        line = line.substring(0, line.indexOf("Hz"));
        line = line.substring(line.lastIndexOf(','));
        line = line.replace(",", "");

        return Integer.parseInt(line.strip());
    }

    private static String parseMetaDataTitle(final List<String> output) {
        final String line = output.stream().filter(l -> l.contains("title")).findFirst().orElse(null);

        if (line == null || line.isBlank()) {
            return null;
        }

        final String[] splits = PATTERN_DOUBLE_DOT.split(line);

        return splits[1].strip();
    }

    private static String parseMetaDataTrack(final List<String> output) {
        final String line = output.stream().filter(l -> l.contains("track")).findFirst().orElse(null);

        if (line == null || line.isBlank()) {
            return null;
        }

        final String[] splits = PATTERN_DOUBLE_DOT.split(line);

        return splits[1].strip();
    }

    DefaultFFprobe(final String ffprobeExecutable) {
        super(ffprobeExecutable);
    }

    @Override
    public AudioSource getMetaData(final URI uri) throws Exception {
        addArgument("-hide_banner");
        addArgument("-select_streams a");
        addArgument("-i");
        addArgument(toFileName(uri));

        final String command = createCommand();

        getLogger().debug("execute: {}", command);

        final ProcessBuilder processBuilder = createProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        final Process process = processBuilder.start();

        final List<String> output;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            output = br.lines().toList();
        }

        final int exitValue = process.waitFor();
        final String metaData = String.join(System.lineSeparator(), output);

        if (exitValue != 0) {
            throw new IOException("command: " + command + System.lineSeparator() + metaData);
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("info: {}", metaData);
        }

        final DefaultAudioSource audioSource = parseMetaData(output);
        audioSource.setUri(uri);
        audioSource.setMetaData(metaData);

        return audioSource;
    }

    @Override
    public String getVersion() throws Exception {
        return super.getVersion();
    }
}
