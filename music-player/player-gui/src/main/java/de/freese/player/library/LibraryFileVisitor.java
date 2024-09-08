// Created: 08 Sept. 2024
package de.freese.player.library;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.freese.player.model.AudioCodec;

/**
 * @author Thomas Freese
 */
public class LibraryFileVisitor implements FileVisitor<Path> {

    private final Consumer<Path> consumer;
    private final Set<String> supportedAudioFiles;

    public LibraryFileVisitor(final Consumer<Path> consumer) {
        super();

        this.consumer = Objects.requireNonNull(consumer, "consumer required");

        supportedAudioFiles = Arrays.stream(AudioCodec.values()).map(AudioCodec::getFileExtension).collect(Collectors.toSet());
    }

    @Override
    public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        final String fileName = file.getFileName().toString();
        final String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if (supportedAudioFiles.contains(fileExtension)) {
            consumer.accept(file);
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
