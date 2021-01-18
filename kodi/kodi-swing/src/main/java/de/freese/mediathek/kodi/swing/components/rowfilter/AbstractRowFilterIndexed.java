/**
 * Created: 10.06.2016
 */

package de.freese.mediathek.kodi.swing.components.rowfilter;

import javax.swing.RowFilter;

/**
 * Basis-{@link RowFilter} mit definierbaren Spalten.<br>
 * Geklaut von org.jdesktop.swingx.sort.RowFilters.
 *
 * @author Thomas Freese
 */
public abstract class AbstractRowFilterIndexed extends RowFilter<Object, Object>
{
    /**
     *
     */
    private final int[] columns;

    /**
     * Erstellt ein neues {@link AbstractRowFilterIndexed} Object.
     *
     * @param columns int[]
     */
    protected AbstractRowFilterIndexed(final int...columns)
    {
        super();

        checkIndices(columns);
        this.columns = columns;
    }

    /**
     * Throws an IllegalArgumentException if any of the values in columns are < 0.
     *
     * @param columns int[]
     */
    protected void checkIndices(final int[] columns)
    {
        for (int i = columns.length - 1; i >= 0; i--)
        {
            if (columns[i] < 0)
            {
                throw new IllegalArgumentException("Index must be >= 0");
            }
        }
    }

    /**
     * @see javax.swing.RowFilter#include(javax.swing.RowFilter.Entry)
     */
    @Override
    public boolean include(final Entry<? extends Object, ? extends Object> value)
    {
        int count = value.getValueCount();

        if (this.columns.length > 0)
        {
            for (int i = this.columns.length - 1; i >= 0; i--)
            {
                int index = this.columns[i];

                if (index < count)
                {
                    if (include(value, index))
                    {
                        return true;
                    }
                }
            }
        }
        else
        {
            // Alle Spalten.
            while (--count >= 0)
            {
                if (include(value, count))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @param value {@link javax.swing.RowFilter.Entry}
     * @param index int
     * @return boolean
     */
    protected abstract boolean include(Entry<? extends Object, ? extends Object> value, int index);
}
