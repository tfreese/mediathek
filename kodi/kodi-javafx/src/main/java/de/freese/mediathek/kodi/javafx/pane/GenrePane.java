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

        buttonReload = new Button(resourceBundle.getString("reload"));
        buttonReload.setMaxWidth(Double.MAX_VALUE);
        getChildren().add(buttonReload);

        final SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.4D);
        splitPane.setFocusTraversable(true);
        getChildren().add(splitPane);

        tableViewGenres = createTableViewGenres(resourceBundle);
        splitPane.getItems().add(tableViewGenres);

        final HBox hBox = new HBox();

        listViewFilme = new ListView<>();
        listViewFilme.setEditable(false);
        listViewFilme.setCellFactory(new ModelListCellFactory());
        listViewFilme.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TitledPane titledPane = new TitledPane(resourceBundle.getString("filme"), listViewFilme);
        titledPane.setPrefHeight(10000D);
        HBox.setHgrow(titledPane, Priority.ALWAYS);
        hBox.getChildren().add(titledPane);

        listViewSerien = new ListView<>();
        listViewSerien.setEditable(false);
        listViewSerien.setCellFactory(new ModelListCellFactory());
        listViewSerien.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        titledPane = new TitledPane(resourceBundle.getString("serien"), listViewSerien);
        titledPane.setPrefHeight(10000D);
        HBox.setHgrow(titledPane, Priority.ALWAYS);
        hBox.getChildren().add(titledPane);

        splitPane.getItems().add(hBox);
    }

    public Button getButtonReload() {
        return buttonReload;
    }

    public ObservableList<Model> getFilmeItems() {
        return listViewFilme.getItems();
    }

    public ObservableList<Model> getSerienItems() {
        return listViewSerien.getItems();
    }

    public ObservableList<Genre> getTableItems() {
        return tableViewGenres.getItems();
    }

    public TableViewSelectionModel<Genre> getTableSelectionModel() {
        return tableViewGenres.getSelectionModel();
    }

    private TableView<Genre> createTableViewGenres(final ResourceBundle resourceBundle) {
        final TableView<Genre> tableView = new TableView<>();
        tableView.setEditable(false);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Tabellen alignment über CellStyle
        final TableColumn<Genre, Integer> columnID = new TableColumn<>(resourceBundle.getString("id"));
        columnID.setResizable(false);
        columnID.prefWidthProperty().bind(tableView.widthProperty().multiply(0.1D)); // 10 % Breite
        columnID.setCellValueFactory(new PropertyValueFactory<>("pk"));
        // columnID.setCellValueFactory(cell -> cell.getValue().getPk());
        columnID.setStyle("-fx-alignment: CENTER-RIGHT;");

        // Sortierung auf Genre Spalte
        final TableColumn<Genre, String> columnGenre = new TableColumn<>(resourceBundle.getString("genre"));
        columnGenre.prefWidthProperty().bind(tableView.widthProperty().multiply(0.58D)); // 58 % Breite
        columnGenre.setCellValueFactory(new PropertyValueFactory<>("name"));
        // columnGenre.setCellValueFactory(cell -> cell.getValue().getName());
        // columnGenre.setSortType(TableColumn.SortType.ASCENDING);

        final TableColumn<Genre, Integer> columnFilme = new TableColumn<>(resourceBundle.getString("filme"));
        columnFilme.setResizable(false);
        columnFilme.prefWidthProperty().bind(tableView.widthProperty().multiply(0.16D)); // 16 % Breite
        columnFilme.setCellValueFactory(new PropertyValueFactory<>("anzahlFilme"));
        // columnFilme.setCellValueFactory(cell -> cell.getValue().getAnzahlFilme());
        columnFilme.setStyle("-fx-alignment: CENTER-RIGHT;");

        // Alignment über CellFactory
        final TableColumn<Genre, String> columnSerien = new TableColumn<>(resourceBundle.getString("serien"));
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
