/**
 * Created on 11.04.2015 11:14:28
 */
package de.freese.mediathek.kodi.javafx.components;

import java.text.Format;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

/**
 * @author Thomas Freese
 * @param <S> Entity Type
 * @param <T> Attribute Type
 */
public class FormattedTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>>
{
    /**
     *
     */
    private TextAlignment alignment;

    /**
     *
     */
    private Format format;

    /**
     * @see javafx.util.Callback#call(java.lang.Object)
     */
    @Override
    public TableCell<S, T> call(final TableColumn<S, T> param)
    {
        TableCell<S, T> cell = new TableCell<>()
        {
            /**
             * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
             */
            @Override
            public void updateItem(final T item, final boolean empty)
            {
                if (item == getItem())
                {
                    return;
                }

                super.updateItem(item, empty);

                if (item == null)
                {
                    super.setText(null);
                    super.setGraphic(null);
                }
                else if (FormattedTableCellFactory.this.format != null)
                {
                    super.setText(FormattedTableCellFactory.this.format.format(item));
                }
                else if (item instanceof Node)
                {
                    super.setText(null);
                    super.setGraphic((Node) item);
                }
                else
                {
                    super.setText(item.toString());
                    super.setGraphic(null);
                }
            }
        };

        cell.setTextAlignment(this.alignment);

        switch (this.alignment)
        {
            case CENTER:
                cell.setAlignment(Pos.CENTER);
                break;
            case RIGHT:
                cell.setAlignment(Pos.CENTER_RIGHT);
                break;
            default:
                cell.setAlignment(Pos.CENTER_LEFT);
                break;
        }

        return cell;
    }

    /**
     * @return {@link TextAlignment}
     */
    public TextAlignment getAlignment()
    {
        return this.alignment;
    }

    /**
     * @return {@link Format}
     */
    public Format getFormat()
    {
        return this.format;
    }

    /**
     * @param alignment {@link TextAlignment}
     */
    public void setAlignment(final TextAlignment alignment)
    {
        this.alignment = alignment;
    }

    /**
     * @param format {@link Format}
     */
    public void setFormat(final Format format)
    {
        this.format = format;
    }
}
