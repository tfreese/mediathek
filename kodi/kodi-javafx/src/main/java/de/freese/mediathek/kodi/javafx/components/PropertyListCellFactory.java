/**
 * Created: 12.04.2015
 */

package de.freese.mediathek.kodi.javafx.components;

import java.lang.reflect.Method;
import org.springframework.util.ReflectionUtils;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * @author Thomas Freese
 * @param <T> Entity
 */
public class PropertyListCellFactory<T> implements Callback<ListView<T>, ListCell<T>>
{
    /**
     * 
     */
    private final Method method;

    /**
     * Erstellt ein neues {@link PropertyListCellFactory} Object.
     * 
     * @param clazz Class
     * @param methodName String
     */
    public PropertyListCellFactory(final Class<T> clazz, final String methodName)
    {
        super();

        // Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        this.method = ReflectionUtils.findMethod(clazz, methodName);
    }

    /**
     * @see javafx.util.Callback#call(java.lang.Object)
     */
    @Override
    public ListCell<T> call(final ListView<T> param)
    {
        return new ListCell<>()
        {
            /**
             * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
             */
            @Override
            protected void updateItem(final T entity, final boolean empty)
            {
                super.updateItem(entity, empty);

                if (entity != null)
                {
                    setText(ReflectionUtils.invokeMethod(PropertyListCellFactory.this.method, entity).toString());
                }
                else
                {
                    setText(null);
                }
            }
        };
    }
}
