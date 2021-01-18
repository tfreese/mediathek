/**
 * Created: 27.04.2014
 */

package de.freese.mediathek.services.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thomas Freese
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country implements Comparable<Country>
{
    /**
     * iso_3166_1
     */
    private String iso_3166_1;

    /**
     * name
     */
    private String name;

    /**
     * Erstellt ein neues {@link Country} Object.
     */
    public Country()
    {
        super();
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Country o)
    {
        return this.name.compareTo(o.getName());
    }

    /**
     * @return String
     */
    public String getIso_3166_1()
    {
        return this.iso_3166_1;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @param iso_3166_1 String
     */
    public void setIso_3166_1(final String iso_3166_1)
    {
        this.iso_3166_1 = iso_3166_1;
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
        builder.append("Country [iso_3166_1=");
        builder.append(this.iso_3166_1);
        builder.append(", name=");
        builder.append(this.name);
        builder.append("]");

        return builder.toString();
    }
}
