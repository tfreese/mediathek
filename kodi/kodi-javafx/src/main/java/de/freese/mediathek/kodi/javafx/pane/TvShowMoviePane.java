// Created: 01.06.2016
package de.freese.mediathek.kodi.javafx.pane;

import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import de.freese.mediathek.kodi.model.Model;

/**
 * @author Thomas Freese
 */
public class TvShowMoviePane<T extends Model> extends VBox {
    private final Button buttonEditGenres;
    private final Button buttonReload;
    private final ImageView imageView;
    private final Label labelGenres;
    private final Label labelId;
    /**
     * Wird für die Filterung benötigt.
     */
    private final ObservableList<T> tableList = FXCollections.observableArrayList();
    private final TableView<T> tableView;

    public TvShowMoviePane(final ResourceBundle resourceBundle) {
        super();

        getStyleClass().addAll("vbox", "padding");

        buttonReload = new Button(resourceBundle.getString("reload"));
        buttonReload.setPrefWidth(Double.MAX_VALUE);
        getChildren().add(buttonReload);

        final SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.4D);
        splitPane.setFocusTraversable(true);
        getChildren().add(splitPane);

        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("gridpane");

        // FilterLabel
        gridPane.add(new Label(resourceBundle.getString("filter") + ":"), 0, 1);

        // FilterTextField
        final TextField textField = new TextField();
        gridPane.add(textField, 1, 1);
        GridPane.setHgrow(textField, Priority.ALWAYS);

        tableView = createTableView(textField.textProperty(), resourceBundle);
        tableView.setPrefHeight(10000D);

        gridPane.add(tableView, 0, 2, 2, 1);
        // GridPane.setVgrow(tableView, Priority.ALWAYS);
        splitPane.getItems().add(gridPane);

        // Details
        gridPane = new GridPane();
        gridPane.getStyleClass().addAll("gridpane", "padding");
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        gridPane.add(imageView, 0, 0, 2, 1);

        // Genres
        gridPane.add(new Label(resourceBundle.getString("genres") + ":"), 0, 1);
        labelGenres = new Label();
        gridPane.add(labelGenres, 1, 1);

        // IMDB-IDs
        gridPane.add(new Label(resourceBundle.getString("id") + ":"), 0, 2);
        labelId = new Label();
        gridPane.add(labelId, 1, 2);

        final TitledPane titledPane = new TitledPane(resourceBundle.getString("details"), gridPane);
        // titledPane.setPrefHeight(10000D);

        final VBox vBox = new VBox();
        vBox.getStyleClass().addAll("vbox");
        vBox.getChildren().add(titledPane);

        buttonEditGenres = new Button(resourceBundle.getString("edit_genres"));
        VBox.setMargin(buttonEditGenres, new Insets(5D, 5D, 5D, 5D));
        buttonEditGenres.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        vBox.getChildren().add(buttonEditGenres);

        splitPane.getItems().add(vBox);
    }

    public Button getButtonEditGenres() {
        return buttonEditGenres;
    }

    public Button getButtonReload() {
        return buttonReload;
    }

    public StringProperty getGenresProperty() {
        return labelGenres.textProperty();
    }

    public StringProperty getIdProperty() {
        return labelId.textProperty();
    }

    public ObjectProperty<Image> getImageProperty() {
        return imageView.imageProperty();
    }

    public ObservableList<T> getTableItems() {
        // return tableView.getItems();
        return tableList;
    }

    public TableViewSelectionModel<T> getTableSelectionModel() {
        return tableView.getSelectionModel();
    }

    @SuppressWarnings("unchecked")
    private TableView<T> createTableView(final StringProperty propertyItemFilter, final ResourceBundle resourceBundle) {
        final TableView<T> tw = new TableView<>();
        tw.setEditable(false);
        tw.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Tabellen alignment über CellStyle
        final TableColumn<T, Integer> columnID = new TableColumn<>(resourceBundle.getString("id"));
        columnID.setResizable(false);
        columnID.prefWidthProperty().bind(tw.widthProperty().multiply(0.1D)); // 10 % Breite
        columnID.setCellValueFactory(new PropertyValueFactory<>("pk"));
        columnID.setStyle("-fx-alignment: CENTER-RIGHT;");

        // Sortierung auf Name-Spalte
        final TableColumn<T, String> columnName = new TableColumn<>(resourceBundle.getString("name"));
        columnName.prefWidthProperty().bind(tw.widthProperty().multiply(0.9D)); // 90 % Breite
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        // columnName.setSortType(TableColumn.SortType.ASCENDING);

        tw.getColumns().addAll(columnID, columnName);

        // Aller verfügbarer Platz für Genre-Spalte, Rest hat feste Breite
        // columnName.prefWidthProperty().bind(tw.widthProperty().subtract(columnID.getMaxWidth() + 16D));

        // Für Filter
        final FilteredList<T> filteredData = new FilteredList<>(tableList, p -> true);

        // Filter-Textfeld mit FilteredList verbinden.
        propertyItemFilter.addListener((observable, oldValue, newValue) -> filteredData.setPredicate(value -> {
            if (newValue == null || newValue.isBlank()) {
                return true;
            }

            final String text = value.getName();

            return text.toLowerCase().contains(newValue.toLowerCase());
        }));

        // Da die ObservableList der TableItems neu gesetzt wird, muss auch die Sortierung neu gemacht werden.
        final SortedList<T> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tw.comparatorProperty());

        tw.setItems(sortedData);

        return tw;
    }
}
