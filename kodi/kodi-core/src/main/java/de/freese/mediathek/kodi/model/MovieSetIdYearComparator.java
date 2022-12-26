// Created: 15.10.2015
package de.freese.mediathek.kodi.model;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

/**
 * Sortiert Filme in einem Set nach Jahr und nicht nach Name.<br>
 * Comparison method violates its general contract!<br>
 * Reflexivität -> compare(a,a)==0<br>
 * Antisymmetrie -> compare(a,b)==-compare(b,a)<br>
 * Transitivität -> compare(a,b)==-1 && compare(b,c)==-1 => compare(a,c)==-1<br>
 *
 * @author Thomas Freese
 */
public class MovieSetIdYearComparator implements Comparator<Movie>
{
    private static final Collator COLLATOR = Collator.getInstance(Locale.GERMANY);

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(final Movie o1, final Movie o2)
    {
        // if (o1.getPk() == o2.getPk())
        // {
        // return 0;
        // }

        int comp = 0;

        if ((o1.getSetID() > 0) && (o1.getSetID() == o2.getSetID()))
        {
            comp = Integer.compare(o1.getYear(), o2.getYear());
        }

        if (comp == 0)
        {
            String name1 = Objects.toString(o1.getName()).strip();
            String name2 = Objects.toString(o2.getName()).strip();

            comp = COLLATOR.compare(name1, name2);
            // comp = name1.compareTo(name2);
        }

        // System.out.println(comp);
        return comp;
    }
}
