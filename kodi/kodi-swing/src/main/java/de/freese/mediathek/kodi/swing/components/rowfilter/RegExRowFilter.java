// Created: 10.06.2016
package de.freese.mediathek.kodi.swing.components.rowfilter;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.RowFilter;

/**
 * {@link RowFilter} mit definierbaren Spalten für REGEX-Ausdrücke.<br>
 * Geklaut von org.jdesktop.swingx.sort.RowFilters.<br>
 * <br>
 * Beispiel: new RegExRowFilter(Pattern.CASE_INSENSITIVE, "^a", 2);
 *
 * @author Thomas Freese
 */
public class RegExRowFilter extends AbstractRowFilterIndexed
{
    /**
     *
     */
    private final Matcher matcher;

    /**
     * Erstellt ein neues {@link RegExRowFilter} Object.<br>
     *
     * @param matchFlags int
     * @param regex {@link String}
     * @param columns int[]
     */
    public RegExRowFilter(final int matchFlags, final String regex, final int...columns)
    {
        this(Pattern.compile(Objects.requireNonNull(regex), Objects.requireNonNull(matchFlags)), columns);
    }

    /**
     * Erstellt ein neues {@link RegExRowFilter} Object.
     *
     * @param regexPattern {@link Pattern}
     * @param columns int[]
     */
    public RegExRowFilter(final Pattern regexPattern, final int...columns)
    {
        super(columns);

        this.matcher = Objects.requireNonNull(regexPattern).matcher("");
    }

    /**
     * @see de.freese.mediathek.kodi.swing.components.rowfilter.AbstractRowFilterIndexed#include(javax.swing.RowFilter.Entry, int)
     */
    @Override
    protected boolean include(final RowFilter.Entry<? extends Object, ? extends Object> value, final int index)
    {
        this.matcher.reset(value.getStringValue(index));

        return this.matcher.find();
    }
}
