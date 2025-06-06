// Created: 12.04.2015
package de.freese.mediathek.kodi.javafx.components;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author Thomas Freese
 */
public class PickList<T extends Comparable<? super T>> extends HBox {
    private final ListView<T> listViewLeft;
    private final ListView<T> listViewRight;

    public PickList() {
        super();

        getStylesheets().add("styles/styles.css");
        getStyleClass().addAll("hbox", "padding");
        setAlignment(Pos.CENTER);

        listViewLeft = new ListView<>();
        listViewRight = new ListView<>();

        final Button buttonLeftToRight = new Button(">");
        final Button buttonRightToLeft = new Button("<");

        final VBox vBox = new VBox(100);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(buttonLeftToRight, buttonRightToLeft);

        getChildren().add(listViewLeft);
        getChildren().add(vBox);
        getChildren().add(listViewRight);

        buttonLeftToRight.disableProperty().bind(listViewLeft.getSelectionModel().selectedItemProperty().isNull());
        buttonLeftToRight.setOnAction(event -> {
            final ObservableList<T> listRight = PickList.this.listViewRight.getItems();

            final T item = PickList.this.listViewLeft.getSelectionModel().getSelectedItem();

            if (!listRight.contains(item)) {
                listRight.add(item);
            }

            listRight.sort(Comparable::compareTo);
        });

        buttonRightToLeft.disableProperty().bind(listViewRight.getSelectionModel().selectedItemProperty().isNull());
        buttonRightToLeft.setOnAction(event -> {
            final T item = PickList.this.listViewRight.getSelectionModel().getSelectedItem();
            PickList.this.listViewRight.getItems().remove(item);
        });
    }

    public ListView<T> getListViewLeft() {
        return listViewLeft;
    }

    public ListView<T> getListViewRight() {
        return listViewRight;
    }
}
