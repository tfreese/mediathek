// Created: 16 Juli 2024
package de.freese.player.core.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.exception.PlayerException;

/**
 * @author Thomas Freese
 */
abstract class AbstractFF {
    protected static final Pattern PATTERN_COMMA = Pattern.compile(",", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNIX_LINES);
    protected static final Pattern PATTERN_DOUBLE_DOT = Pattern.compile(":", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNIX_LINES);
    protected static final Pattern PATTERN_SPACES = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNIX_LINES);

    private final List<String> arguments = new ArrayList<>();
    private final String executable;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected AbstractFF(final String executable) {
        super();

        this.executable = Objects.requireNonNull(executable, "executable required");
    }

    protected void addArgument(final String argument) {
        arguments.add(argument);

    }

    protected String createCommand() {
        return executable + " " + String.join(" ", arguments);
    }

    protected ProcessBuilder createProcessBuilder(final String command) {
        final String processEnvironment;
        final String processArgument;

        if (System.getProperty("os.name").contains("indows")) {
            processEnvironment = "cmd.exe";
            processArgument = "/C";
        }
        else {
            processEnvironment = "/bin/bash";
            processArgument = "-c";
        }

        return new ProcessBuilder(processEnvironment, processArgument, command);
    }

    protected Logger getLogger() {
        return logger;
    }

    protected String getVersion() {
        addArgument("-version");

        final String command = createCommand();
        final ProcessBuilder processBuilder = createProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        try {
            final Process process = processBuilder.start();
            final List<String> output;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                output = br.lines().toList();
            }

            final int exitValue = process.waitFor();

            if (exitValue != 0) {
                throw new PlayerException("command: " + command + System.lineSeparator() + String.join(System.lineSeparator(), output));
            }

            return output.getFirst();
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
}
