/**
 * Created: 27.04.2014
 */

package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Language implements Comparable<Language>
{
    /**
     * iso_639_1
     */
    private String iso_639_1;

    /**
     * name
     */
    private String name;

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Language o)
    {
        return this.name.compareTo(o.getName());
    }

    /**
     * @return String
     */
    public String getIso_639_1()
    {
        return this.iso_639_1;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @param iso_639_1 String
     */
    public void setIso_639_1(final String iso_639_1)
    {
        this.iso_639_1 = iso_639_1;
    }

    /**
     * @param name String
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Language [iso_639_1=");
        builder.append(this.iso_639_1);
        builder.append(", name=");
        builder.append(this.name);
        builder.append("]");

        return builder.toString();
    }
}
