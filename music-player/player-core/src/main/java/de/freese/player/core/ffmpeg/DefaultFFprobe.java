// Created: 15 Juli 2024
package de.freese.player.core.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import de.freese.player.core.exception.PlayerException;
import de.freese.player.core.input.AudioSource;
import de.freese.player.core.input.DefaultAudioSource;
import de.freese.player.core.util.PlayerUtils;

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
            audioSource.setSampleRate(samplingRate);
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
        final String line = output.stream()
                .filter(l -> l.startsWith("album ") || l.startsWith("ALBUM "))
                .findFirst()
                .orElse(null);

        if (line == null || line.isBlank()) {
            return null;
        }
        
        return line.substring(line.indexOf(":") + 1).strip();
    }

    private static String parseMetaDataArtist(final List<String> output) {
        String line = output.stream()
                .filter(l -> l.startsWith("artist") || l.startsWith("ARTIST"))
                .findFirst()
                .orElse(null);

        if (line == null || line.isBlank()) {
            line = output.stream()
                    .filter(l -> l.startsWith("artist-sort") || l.startsWith("ARTIST-SORT"))
                    .findFirst()
                    .orElse(null);
        }

        if (line == null || line.isBlank()) {
            // Compilation
            line = output.stream()
                    .filter(l -> l.startsWith("album_artist") || l.startsWith("ALBUM_ARTIST"))
                    .findFirst()
                    .orElse(null);
        }

        if (line == null || line.isBlank()) {
            return null;
        }

        return line.substring(line.indexOf(":") + 1).strip();
    }

    private static int parseMetaDataBitRate(final List<String> output) {
        String line = output.stream()
                .filter(l -> l.contains("Stream #") && l.contains("Audio:"))
                .findFirst()
                .orElse("");

        if (line.contains("kb/s")) {
            line = line.substring(line.lastIndexOf(','));

            final String[] splits = PATTERN_SPACES.split(line);

            return Integer.parseInt(splits[1].strip());
        }

        // Fallback
        line = output.stream()
                .filter(l -> l.contains("Duration:"))
                .findFirst()
                .orElse("");
        line = line.substring(line.lastIndexOf("bitrate:"));

        final String[] splits = PATTERN_SPACES.split(line);

        return Integer.parseInt(splits[1].strip());
    }

    private static int parseMetaDataChannels(final List<String> output) {
        String line = output.stream()
                .filter(l -> l.contains("Stream #") && l.contains("Audio:"))
                .findFirst()
                .orElse("");
        line = line.substring(line.indexOf("Hz,"));

        if (line.contains("channels")) {
            line = line.replace("Hz,", "").strip();

            final String[] splits = PATTERN_SPACES.split(line);

            return Integer.parseInt(splits[0].strip());
        }

        final String[] splits = PATTERN_SPACES.split(line);

        final String channelsValue = splits[1].replace(",", "").strip();
        int channels = -1;

        if ("mono".equalsIgnoreCase(channelsValue)) {
            channels = 1;
        }
        else if ("stereo".equalsIgnoreCase(channelsValue)) {
            channels = 2;
        }

        return channels;
    }

    private static boolean parseMetaDataCompilation(final List<String> output) {
        final String line = output.stream()
                .filter(l -> l.startsWith("compilation ") || l.startsWith("COMPILATION "))
                .findFirst()
                .orElse(null);

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
        final String line = output.stream()
                .filter(l -> l.startsWith("date ") || l.startsWith("DATE ") || l.startsWith("originaldate ") || l.startsWith("ORIGINALDATE "))
                .findFirst()
                .orElse(null);

        if (line == null || line.isBlank()) {
            return null;
        }

        final String[] splits = PATTERN_DOUBLE_DOT.split(line);

        return splits[1].strip();
    }

    private static String parseMetaDataDisk(final List<String> output) {
        final String line = output.stream()
                .filter(l -> l.startsWith("disc ") || l.startsWith("DISC "))
                .findFirst()
                .orElse(null);

        if (line == null || line.isBlank()) {
            return null;
        }

        final String[] splits = PATTERN_DOUBLE_DOT.split(line);

        return splits[1].strip();
    }

    private static Duration parseMetaDataDuration(final List<String> output) {
        String line = output.stream()
                .filter(l -> l.contains("Duration:"))
                .findFirst()
                .orElse("");
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
        String line = output.stream()
                .filter(l -> l.contains("Input #0,"))
                .findFirst()
                .orElse("");
        String[] splits = PATTERN_COMMA.split(line);

        final String formatInput = splits[1].strip();

        line = output.stream()
                .filter(l -> l.contains("Stream #") && l.contains("Audio:"))
                .findFirst()
                .orElse("");
        line = line.substring(line.indexOf("Audio:"));
        splits = PATTERN_SPACES.split(line);

        final String formatStream = splits[1].replace(",", "").strip();

        if (Objects.equals(formatInput, formatStream)) {
            return formatStream;
        }

        return String.join("/", formatInput, formatStream);
    }

    private static String parseMetaDataGenre(final List<String> output) {
        final String line = output.stream()
                .filter(l -> l.startsWith("genre ") || l.startsWith("GENRE "))
                .findFirst()
                .orElse(null);

        if (line == null || line.isBlank()) {
            return null;
        }

        return line.substring(line.indexOf(":") + 1).strip();
    }

    private static int parseMetaDataSamplingRate(final List<String> output) {
        String line = output.stream()
                .filter(l -> l.contains("Stream #") && l.contains("Audio:"))
                .findFirst()
                .orElse("");

        line = line.substring(0, line.indexOf("Hz"));
        line = line.substring(line.lastIndexOf(','));
        line = line.replace(",", "");

        return Integer.parseInt(line.strip());
    }

    private static String parseMetaDataTitle(final List<String> output) {
        final String line = output.stream()
                .filter(l -> l.startsWith("title ") || l.startsWith("TITLE "))
                .findFirst()
                .orElse(null);

        if (line == null || line.isBlank()) {
            return null;
        }

        return line.substring(line.indexOf(":") + 1).strip();
    }

    private static String parseMetaDataTrack(final List<String> output) {
        final String line = output.stream()
                .filter(l -> l.startsWith("track ") || l.startsWith("TRACK "))
                .findFirst()
                .orElse(null);

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
    public AudioSource getMetaData(final URI uri) {
        addArgument("-hide_banner");
        addArgument("-select_streams a");
        addArgument("-i");
        addArgument(PlayerUtils.toFileName(uri));

        final String command = createCommand();

        getLogger().debug("execute: {}", command);

        final ProcessBuilder processBuilder = createProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        try {
            final Process process = processBuilder.start();

            final List<String> output;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                output = br.lines().toList();
            }

            final int exitValue = process.waitFor();
            final String metaData = String.join(System.lineSeparator(), output);

            if (exitValue != 0) {
                throw new PlayerException("command: " + command + System.lineSeparator() + metaData);
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("{}", metaData);
            }

            final DefaultAudioSource audioSource = parseMetaData(output.stream().map(l -> PATTERN_SPACES.matcher(l).replaceAll(" ")).map(String::strip).toList());
            audioSource.setUri(uri);
            audioSource.setMetaData(metaData);

            return audioSource;
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (InterruptedException ex) {
            // Restore interrupted state.
            Thread.currentThread().interrupt();

            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getVersion() {
        return super.getVersion();
    }
}
