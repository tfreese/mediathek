// Created: 08 Sept. 2024
package de.freese.player.ui.library;

import java.io.IOException;
import java.net.URI;
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
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.input.AudioSource;
import de.freese.player.core.input.AudioSourceFactory;
import de.freese.player.ui.ApplicationContext;

/**
 * @author Thomas Freese
 */
public final class LibraryScanner {
    private static final FileVisitOption[] FILEVISITOPTION_NO_SYNLINKS = {};
    private static final FileVisitOption[] FILEVISITOPTION_WITH_SYMLINKS = {FileVisitOption.FOLLOW_LINKS};
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryScanner.class);

    public void scan(final Set<Path> paths, final IntConsumer sizeConsumer, final Consumer<AudioSource> audioSourceConsumer) {
        final List<Path> files = new ArrayList<>();

        for (Path path : paths) {
            try {
                Files.walkFileTree(path, Set.of(FILEVISITOPTION_NO_SYNLINKS), Integer.MAX_VALUE, new LibraryFileVisitor(files::add));
            }
            catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }
        }

        sizeConsumer.accept(files.size());

        // Delete Orphan Songs.
        final Set<URI> uris = files.stream().map(Path::toUri).collect(Collectors.toSet());
        ApplicationContext.getRepository().deleteOrphansSongs(uris);

        final CompletionService<Void> completionService = new ExecutorCompletionService<>(ApplicationContext.getExecutorService());
        final List<Future<Void>> futures = new ArrayList<>(files.size());

        uris.forEach(uri -> {
            final Future<Void> future = completionService.submit(() -> {
                try {
                    final AudioSource audioSource = AudioSourceFactory.createAudioSource(uri);
                    audioSourceConsumer.accept(audioSource);
                }
                catch (Exception ex) {
                    LOGGER.error("{}: {}", uri, ex.getMessage());
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
