// Created: 12.04.2015
package de.freese.mediathek.kodi.javafx.components;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import de.freese.mediathek.kodi.model.Model;

/**
 * @author Thomas Freese
 */
public class ModelListCellFactory implements Callback<ListView<Model>, ListCell<Model>> {
    @Override
    public ListCell<Model> call(final ListView<Model> param) {
        final ListCell<Model> cell = new ListCell<>() {
            @Override
            protected void updateItem(final Model model, final boolean empty) {
                super.updateItem(model, empty);

                if (model != null) {
                    setText(String.format("%s (%d)", model.getName(), model.getPk()));
                }
                else {
                    setText(null);
                }
            }
        };

        // cell.getStylesheets().add("styles/Styles.css");
        return cell;
    }
}
