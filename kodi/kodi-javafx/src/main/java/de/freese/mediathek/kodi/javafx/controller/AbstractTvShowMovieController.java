/**
 * Created on 12.04.2015 11:18:39
 */
package de.freese.mediathek.kodi.javafx.controller;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import de.freese.mediathek.kodi.javafx.KODIJavaFXClient;
import de.freese.mediathek.kodi.javafx.components.ModelListCellFactory;
import de.freese.mediathek.kodi.javafx.components.PickList;
import de.freese.mediathek.kodi.javafx.pane.TvShowMoviePane;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.IModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.image.Image;

/**
 * Basis-Controller für die Serien und Filme.
 *
 * @author Thomas Freese
 * @param <T> Entity
 */
public abstract class AbstractTvShowMovieController<T extends IModel> extends AbstractController<T>
{
    /**
     *
     */
    private final TvShowMoviePane<T> pane;

    /**
     * Erstellt ein neues Object.
     *
     * @param applicationContext {@link ApplicationContext}
     * @param resourceBundle {@link ResourceBundle}
     */
    protected AbstractTvShowMovieController(final ApplicationContext applicationContext, final ResourceBundle resourceBundle)
    {
        super(applicationContext);

        this.pane = new TvShowMoviePane<>(resourceBundle);
    }

    /**
     * @param value Entity
     * @return {@link List}
     */
    protected abstract List<Genre> getGenres(T value);

    /**
     * @param value Entity
     * @return String
     */
    protected abstract String getImageURL(T value);

    /**
     * @return {@link TvShowMoviePane}
     */
    public TvShowMoviePane<T> getPane()
    {
        return this.pane;
    }

    /**
     * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
     */
    @Override
    public void initialize(final URL url, final ResourceBundle rb)
    {
        ObservableList<T> dataList = getPane().getTableItems();
        TableViewSelectionModel<T> selectionModel = getPane().getTableSelectionModel();

        ReadOnlyObjectProperty<T> selectedItemProperty = selectionModel.selectedItemProperty();
        selectedItemProperty.addListener(this);

        // getPane().getGenresProperty().bind(Bindings.when(selectedItemProperty.isNull()).then("").otherwise(Bindings.selectString(selectedItemProperty,
        // "genres")));

        getPane().getButtonReload().setOnAction(event -> handleReload(dataList, selectionModel));
        getPane().getButtonEditGenres().setOnAction(event -> updateGenres(selectionModel));
    }

    /**
     * @see de.freese.mediathek.kodi.javafx.controller.AbstractController#updateDetails(de.freese.mediathek.kodi.model.IModel)
     */
    @Override
    protected void updateDetails(final T value)
    {
        getPane().getImageProperty().set(null);

        if (value == null)
        {
            return;
        }

        final Task<Image> task = new Task<>()
        {
            /**
             * @see javafx.concurrent.Task#call()
             */
            @Override
            protected Image call() throws Exception
            {
                String url = getImageURL(value);

                if (StringUtils.isNotBlank(url))
                {
                    Optional<InputStream> optional = getCache().getResource(url);

                    if (optional.isPresent())
                    {
                        try (InputStream inputStream = optional.get())
                        {
                            return new Image(inputStream, 480, 853, true, true);
                        }
                    }
                }

                return null;
            }
        };
        task.setOnSucceeded(event -> {
            Image image = task.getValue();
            getPane().getImageProperty().set(image);

        });
        task.setOnFailed(event -> {
            KODIJavaFXClient.LOGGER.info("failed");

            Alert alert = new Alert(AlertType.ERROR, task.getException().getMessage());
            alert.showAndWait();
        });

        // task.run();
        getExecutor().execute(task);
    }

    /**
     * @param value Entity
     * @param genreIDs int[]
     */
    protected abstract void updateGenres(T value, int[] genreIDs);

    /**
     * Update der Genres.
     *
     * @param selectionModel {@link TableViewSelectionModel}
     */
    protected void updateGenres(final TableViewSelectionModel<T> selectionModel)
    {
        T model = selectionModel.getSelectedItem();

        final PickList<IModel> pickList = new PickList<>();
        pickList.getListViewLeft().getItems().addAll(getMediaService().getGenres());
        pickList.getListViewRight().getItems().addAll(getGenres(model));

        // pickList.getListViewLeft().setCellFactory(new PropertyListCellFactory<Genre>(Genre.class, "getName"));
        pickList.getListViewLeft().setCellFactory(new ModelListCellFactory());
        pickList.getListViewRight().setCellFactory(new ModelListCellFactory());

        ChoiceDialog<List<IModel>> dialog = new ChoiceDialog<>();
        dialog.setTitle("Genre Editor");
        dialog.setHeaderText(model.getName());
        dialog.setGraphic(null);
        dialog.getDialogPane().setContent(pickList);
        dialog.setResultConverter(type -> {
            if (ButtonType.OK.equals(type))
            {
                return pickList.getListViewRight().getItems();
            }

            return null;
        });

        Optional<List<IModel>> result = dialog.showAndWait();

        if (result.isPresent())
        {
            List<IModel> genres = result.get();
            int[] genreIDs = new int[genres.size()];

            for (int i = 0; i < genreIDs.length; i++)
            {
                genreIDs[i] = genres.get(i).getPK();
            }

            updateGenres(model, genreIDs);
        }
    }
}
