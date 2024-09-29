// Created: 29 Sept. 2024
package de.freese.player.ui.swing.component.playlist;

import java.util.List;

import javax.swing.SwingWorker;

import de.freese.player.core.input.AudioSource;
import de.freese.player.ui.ApplicationContext;
import de.freese.player.ui.model.PlayList;

/**
 * @author Thomas Freese
 */
public class ReloadPlayListSwingWorker extends SwingWorker<Void, AudioSource> {
    public ReloadPlayListSwingWorker() {
        super();

        ApplicationContext.getSongCollection().clear();
    }

    @Override
    protected Void doInBackground() throws Exception {
        final PlayList currentPlayList = ApplicationContext.getRepository().getCurrentPlayList();
        ApplicationContext.getRepository().getSongs(currentPlayList, this::publish);

        return null;
    }

    @Override
    protected void process(final List<AudioSource> chunks) {
        ApplicationContext.getSongCollection().addAudioSources(chunks);
    }
}
