// Created: 28.12.22
package de.freese.mediathek.kodi.swing.controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.swing.KodiSwingClient;
import de.freese.mediathek.kodi.swing.components.GenreDialog;
import de.freese.mediathek.kodi.swing.service.AbstractShowAndMovieService;
import de.freese.mediathek.kodi.swing.view.AbstractShowAndMovieView;

/**
 * @author Thomas Freese
 */
public abstract class AbstractShowAndMovieController<T> extends AbstractController {
    protected AbstractShowAndMovieController(AbstractShowAndMovieService<T> service, AbstractShowAndMovieView<T> view) {
        super(service, view);
    }

    public void clear() {
        getView().getImageLabel().setIcon(null);
        getView().getGenreLabel().setText(null);
        getView().getIdLabel().setText(null);
    }

    public void openGenreDialog() {
        T entity = getView().getSelected();

        SwingWorker<List<List<Genre>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<List<Genre>> doInBackground() throws Exception {
                List<Genre> allGenres = getService().getAllGenres();
                List<Genre> entityGenres = getService().getEntityGenres(entity);

                List<List<Genre>> result = new ArrayList<>();
                result.add(allGenres);
                result.add(entityGenres);

                return result;
            }

            @Override
            protected void done() {
                try {
                    List<List<Genre>> result = get();

                    GenreDialog dialog = new GenreDialog(KodiSwingClient.getFrame());
                    dialog.open(result.get(0), result.get(1));

                    if (dialog.hasBeenCanceled()) {
                        return;
                    }

                    List<Genre> selected = dialog.getSelectedGenres();
                    int[] newGenreIDs = new int[selected.size()];

                    for (int i = 0; i < newGenreIDs.length; i++) {
                        newGenreIDs[i] = selected.get(i).getPk();
                    }

                    getService().updateEntityGenres(entity, newGenreIDs);
                    setSelected(entity);
                }
                catch (Exception ex) {
                    getLogger().error(ex.getMessage(), ex);
                }
            }
        };
        worker.execute();
    }

    public void reload() {
        getView().clear();

        List<T> data = getService().load();
        getView().fill(data);
    }

    public abstract void setSelected(T entity);

    @Override
    protected AbstractShowAndMovieService<T> getService() {
        return (AbstractShowAndMovieService<T>) super.getService();
    }

    @Override
    protected AbstractShowAndMovieView<T> getView() {
        return (AbstractShowAndMovieView<T>) super.getView();
    }

    protected void setImageIcon(T entity) {
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                return getService().loadImageIcon(entity);
            }

            @Override
            protected void done() {
                try {
                    getView().getImageLabel().setIcon(get());
                }
                catch (Exception ex) {
                    getLogger().error("No valid url: {}", ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}