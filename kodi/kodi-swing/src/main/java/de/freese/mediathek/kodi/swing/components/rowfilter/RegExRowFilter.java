// Created: 10.06.2016
package de.freese.mediathek.kodi.swing.components.rowfilter;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.RowFilter;

/**
 * {@link RowFilter} for specific columns with regular expressions.<br>
 * <br>
 * Example: new RegExRowFilter("^a", Pattern.CASE_INSENSITIVE, List.of(2));
 *
 * @author Thomas Freese
 */
public class RegExRowFilter extends AbstractRowFilterIndexed
{
    private final Matcher matcher;

    public RegExRowFilter(final String regex, final int matchFlags, final List<Integer> columns)
    {
        this(Pattern.compile(Objects.requireNonNull(regex), matchFlags), columns);
    }

    public RegExRowFilter(final Pattern regexPattern, final List<Integer> columns)
    {
        super(columns);

        this.matcher = Objects.requireNonNull(regexPattern).matcher("");
    }

    /**
     * @see de.freese.mediathek.kodi.swing.components.rowfilter.AbstractRowFilterIndexed#include(javax.swing.RowFilter.Entry, int)
     */
    @Override
    protected boolean include(final RowFilter.Entry<?, ?> value, final int index)
    {
        this.matcher.reset(value.getStringValue(index));

        return this.matcher.find();
    }
}
