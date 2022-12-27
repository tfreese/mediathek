// Created: 27.12.22
package de.freese.mediathek.kodi.swing.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractView<T>
{
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Consumer<T> consumerOnSelection = entity ->
    {
    };
    private JButton reloadButton;

    protected AbstractView()
    {
        super();
    }

    public abstract void clear();

    public void doOnReload(Consumer<JButton> consumer)
    {
        consumer.accept(this.reloadButton);
    }

    public void doOnSelection(Consumer<T> consumer)
    {
        this.consumerOnSelection = consumer;
    }

    public abstract void fill(List<T> data);

    public abstract T getSelected();

    public Component init()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        this.reloadButton = new JButton("Reload");
        panel.add(this.reloadButton, BorderLayout.NORTH);

        init(panel);

        return panel;
    }

    public abstract void updateWithSelection(T entity);

    protected Consumer<T> getConsumerOnSelection()
    {
        return consumerOnSelection;
    }

    protected Logger getLogger()
    {
        return logger;
    }

    protected abstract void init(JPanel parentPanel);
}
