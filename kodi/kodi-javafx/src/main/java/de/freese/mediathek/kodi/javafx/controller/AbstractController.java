/**
 * Created on 12.04.2015 11:18:39
 */
package de.freese.mediathek.kodi.javafx.controller;

import java.util.List;
import java.util.concurrent.Executor;
import org.springframework.context.ApplicationContext;
import de.freese.base.core.cache.ResourceCache;
import de.freese.base.core.cache.FileResourceCache;
import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.model.IModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;

/**
 * @param <T> Entity
 * @author Thomas Freese
 */
public abstract class AbstractController<T extends IModel> implements Initializable, ChangeListener<T>
{
    /**
     *
     */
    private final ApplicationContext applicationContext;

    /**
     *
     */
    private final ResourceCache resourceCache;

    /**
     *
     */
    private final Executor executor;

    /**
     *
     */
    private final MediaService mediaService;

    /**
     * Erstellt ein neues Object.
     *
     * @param applicationContext {@link ApplicationContext}
     */
    public AbstractController(final ApplicationContext applicationContext)
    {
        super();

        this.applicationContext = applicationContext;
        this.executor = applicationContext.getBean(Executor.class);
        this.mediaService = applicationContext.getBean(MediaService.class);
        this.resourceCache = new FileResourceCache();
    }

    /**
     * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
     */
    @Override
    public void changed(final ObservableValue<? extends T> observable, final T oldValue, final T newValue)
    {
        // System.out.printf("%s, %s, %s%n", observable, oldValue, newValue);

        updateDetails(newValue);
    }

    /**
     * @param <B> Bean
     * @param clazz Class
     * @return Object
     */
    protected <B> B getBean(final Class<B> clazz)
    {
        return this.applicationContext.getBean(clazz);
    }

    /**
     * @return {@link ResourceCache}
     */
    protected ResourceCache getCache()
    {
        return this.resourceCache;
    }

    /**
     * @return {@link Executor}
     */
    protected Executor getExecutor()
    {
        return this.executor;
    }

    /**
     * @return {@link MediaService}
     */
    protected MediaService getMediaService()
    {
        return this.mediaService;
    }

    /**
     * Reload der Daten.
     *
     * @param dataList {@link ObservableList}
     * @param selectionModel {@link TableViewSelectionModel}
     */
    protected void handleReload(final ObservableList<T> dataList, final TableViewSelectionModel<T> selectionModel)
    {
        dataList.clear();

        final Task<List<T>> task = new Task<>()
        {
            /**
             * @see javafx.concurrent.Task#call()
             */
            @Override
            protected List<T> call() throws Exception
            {
                return load();
            }
        };
        task.setOnSucceeded(event -> {
            List<T> media = task.getValue();
            dataList.addAll(media);
            // selectionModel.select(0);
            selectionModel.selectFirst();
        });

        getExecutor().execute(task);
    }

    /**
     * @return {@link TableView}
     */
    protected abstract List<T> load();

    /**
     * @param value Object
     */
    protected abstract void updateDetails(final T value);
}
