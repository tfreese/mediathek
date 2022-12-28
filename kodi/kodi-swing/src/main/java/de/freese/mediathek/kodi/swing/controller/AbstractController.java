// Created: 27.12.22
package de.freese.mediathek.kodi.swing.controller;

import java.util.List;

import javax.swing.SwingWorker;

import de.freese.mediathek.kodi.api.MediaService;
import de.freese.mediathek.kodi.swing.view.AbstractView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author Thomas Freese
 */
public abstract class AbstractController<T, V extends AbstractView<T>>
{
    private final ApplicationContext applicationContext;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private V view;

    protected AbstractController(ApplicationContext applicationContext)
    {
        super();

        this.applicationContext = applicationContext;
    }

    public void init(final V view)
    {
        this.view = view;

        view.doOnReload(button -> button.addActionListener(event -> reload()));
    }

    protected ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    protected Logger getLogger()
    {
        return logger;
    }

    protected MediaService getMediaService()
    {
        return getApplicationContext().getBean(MediaService.class);
    }

    protected V getView()
    {
        return view;
    }

    protected abstract List<T> loadEntities();

    protected abstract void onSelection(T entity);

    protected void reload()
    {
        getView().clear();

        SwingWorker<List<T>, Void> worker = new SwingWorker<>()
        {
            /**
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected List<T> doInBackground() throws Exception
            {
                return loadEntities();
            }

            /**
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done()
            {
                try
                {
                    getView().fill(get());
                }
                catch (Exception ex)
                {
                    getLogger().error(ex.getMessage(), ex);
                }
            }
        };
        worker.execute();
    }
}
