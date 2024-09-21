// Created: 14 Sept. 2024
package de.freese.player.swing.component.library;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.ApplicationContext;
import de.freese.player.MusicPlayerRepository;
import de.freese.player.input.AudioSource;
import de.freese.player.library.LibraryScanner;
import de.freese.player.player.SongCollection;
import de.freese.player.swing.component.GbcBuilder;

/**
 * @author Thomas Freese
 */
public final class LibraryView {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryView.class);

    private final JList<Path> jList;
    private final JPanel panel = new JPanel(new GridBagLayout());

    public LibraryView() {
        super();

        final JLabel jLabel = new JLabel("Folder");
        panel.add(jLabel, GbcBuilder.of(0, 0).gridwidth(3).fillHorizontal().anchorWest());

        final JButton jButtonAdd = new JButton("Add");
        jButtonAdd.setFocusable(false);
        jButtonAdd.addActionListener(event -> addPath());
        panel.add(jButtonAdd, GbcBuilder.of(3, 0).fillNone().anchorEast());

        final JButton jButtonRemove = new JButton("Remove");
        jButtonRemove.setFocusable(false);
        jButtonRemove.addActionListener(event -> removePath());
        panel.add(jButtonRemove, GbcBuilder.of(4, 0).fillNone().anchorEast());

        final DefaultListModel<Path> listModel = new DefaultListModel<>();
        jList = new JList<>(listModel);
        jList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listModel.addAll(ApplicationContext.getRepository().getLibraryPaths());

        final JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(jList);
        jScrollPane.setPreferredSize(new Dimension(500, 200));
        panel.add(jScrollPane, GbcBuilder.of(0, 1).gridwidth(5).fillBoth());

        final JButton jButtonScan = new JButton("Scan");
        jButtonScan.setFocusable(false);
        jButtonScan.addActionListener(event -> scan());
        panel.add(jButtonScan, GbcBuilder.of(0, 2).gridwidth(5).anchorCenter());
    }

    public JComponent getComponent() {
        return panel;
    }

    private void addPath() {
        final FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Library Folder";
            }
        };

        final JFileChooser jFileChooser = new JFileChooser(Path.of(System.getProperty("user.dir")).toString());
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.setAcceptAllFileFilterUsed(false);
        jFileChooser.addChoosableFileFilter(fileFilter);

        final int choice = jFileChooser.showOpenDialog(panel.getParent());

        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        final File selectedFile = jFileChooser.getSelectedFile();
        final Path selectedPath = selectedFile.toPath();

        ((DefaultListModel<Path>) jList.getModel()).addElement(selectedPath);
        ApplicationContext.getRepository().saveLibraryPath(selectedPath);

        // final Set<String> supportedAudioFiles = AudioCodec.getSupportedFileExtensions();
        //
        // final FileFilter fileFilter = new FileFilter() {
        //     @Override
        //     public boolean accept(final File f) {
        //         if (f.isDirectory()) {
        //             return true;
        //         }
        //
        //         final String extension = PlayerUtils.getFileExtension(f);
        //
        //         return supportedAudioFiles.contains(extension);
        //     }
        //
        //     @Override
        //     public String getDescription() {
        //         return "Supported Audio Files";
        //     }
        // };
    }

    private void removePath() {
        final int selectedIndex = jList.getSelectedIndex();

        final Path path = ((DefaultListModel<Path>) jList.getModel()).remove(selectedIndex);
        ApplicationContext.getRepository().deleteLibraryPath(path);
    }

    private void scan() {
        final Set<Path> paths = new HashSet<>();

        for (int i = 0; i < jList.getModel().getSize(); i++) {
            paths.add(jList.getModel().getElementAt(i));
        }

        final SongCollection songCollection = ApplicationContext.getSongCollection();

        songCollection.clear();

        final SwingWorker<Void, AudioSource> swingWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                final MusicPlayerRepository repository = ApplicationContext.getRepository();
                final LibraryScanner libraryScanner = new LibraryScanner();

                libraryScanner.scan(paths, audioSource -> {
                    repository.saveOrUpdateSong(audioSource);

                    // try {
                    //     TimeUnit.SECONDS.sleep(1);
                    // }
                    // catch (InterruptedException ex) {
                    //     // Restore interrupted state.
                    //     Thread.currentThread().interrupt();
                    // }

                    publish(audioSource);
                });

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();

                    songCollection.clear();

                    final SwingWorker<Void, AudioSource> swingWorker = new SwingWorker<>() {
                        @Override
                        protected Void doInBackground() {
                            ApplicationContext.getRepository().loadSongs(this::publish);

                            return null;
                        }

                        @Override
                        protected void process(final List<AudioSource> chunks) {
                            songCollection.addAudioSources(chunks);
                        }
                    };
                    ApplicationContext.getExecutorService().execute(swingWorker);
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }

            @Override
            protected void process(final List<AudioSource> chunks) {
                songCollection.addAudioSources(chunks);
            }
        };
        ApplicationContext.getExecutorService().execute(swingWorker);
    }
}
