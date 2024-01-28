// Created: 12.04.2015
package de.freese.mediathek.kodi.javafx.components;

import java.lang.reflect.Method;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import org.springframework.util.ReflectionUtils;

/**
 * @author Thomas Freese
 */
public class PropertyListCellFactory<T> implements Callback<ListView<T>, ListCell<T>> {
    private final Method method;

    public PropertyListCellFactory(final Class<T> clazz, final String methodName) {
        super();

        // Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        this.method = ReflectionUtils.findMethod(clazz, methodName);
    }

    @Override
    public ListCell<T> call(final ListView<T> param) {
        return new ListCell<>() {
            @Override
            protected void updateItem(final T entity, final boolean empty) {
                super.updateItem(entity, empty);

                if (entity != null) {
                    setText(ReflectionUtils.invokeMethod(PropertyListCellFactory.this.method, entity).toString());
                }
                else {
                    setText(null);
                }
            }
        };
    }
}
