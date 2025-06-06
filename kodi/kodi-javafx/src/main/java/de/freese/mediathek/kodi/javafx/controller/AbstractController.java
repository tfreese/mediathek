// Created: 12.04.2015
package de.freese.mediathek.kodi.javafx.controller;

import java.util.List;
import java.util.concurrent.Executor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView.TableViewSelectionModel;

import org.springframework.context.ApplicationContext;

import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.model.Model;
import de.freese.mediathek.utils.cache.ResourceCache;

/**
 * @author Thomas Freese
 */
public abstract class AbstractController<T extends Model> implements Initializable, ChangeListener<T> {
    private final ApplicationContext applicationContext;
    private final Executor executor;
    private final MediaService mediaService;
    private final ResourceCache resourceCache;

    protected AbstractController(final ApplicationContext applicationContext) {
        super();

        this.applicationContext = applicationContext;
        this.executor = applicationContext.getBean(Executor.class);
        this.mediaService = applicationContext.getBean(MediaService.class);
        this.resourceCache = applicationContext.getBean(ResourceCache.class);
    }

    @Override
    public void changed(final ObservableValue<? extends T> observable, final T oldValue, final T newValue) {
        // System.out.printf("%s, %s, %s%n", observable, oldValue, newValue);

        updateDetails(newValue);
    }

    public ResourceCache getResourceCache() {
        return resourceCache;
    }

    protected <B> B getBean(final Class<B> clazz) {
        return applicationContext.getBean(clazz);
    }

    protected Executor getExecutor() {
        return executor;
    }

    protected MediaService getMediaService() {
        return mediaService;
    }

    protected void handleReload(final ObservableList<T> dataList, final TableViewSelectionModel<T> selectionModel) {
        dataList.clear();

        final Task<List<T>> task = new Task<>() {
            @Override
            protected List<T> call() throws Exception {
                return load();
            }
        };
        task.setOnSucceeded(event -> {
            final List<T> media = task.getValue();
            dataList.addAll(media);
            // selectionModel.select(0);
            selectionModel.selectFirst();
        });

        getExecutor().execute(task);
    }

    protected abstract List<T> load();

    protected abstract void updateDetails(T value);
}
