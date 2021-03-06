/**
 * Created on 15.04.2015 17:20:03
 */
package de.freese.mediathek.kodi.javafx.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import org.springframework.context.ApplicationContext;
import de.freese.mediathek.kodi.javafx.KODIJavaFXClient;
import de.freese.mediathek.kodi.javafx.pane.GenrePane;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.IModel;
import de.freese.mediathek.kodi.model.Movie;
import de.freese.mediathek.kodi.model.Show;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableView.TableViewSelectionModel;

/**
 * Controller für die Genre-Übersicht.
 *
 * @author Thomas Freese
 */
public class GenreController extends AbstractController<Genre>
{
    /**
     *
     */
    private final GenrePane scene;

    /**
     * Erstellt ein neues Object.
     *
     * @param applicationContext {@link ApplicationContext}
     * @param resourceBundle {@link ResourceBundle}
     */
    public GenreController(final ApplicationContext applicationContext, final ResourceBundle resourceBundle)
    {
        super(applicationContext);

        this.scene = new GenrePane(resourceBundle);

        initialize(null, resourceBundle);
    }

    /**
     * @return {@link GenrePane}
     */
    public GenrePane getPane()
    {
        return this.scene;
    }

    /**
     * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources)
    {
        ObservableList<Genre> dataList = getPane().getTableItems();
        TableViewSelectionModel<Genre> selectionModel = getPane().getTableSelectionModel();

        final Button buttonReload = this.scene.getButtonReload();
        buttonReload.setOnAction(event -> handleReload(dataList, selectionModel));

        selectionModel.selectedItemProperty().addListener(this);
    }

    /**
     * @see de.freese.mediathek.kodi.javafx.controller.AbstractController#load()
     */
    @Override
    protected List<Genre> load()
    {
        final List<Genre> genres = getMediaService().getGenres();

        return genres;
    }

    /**
     * @see de.freese.mediathek.kodi.javafx.controller.AbstractController#updateDetails(de.freese.mediathek.kodi.model.IModel)
     */
    @Override
    protected void updateDetails(final Genre value)
    {
        getPane().getFilmeItems().clear();
        getPane().getSerienItems().clear();

        final Task<List<IModel>[]> task = new Task<>()
        {
            /**
             * @see javafx.concurrent.Task#call()
             */
            @SuppressWarnings("unchecked")
            @Override
            protected List<IModel>[] call() throws Exception
            {
                final List<Movie> movies = getMediaService().getGenreMovies(value.getPK());
                final List<Show> shows = getMediaService().getGenreShows(value.getPK());

                return (List<IModel>[]) new List<?>[]
                {
                        movies, shows
                };
            }
        };
        task.setOnSucceeded(event -> {
            final List<? extends IModel>[] media = task.getValue();
            getPane().getFilmeItems().addAll(media[0]);
            getPane().getSerienItems().addAll(media[1]);

        });
        task.setOnFailed(event -> {
            KODIJavaFXClient.LOGGER.info("failed");

            Alert alert = new Alert(AlertType.ERROR, task.getException().getMessage());
            alert.showAndWait();
        });

        getExecutor().execute(task);
    }
}
