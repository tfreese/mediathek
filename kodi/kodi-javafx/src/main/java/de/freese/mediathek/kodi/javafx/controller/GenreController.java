// Created: 15.04.2015
package de.freese.mediathek.kodi.javafx.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableView.TableViewSelectionModel;

import org.springframework.context.ApplicationContext;

import de.freese.mediathek.kodi.javafx.KodiJavaFxClient;
import de.freese.mediathek.kodi.javafx.pane.GenrePane;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Model;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;

/**
 * @author Thomas Freese
 */
public class GenreController extends AbstractController<Genre> {
    private final GenrePane scene;

    public GenreController(final ApplicationContext applicationContext, final ResourceBundle resourceBundle) {
        super(applicationContext);

        this.scene = new GenrePane(resourceBundle);

        initialize(null, resourceBundle);
    }

    public GenrePane getPane() {
        return this.scene;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final ObservableList<Genre> dataList = getPane().getTableItems();
        final TableViewSelectionModel<Genre> selectionModel = getPane().getTableSelectionModel();

        final Button buttonReload = this.scene.getButtonReload();
        buttonReload.setOnAction(event -> handleReload(dataList, selectionModel));

        selectionModel.selectedItemProperty().addListener(this);
    }

    @Override
    protected List<Genre> load() {
        return getMediaService().getGenres();
    }

    @Override
    protected void updateDetails(final Genre value) {
        getPane().getFilmeItems().clear();
        getPane().getSerienItems().clear();

        final Task<List<Model>[]> task = new Task<>() {
            @SuppressWarnings("unchecked")
            @Override
            protected List<Model>[] call() throws Exception {
                final List<Movie> movies = getMediaService().getGenreMovies(value.getPk());
                final List<Show> shows = getMediaService().getGenreShows(value.getPk());

                return (List<Model>[]) new List<?>[]{movies, shows};
            }
        };
        task.setOnSucceeded(event -> {
            final List<? extends Model>[] media = task.getValue();
            getPane().getFilmeItems().addAll(media[0]);
            getPane().getSerienItems().addAll(media[1]);

        });
        task.setOnFailed(event -> {
            KodiJavaFxClient.LOGGER.info("failed");

            final Alert alert = new Alert(AlertType.ERROR, task.getException().getMessage());
            alert.showAndWait();
        });

        getExecutor().execute(task);
    }
}
