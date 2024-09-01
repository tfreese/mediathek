// Created: 24 Aug. 2024
package de.freese.player.swing.component;

import java.awt.FlowLayout;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import de.freese.player.player.Player;
import de.freese.player.utils.image.ImageFactory;

/**
 * @author Thomas Freese
 */
public final class PlayerControlComponent {
    private static final Icon ICON_PAUSE = ImageFactory.getIcon("images/media-pause-white.svg");
    private static final Icon ICON_PLAY = ImageFactory.getIcon("images/media-play-white.svg");
    private static final Icon ICON_STOP = ImageFactory.getIcon("images/media-stop-white.svg");

    private final JToggleButton buttonPlayPause;
    private final JComponent component;

    public PlayerControlComponent(final Player player) {
        super();

        Objects.requireNonNull(player, "player required");

        buttonPlayPause = new JToggleButton(ICON_PLAY);
        final JButton buttonStop = new JButton(ICON_STOP);

        player.addStopListener(audioSource -> onStop());

        buttonPlayPause.addActionListener(event -> {
            if (buttonPlayPause.isSelected()) {
                buttonPlayPause.setIcon(ICON_PAUSE);

                if (!player.isPlaying()) {
                    player.play();
                }
                else {
                    player.resume();
                }
            }

            if (!buttonPlayPause.isSelected()) {
                buttonPlayPause.setIcon(ICON_PLAY);

                if (player.isPlaying()) {
                    player.pause();
                }
            }
        });

        buttonStop.addActionListener(event -> {
            player.stop();

            buttonPlayPause.setSelected(false);
            buttonPlayPause.setIcon(ICON_PLAY);
        });

        component = new JPanel(new FlowLayout());
        component.add(buttonPlayPause);
        component.add(buttonStop);
    }

    public JComponent getComponent() {
        return component;
    }

    public void onPlay() {
        buttonPlayPause.setSelected(true);
        buttonPlayPause.setIcon(ICON_PAUSE);
    }

    public void onStop() {
        buttonPlayPause.setSelected(false);
        buttonPlayPause.setIcon(ICON_PLAY);
    }
}
