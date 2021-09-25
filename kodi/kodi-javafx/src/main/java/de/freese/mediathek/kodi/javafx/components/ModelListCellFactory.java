// Created: 12.04.2015
package de.freese.mediathek.kodi.javafx.components;

import de.freese.mediathek.kodi.model.IModel;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * @author Thomas Freese
 */
public class ModelListCellFactory implements Callback<ListView<IModel>, ListCell<IModel>>
{
    /**
     * @see javafx.util.Callback#call(java.lang.Object)
     */
    @Override
    public ListCell<IModel> call(final ListView<IModel> param)
    {
        ListCell<IModel> cell = new ListCell<>()
        {
            /**
             * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
             */
            @Override
            protected void updateItem(final IModel model, final boolean empty)
            {
                super.updateItem(model, empty);

                if (model != null)
                {
                    setText(String.format("%s (%d)", model.getName(), model.getPK()));
                }
                else
                {
                    setText(null);
                }
            }
        };

        // cell.getStylesheets().add("styles/Styles.css");
        return cell;
    }
}
