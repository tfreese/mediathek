// Created: 08 Sept. 2024
package de.freese.player.library;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.ApplicationContext;
import de.freese.player.input.AudioSource;
import de.freese.player.input.AudioSourceFactory;

/**
 * @author Thomas Freese
 */
public final class LibraryScanner {
    private static final FileVisitOption[] FILEVISITOPTION_NO_SYNLINKS = {};
    private static final FileVisitOption[] FILEVISITOPTION_WITH_SYMLINKS = {FileVisitOption.FOLLOW_LINKS};
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryScanner.class);

    public void scan(final Set<Path> paths, final Consumer<AudioSource> audioSourceConsumer) {
        final List<Path> files = new ArrayList<>();

        for (Path path : paths) {
            try {
                Files.walkFileTree(path, Set.of(FILEVISITOPTION_NO_SYNLINKS), Integer.MAX_VALUE, new LibraryFileVisitor(files::add));
            }
            catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }
        }

        final CompletionService<Void> completionService = new ExecutorCompletionService<>(ApplicationContext.getExecutorService());
        final List<Future<Void>> futures = new ArrayList<>(files.size());

        files.forEach(path -> {
            final Future<Void> future = completionService.submit(() -> {
                try {
                    final AudioSource audioSource = AudioSourceFactory.createAudioSource(path);
                    audioSourceConsumer.accept(audioSource);
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage());
                }

                return null;
            });

            futures.add(future);
        });

        // Wait until all are finished.
        try {
            for (Future<Void> future : futures) {
                future.get();
            }
        }
        catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
        catch (ExecutionException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
