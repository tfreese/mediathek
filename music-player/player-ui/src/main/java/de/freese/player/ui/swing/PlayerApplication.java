// Created: 18 Aug. 2024
package de.freese.player.ui.swing;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.player.core.input.AudioSource;
import de.freese.player.ui.ApplicationContext;
import de.freese.player.ui.swing.component.PlayerView;
import de.freese.player.ui.swing.component.library.LibraryView;
import de.freese.player.ui.swing.component.playlist.PlayListView;
import de.freese.player.ui.swing.component.playlist.ReloadPlayListSwingWorker;

/**
 * @author Thomas Freese
 */
public final class PlayerApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerApplication.class);

    private static JFrame mainFrame;

    private static final class MainFrameListener extends WindowAdapter {
        @Override
        public void windowClosing(final WindowEvent event) {
            stop();
        }
    }

    public static JFrame getMainFrame() {
        return mainFrame;
    }

    public static void init() throws Exception {
        LOGGER.info("initialize application");

        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        // final UIDefaults defaults = UIManager.getLookAndFeelDefaults();

        final Color defaultBackground = new Color(55, 55, 55);
        // final Color defaultForeground = new Color(235, 235, 235);

        UIManager.put("FileChooser.useSystemIcons", Boolean.TRUE);

        // UIManager.put("Button.background", defaultBackground);
        // UIManager.put("Button.foreground", defaultForeground);
        // UIManager.put("ToggleButton.background", defaultBackground);
        // UIManager.put("ToggleButton.foreground", defaultForeground);
        //
        // UIManager.put("Label.foreground", defaultForeground);
        // UIManager.put("List.background", defaultBackground);
        // UIManager.put("List.foreground", defaultForeground);
        //
        // UIManager.put("MenuBar.background", defaultBackground);
        // UIManager.put("Menu.background", defaultBackground);
        // UIManager.put("Menu.foreground", defaultForeground);
        // UIManager.put("MenuItem.background", defaultBackground);
        // UIManager.put("MenuItem.foreground", defaultForeground);
        //
        // UIManager.put("Panel.background", defaultBackground);
        //
        // UIManager.put("ScrollBar.background", defaultBackground);
        // // UIManager.put("ScrollBar.thumb", new ColorUIResource(defaultBackground));
        // UIManager.put("ScrollPane.background", defaultBackground);

        UIManager.put("Table.alternateRowColor", defaultBackground);
        // UIManager.put("Table.alternateRowColor", UIManager.getColor("TableHeader.background"));
        // UIManager.put("Table.background", new ColorUIResource(defaultBackground));
        // UIManager.put("Table.foreground", defaultForeground);
        // UIManager.put("TableHeader.background", defaultBackground);
        // UIManager.put("TableHeader.foreground", defaultForeground);

        UIManager.put("ToolTip.background", UIManager.getColor("Label.background").darker());
        UIManager.put("ToolTip.foreground", UIManager.getColor("Label.foreground"));

        // UIManager.put("Viewport.background", defaultBackground);

        final JFrame jFrame = new JFrame();
        jFrame.setTitle("Music-Player");
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.addWindowListener(new MainFrameListener());
        // jFrame.setLayout(new BorderLayout());

        final PlayerView playerView = new PlayerView();
        playerView.init();
        jFrame.setContentPane(playerView.getComponent());

        initMenu(jFrame);

        // frame.setSize(800, 600);
        // frame.setSize(1280, 768);
        // frame.setSize(1280, 1024);
        // frame.setSize(1680, 1050);
        jFrame.setSize(1920, 1080);
        // jFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        jFrame.setLocationRelativeTo(null);

        mainFrame = jFrame;

        // Runtime.getRuntime().addShutdownHook(new Thread(PlayerApplication::stop, "Shutdown"));
    }

    public static void start() {
        LOGGER.info("starting application");

        mainFrame.setVisible(true);

        final SwingWorker<Void, AudioSource> swingWorker = new ReloadPlayListSwingWorker();
        ApplicationContext.getExecutorService().execute(swingWorker);
    }

    private static void initMenu(final JFrame frame) {
        final JMenuItem jMenuItemFileExit = new JMenuItem("Exit");
        jMenuItemFileExit.addActionListener(event -> stop());

        final JMenu jMenuFile = new JMenu("File");
        jMenuFile.add(jMenuItemFileExit);

        final JMenuItem jMenuItemToolsLibrary = new JMenuItem("Library");
        jMenuItemToolsLibrary.addActionListener(event -> {
            final JDialog jDialog = new JDialog(getMainFrame(), "Library", true);
            jDialog.setContentPane(new LibraryView().getComponent());
            jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            jDialog.pack();
            jDialog.setLocationRelativeTo(null);
            jDialog.setVisible(true);
        });

        final JMenuItem jMenuItemToolsPlayList = new JMenuItem("PlayList");
        jMenuItemToolsPlayList.addActionListener(event -> {
            final JDialog jDialog = new JDialog(getMainFrame(), "PlayList", true);
            jDialog.setContentPane(new PlayListView().getComponent());
            jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            jDialog.pack();
            jDialog.setLocationRelativeTo(null);
            jDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(final WindowEvent e) {
                    final SwingWorker<Void, AudioSource> swingWorker = new ReloadPlayListSwingWorker();
                    ApplicationContext.getExecutorService().execute(swingWorker);
                }
            });
            jDialog.setVisible(true);
        });

        final JMenuItem jMenuItemToolsEqualizer = new JMenuItem("Equalizer");
        jMenuItemToolsEqualizer.addActionListener(event -> {
            final JDialog jDialog = new JDialog(getMainFrame(), "Equalizer", true);
            final JPanel jPanel = new JPanel();
            jPanel.add(new JLabel("TODO"));
            jDialog.setContentPane(jPanel);
            jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            jDialog.setSize(500, 200);
            jDialog.setLocationRelativeTo(null);
            jDialog.setVisible(true);
        });

        final JMenu jMenuTools = new JMenu("Tools");
        jMenuTools.add(jMenuItemToolsLibrary);
        jMenuTools.add(jMenuItemToolsPlayList);
        jMenuTools.add(jMenuItemToolsEqualizer);

        final JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.add(jMenuFile);
        jMenuBar.add(jMenuTools);
        frame.setJMenuBar(jMenuBar);
    }

    private static void stop() {
        LOGGER.info("stopping application");

        ApplicationContext.stop();

        mainFrame = null;

        System.exit(0);
    }

    private PlayerApplication() {
        super();
    }
}
