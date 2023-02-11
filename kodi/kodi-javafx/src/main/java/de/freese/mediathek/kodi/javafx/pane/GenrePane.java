// Created: 15.04.2015
package de.freese.mediathek.kodi.javafx.pane;

import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import de.freese.mediathek.kodi.javafx.components.ModelListCellFactory;
import de.freese.mediathek.kodi.model.Genre;
import de.freese.mediathek.kodi.model.Model;

/**
 * @author Thomas Freese
 */
public class GenrePane extends VBox {
    private final Button buttonReload;

    private final ListView<Model> listViewFilme;

    private final ListView<Model> listViewSerien;

    private final TableView<Genre> tableViewGenres;

    public GenrePane(final ResourceBundle resourceBundle) {
        super();

        getStyleClass().addAll("vbox", "padding");

        this.buttonReload = new Button(resourceBundle.getString("reload"));
        this.buttonReload.setMaxWidth(Double.MAX_VALUE);
        getChildren().add(this.buttonReload);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.4D);
        splitPane.setFocusTraversable(true);
        getChildren().add(splitPane);

        this.tableViewGenres = createTableViewGenres(resourceBundle);
        splitPane.getItems().add(this.tableViewGenres);

        HBox hBox = new HBox();

        this.listViewFilme = new ListView<>();
        this.listViewFilme.setEditable(false);
        this.listViewFilme.setCellFactory(new ModelListCellFactory());
        this.listViewFilme.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TitledPane titledPane = new TitledPane(resourceBundle.getString("filme"), this.listViewFilme);
        titledPane.setPrefHeight(10000D);
        HBox.setHgrow(titledPane, Priority.ALWAYS);
        hBox.getChildren().add(titledPane);

        this.listViewSerien = new ListView<>();
        this.listViewSerien.setEditable(false);
        this.listViewSerien.setCellFactory(new ModelListCellFactory());
        this.listViewSerien.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        titledPane = new TitledPane(resourceBundle.getString("serien"), this.listViewSerien);
        titledPane.setPrefHeight(10000D);
        HBox.setHgrow(titledPane, Priority.ALWAYS);
        hBox.getChildren().add(titledPane);

        splitPane.getItems().add(hBox);
    }

    public Button getButtonReload() {
        return this.buttonReload;
    }

    public ObservableList<Model> getFilmeItems() {
        return this.listViewFilme.getItems();
    }

    public ObservableList<Model> getSerienItems() {
        return this.listViewSerien.getItems();
    }

    public ObservableList<Genre> getTableItems() {
        return this.tableViewGenres.getItems();
    }

    public TableViewSelectionModel<Genre> getTableSelectionModel() {
        return this.tableViewGenres.getSelectionModel();
    }

    private TableView<Genre> createTableViewGenres(final ResourceBundle resourceBundle) {
        TableView<Genre> tableView = new TableView<>();
        tableView.setEditable(false);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Tabellen alignment über CellStyle
        TableColumn<Genre, Integer> columnID = new TableColumn<>(resourceBundle.getString("id"));
        columnID.setResizable(false);
        columnID.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1D)); // 10 % Breite
        columnID.setCellValueFactory(new PropertyValueFactory<>("pk"));
        // columnID.setCellValueFactory(cell -> cell.getValue().getPk());
        columnID.setStyle("-fx-alignment: CENTER-RIGHT;");

        // Sortierung auf Genre Spalte
        TableColumn<Genre, String> columnGenre = new TableColumn<>(resourceBundle.getString("genre"));
        columnGenre.prefWidthProperty().bind(tableView.widthProperty().multiply(0.58D)); // 58 % Breite
        columnGenre.setCellValueFactory(new PropertyValueFactory<>("name"));
        // columnGenre.setCellValueFactory(cell -> cell.getValue().getName());
        // columnGenre.setSortType(TableColumn.SortType.ASCENDING);

        TableColumn<Genre, Integer> columnFilme = new TableColumn<>(resourceBundle.getString("filme"));
        columnFilme.setResizable(false);
        columnFilme.prefWidthProperty().bind(tableView.widthProperty().multiply(0.16D)); // 16 % Breite
        columnFilme.setCellValueFactory(new PropertyValueFactory<>("anzahlFilme"));
        // columnFilme.setCellValueFactory(cell -> cell.getValue().getAnzahlFilme());
        columnFilme.setStyle("-fx-alignment: CENTER-RIGHT;");

        // Alignment über CellFactory
        TableColumn<Genre, String> columnSerien = new TableColumn<>(resourceBundle.getString("serien"));
        columnSerien.setResizable(false);
        columnSerien.prefWidthProperty().bind(tableView.widthProperty().multiply(0.16D)); // 16 % Breite
        columnSerien.setCellValueFactory(new PropertyValueFactory<>("anzahlSerien"));
        // columnSerien.setCellValueFactory(cell -> cell.getValue().getAnzahlSerien());
        columnSerien.setStyle("-fx-alignment: CENTER-RIGHT;");
        // FormattedTableCellFactory<Genre, String> cellFactorySerien = new FormattedTableCellFactory<>();
        // cellFactorySerien.setAlignment(TextAlignment.RIGHT);
        // columnSerien.setCellFactory(cellFactorySerien);
        // columnSerien.setCellFactory(TextFieldTableCell.forTableColumn());
        // columnSerien.setOnEditCommit(
        // new EventHandler<CellEditEvent<Person, String>>()
        // {
        // @Override
        // public void handle(CellEditEvent<Person, String> t)
        // {
        // ((Person) t.getTableView().getItems().get(
        // t.getTablePosition().getRow())).setFirstName(t.getNewValue());
        // }
        // }
        // );

        tableView.getColumns().add(columnID);
        tableView.getColumns().add(columnGenre);
        tableView.getColumns().add(columnFilme);
        tableView.getColumns().add(columnSerien);

        // Aller verfügbarer Platz für Genre-Spalte, Rest hat feste Breite.
        // columnGenre.prefWidthProperty()
        // .bind(tableView.widthProperty().subtract(columnID.getMaxWidth() + columnFilme.getMinWidth() + columnSerien.getMinWidth() + 16D));

        return tableView;
    }
}
