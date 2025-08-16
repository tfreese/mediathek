// Created: 15 Aug. 2025
package de.freese.player.test.ui.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * @author Thomas Freese
 */
public final class TestPlayer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestPlayer.class);

    static void main() {
        // Redirect Java-Util-Logger to Slf4J.
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        // AudioSystem.getTargetEncodings(AudioFormat.Encoding.PCM_SIGNED);
        // AudioSystem.getTargetFormats(AudioFormat.Encoding.PCM_SIGNED, DefaultAudioPlayerSink.getTargetAudioFormat());

        final Path tempPath = Path.of(System.getProperty("java.io.tmpdir"), ".music-player");
        System.setProperty("java.io.tmpdir", tempPath.toString());

        final ExecutorService executorService = Executors.newFixedThreadPool(8);

        try {
            Files.createDirectories(tempPath);

            SwingUtilities.invokeLater(() -> {
                final JFrame frame = new JFrame("Test Player");
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(final WindowEvent event) {
                        executorService.close();

                        try {
                            Files.walkFileTree(tempPath, new SimpleFileVisitor<>() {
                                @Override
                                public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
                                    Files.delete(dir);

                                    return FileVisitResult.CONTINUE;
                                }

                                @Override
                                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                                    Files.delete(file);

                                    return FileVisitResult.CONTINUE;
                                }
                            });
                        }
                        catch (IOException ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    }
                });

                final JTabbedPane jTabbedPane = new JTabbedPane();
                frame.add(jTabbedPane);

                jTabbedPane.addTab("AudioSystem", new PanelAudioSystem(executorService));
                jTabbedPane.addTab("Signals", new PanelSignal(executorService));
                jTabbedPane.addTab("Player", new PanelPlayer(executorService, tempPath));

                frame.setSize(800, 600);
                // frame.setSize(1280, 768);
                // frame.setSize(1280, 1024);
                // frame.setSize(1680, 1050);
                // frame.setSize(1920, 1080);
                // frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private TestPlayer() {
        super();
    }
}
