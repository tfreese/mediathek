// Created: 01.06.2016
package de.freese.mediathek.kodi.javafx.pane;

import java.util.ResourceBundle;

import de.freese.mediathek.kodi.model.Model;
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

/**
 * @author Thomas Freese
 */
public class TvShowMoviePane<T extends Model> extends VBox
{
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

    public TvShowMoviePane(final ResourceBundle resourceBundle)
    {
        super();

        getStyleClass().addAll("vbox", "padding");

        this.buttonReload = new Button(resourceBundle.getString("reload"));
        this.buttonReload.setPrefWidth(Double.MAX_VALUE);
        getChildren().add(this.buttonReload);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.4D);
        splitPane.setFocusTraversable(true);
        getChildren().add(splitPane);

        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("gridpane");

        // FilterLabel
        gridPane.add(new Label(resourceBundle.getString("filter") + ":"), 0, 1);

        // FilterTextField
        TextField textField = new TextField();
        gridPane.add(textField, 1, 1);
        GridPane.setHgrow(textField, Priority.ALWAYS);

        this.tableView = createTableView(textField.textProperty(), resourceBundle);
        this.tableView.setPrefHeight(10000D);
        gridPane.add(this.tableView, 0, 2, 2, 1);
        // GridPane.setVgrow(this.tableView, Priority.ALWAYS);
        splitPane.getItems().add(gridPane);

        // Details
        gridPane = new GridPane();
        gridPane.getStyleClass().addAll("gridpane", "padding");
        this.imageView = new ImageView();
        this.imageView.setPreserveRatio(true);
        gridPane.add(this.imageView, 0, 0, 2, 1);

        // Genres
        gridPane.add(new Label(resourceBundle.getString("genres") + ":"), 0, 1);
        this.labelGenres = new Label();
        gridPane.add(this.labelGenres, 1, 1);

        // IMDB-IDs
        gridPane.add(new Label(resourceBundle.getString("id") + ":"), 0, 2);
        this.labelId = new Label();
        gridPane.add(this.labelId, 1, 2);

        TitledPane titledPane = new TitledPane(resourceBundle.getString("details"), gridPane);
        // titledPane.setPrefHeight(10000D);

        VBox vBox = new VBox();
        vBox.getStyleClass().addAll("vbox");
        vBox.getChildren().add(titledPane);

        this.buttonEditGenres = new Button(resourceBundle.getString("edit_genres"));
        VBox.setMargin(this.buttonEditGenres, new Insets(5D, 5D, 5D, 5D));
        this.buttonEditGenres.disableProperty().bind(this.tableView.getSelectionModel().selectedItemProperty().isNull());
        vBox.getChildren().add(this.buttonEditGenres);

        splitPane.getItems().add(vBox);
    }

    public Button getButtonEditGenres()
    {
        return this.buttonEditGenres;
    }

    public Button getButtonReload()
    {
        return this.buttonReload;
    }

    public StringProperty getGenresProperty()
    {
        return this.labelGenres.textProperty();
    }

    public StringProperty getIdProperty()
    {
        return this.labelId.textProperty();
    }

    public ObjectProperty<Image> getImageProperty()
    {
        return this.imageView.imageProperty();
    }

    public ObservableList<T> getTableItems()
    {
        // return this.tableView.getItems();
        return this.tableList;
    }

    public TableViewSelectionModel<T> getTableSelectionModel()
    {
        return this.tableView.getSelectionModel();
    }

    @SuppressWarnings("unchecked")
    private TableView<T> createTableView(final StringProperty propertyItemFilter, final ResourceBundle resourceBundle)
    {
        TableView<T> tableView = new TableView<>();
        tableView.setEditable(false);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Tabellen alignment über CellStyle
        TableColumn<T, Integer> columnID = new TableColumn<>(resourceBundle.getString("id"));
        columnID.setResizable(false);
        columnID.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1D)); // 10 % Breite
        columnID.setCellValueFactory(new PropertyValueFactory<>("pk"));
        columnID.setStyle("-fx-alignment: CENTER-RIGHT;");

        // Sortierung auf Name-Spalte
        TableColumn<T, String> columnName = new TableColumn<>(resourceBundle.getString("name"));
        columnName.prefWidthProperty().bind(tableView.widthProperty().multiply(0.9D)); // 90 % Breite
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        // columnName.setSortType(TableColumn.SortType.ASCENDING);

        tableView.getColumns().addAll(columnID, columnName);

        // Aller verfügbarer Platz für Genre-Spalte, Rest hat feste Breite
        // columnName.prefWidthProperty().bind(tableView.widthProperty().subtract(columnID.getMaxWidth() + 16D));

        // Für Filter
        FilteredList<T> filteredData = new FilteredList<>(this.tableList, p -> true);

        // Filter-Textfeld mit FilteredList verbinden.
        propertyItemFilter.addListener((observable, oldValue, newValue) -> filteredData.setPredicate(value ->
        {
            if (newValue == null || newValue.isBlank())
            {
                return true;
            }

            String text = value.getName();

            return text.toLowerCase().contains(newValue.toLowerCase());
        }));

        // Da die ObservableList der TableItems neu gesetzt wird, muss auch die Sortierung neu gemacht werden.
        SortedList<T> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);

        return tableView;
    }
}
