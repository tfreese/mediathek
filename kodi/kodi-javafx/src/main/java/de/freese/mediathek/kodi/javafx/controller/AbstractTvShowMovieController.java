// Created: 12.04.2015
package de.freese.mediathek.kodi.javafx.controller;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.image.Image;

import org.springframework.context.ApplicationContext;

import de.freese.mediathek.kodi.javafx.KodiJavaFxClient;
import de.freese.mediathek.kodi.javafx.components.ModelListCellFactory;
import de.freese.mediathek.kodi.javafx.components.PickList;
import de.freese.mediathek.kodi.javafx.pane.TvShowMoviePane;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Model;

/**
 * @author Thomas Freese
 */
public abstract class AbstractTvShowMovieController<T extends Model> extends AbstractController<T> {
    private final TvShowMoviePane<T> pane;

    protected AbstractTvShowMovieController(final ApplicationContext applicationContext, final ResourceBundle resourceBundle) {
        super(applicationContext);

        this.pane = new TvShowMoviePane<>(resourceBundle);
    }

    public TvShowMoviePane<T> getPane() {
        return this.pane;
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        final ObservableList<T> dataList = getPane().getTableItems();
        final TableViewSelectionModel<T> selectionModel = getPane().getTableSelectionModel();

        final ReadOnlyObjectProperty<T> selectedItemProperty = selectionModel.selectedItemProperty();
        selectedItemProperty.addListener(this);

        // getPane().getGenresProperty().bind(Bindings.when(selectedItemProperty.isNull()).then("").otherwise(Bindings.selectString(selectedItemProperty,
        // "genres")));

        getPane().getButtonReload().setOnAction(event -> handleReload(dataList, selectionModel));
        getPane().getButtonEditGenres().setOnAction(event -> updateGenres(selectionModel));
    }

    protected abstract List<Genre> getGenres(T value);

    protected abstract String getImageUrl(T value);

    @Override
    protected void updateDetails(final T value) {
        getPane().getImageProperty().set(null);

        if (value == null) {
            return;
        }

        final Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                String url = getImageUrl(value);

                if (url != null) {
                    if (url.contains("\"")) {
                        url = url.substring(0, url.indexOf('"'));
                    }

                    final URI uri = URI.create(url);

                    try (InputStream inputStream = getResourceCache().getResource(uri)) {
                        return new Image(inputStream, 1024, 768, true, true);
                    }
                }

                return null;
            }
        };
        task.setOnSucceeded(event -> {
            final Image image = task.getValue();
            getPane().getImageProperty().set(image);

        });
        // Alert alert = new Alert(AlertType.ERROR, task.getException().getMessage());
        // alert.showAndWait();
        task.setOnFailed(event -> KodiJavaFxClient.LOGGER.error(task.getException().getMessage()));

        // task.run();
        getExecutor().execute(task);
    }

    protected abstract void updateGenres(T value, int[] genreIDs);

    protected void updateGenres(final TableViewSelectionModel<T> selectionModel) {
        final T model = selectionModel.getSelectedItem();

        final PickList<Model> pickList = new PickList<>();
        pickList.getListViewLeft().getItems().addAll(getMediaService().getGenres());
        pickList.getListViewRight().getItems().addAll(getGenres(model));

        // pickList.getListViewLeft().setCellFactory(new PropertyListCellFactory<Genre>(Genre.class, "getName"));
        pickList.getListViewLeft().setCellFactory(new ModelListCellFactory());
        pickList.getListViewRight().setCellFactory(new ModelListCellFactory());

        final ChoiceDialog<List<Model>> dialog = new ChoiceDialog<>();
        dialog.setTitle("Genre Editor");
        dialog.setHeaderText(model.getName());
        dialog.setGraphic(null);
        dialog.getDialogPane().setContent(pickList);
        dialog.setResultConverter(type -> {
            if (ButtonType.OK.equals(type)) {
                return pickList.getListViewRight().getItems();
            }

            return null;
        });

        final Optional<List<Model>> result = dialog.showAndWait();

        if (result.isPresent()) {
            final List<Model> genres = result.get();
            final int[] genreIDs = new int[genres.size()];

            for (int i = 0; i < genreIDs.length; i++) {
                genreIDs[i] = genres.get(i).getPk();
            }

            updateGenres(model, genreIDs);
        }
    }
}
