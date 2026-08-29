package de.freese.player.ui.library;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;

import de.freese.player.core.model.AudioCodec;
import de.freese.player.core.util.PlayerUtils;

/**
 * @author Thomas Freese
 * @since 08.09.2024
 */
public class LibraryFileVisitor implements FileVisitor<Path> {

    private final Consumer<Path> consumer;
    private final Set<String> supportedAudioFiles;

    public LibraryFileVisitor(final Consumer<Path> consumer) {
        super();

        this.consumer = Objects.requireNonNull(consumer, "consumer required");

        supportedAudioFiles = AudioCodec.getSupportedFileExtensions();
    }

    @Override
    public @NonNull FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public @NonNull FileVisitResult preVisitDirectory(final Path dir, final @NonNull BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public @NonNull FileVisitResult visitFile(final Path file, final @NonNull BasicFileAttributes attrs) throws IOException {
        final String fileExtension = PlayerUtils.getFileExtension(file);

        if (supportedAudioFiles.contains(fileExtension)) {
            consumer.accept(file);
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public @NonNull FileVisitResult visitFileFailed(final Path file, final @NonNull IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
