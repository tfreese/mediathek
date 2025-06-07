// Created: 10.06.2016
package de.freese.mediathek.kodi.swing.components.rowfilter;

import java.util.List;

import javax.swing.RowFilter;

/**
 * {@link RowFilter} for specific columns.<br>
 *
 * @author Thomas Freese
 */
public abstract class AbstractRowFilterIndexed extends RowFilter<Object, Object> {
    private final List<Integer> columns;

    protected AbstractRowFilterIndexed(final List<Integer> columns) {
        super();

        checkIndices(columns);
        this.columns = columns;
    }

    @Override
    public boolean include(final Entry<?, ?> value) {
        int count = value.getValueCount();

        if (!columns.isEmpty()) {
            for (int i = columns.size() - 1; i >= 0; i--) {
                final int index = columns.get(i);

                if (index < count) {
                    if (isInclude(value, index)) {
                        return true;
                    }
                }
            }
        }
        else {
            // Alle Spalten.
            while (--count >= 0) {
                if (isInclude(value, count)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected void checkIndices(final List<Integer> columns) {
        if (columns.stream().anyMatch(c -> c < 0)) {
            throw new IllegalArgumentException("Index must be >= 0");
        }
    }

    protected abstract boolean isInclude(Entry<?, ?> value, int index);
}
