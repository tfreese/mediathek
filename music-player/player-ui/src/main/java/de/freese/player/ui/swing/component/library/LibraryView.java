// Created: 14 Sept. 2024
package de.freese.player.ui.swing.component.library;

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
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.input.AudioSource;
import de.freese.player.core.player.SongCollection;
import de.freese.player.ui.ApplicationContext;
import de.freese.player.ui.PlayerRepository;
import de.freese.player.ui.library.LibraryScanner;
import de.freese.player.ui.swing.component.GbcBuilder;
import de.freese.player.ui.swing.component.playlist.ReloadPlayListSwingWorker;

/**
 * @author Thomas Freese
 */
public final class LibraryView {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryView.class);

    private final JList<Path> jList;
    private final JProgressBar jProgressBar;
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
        jButtonRemove.setEnabled(false);
        jButtonRemove.addActionListener(event -> removePath());
        panel.add(jButtonRemove, GbcBuilder.of(4, 0).fillNone().anchorEast());

        final DefaultListModel<Path> listModel = new DefaultListModel<>();
        jList = new JList<>(listModel);
        jList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        final JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(jList);
        jScrollPane.setPreferredSize(new Dimension(500, 200));
        panel.add(jScrollPane, GbcBuilder.of(0, 1).gridwidth(5).fillBoth());

        final JButton jButtonScan = new JButton("Scan");
        jButtonScan.setFocusable(false);
        jButtonScan.setEnabled(false);
        jButtonScan.addActionListener(event -> scan());
        panel.add(jButtonScan, GbcBuilder.of(0, 2).gridwidth(5).anchorCenter());

        jProgressBar = new JProgressBar();
        jProgressBar.setEnabled(false);
        jProgressBar.setMinimum(0);
        panel.add(jProgressBar, GbcBuilder.of(0, 3).gridwidth(5).fillHorizontal());

        jList.addListSelectionListener(event -> jButtonRemove.setEnabled(jList.getSelectedValue() != null));
        listModel.addListDataListener(new ListDataListener() {
            @Override
            public void contentsChanged(final ListDataEvent e) {
                jButtonScan.setEnabled(listModel.getSize() > 0);
            }

            @Override
            public void intervalAdded(final ListDataEvent e) {
                contentsChanged(null);
            }

            @Override
            public void intervalRemoved(final ListDataEvent e) {
                contentsChanged(null);
            }
        });
        listModel.addAll(ApplicationContext.getRepository().getLibraryPaths());
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
        final Path path = jList.getSelectedValue();

        if (path == null) {
            return;
        }

        ApplicationContext.getRepository().deleteLibraryPath(path);

        ((DefaultListModel<Path>) jList.getModel()).removeElement(path);
    }

    private void scan() {
        if (jList.getModel().getSize() == 0) {
            return;
        }

        final Set<Path> paths = new HashSet<>();

        for (int i = 0; i < jList.getModel().getSize(); i++) {
            paths.add(jList.getModel().getElementAt(i));
        }

        final SongCollection songCollection = ApplicationContext.getSongCollection();

        songCollection.clear();

        final SwingWorker<Void, AudioSource> swingWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                final PlayerRepository repository = ApplicationContext.getRepository();
                final LibraryScanner libraryScanner = new LibraryScanner();

                libraryScanner.scan(paths,
                        size -> SwingUtilities.invokeLater(() -> {
                            jProgressBar.setEnabled(true);
                            jProgressBar.setString(null);
                            jProgressBar.setStringPainted(true);
                            jProgressBar.setValue(0);
                            jProgressBar.setMaximum(size);
                        }),
                        audioSource -> {
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

                    jProgressBar.setString(null);
                    jProgressBar.setStringPainted(false);
                    jProgressBar.setValue(0);
                    jProgressBar.setMaximum(0);

                    final SwingWorker<Void, AudioSource> swingWorker = new ReloadPlayListSwingWorker();
                    ApplicationContext.getExecutorService().execute(swingWorker);
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }

            @Override
            protected void process(final List<AudioSource> chunks) {
                jProgressBar.setValue(jProgressBar.getValue() + chunks.size());

                final int value = jProgressBar.getValue();
                final int maxValue = jProgressBar.getMaximum();
                jProgressBar.setString("%d / %d   (%.2f %%)".formatted(value, maxValue, ((double) value / (double) maxValue) * 100D));
                songCollection.addAudioSources(chunks);
            }
        };
        ApplicationContext.getExecutorService().execute(swingWorker);
    }
}
