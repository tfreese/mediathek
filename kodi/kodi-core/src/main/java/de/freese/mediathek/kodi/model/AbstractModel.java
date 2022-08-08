// Created: 16.09.2014
package de.freese.mediathek.kodi.model;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public abstract class AbstractModel implements Model
{
    /**
     *
     */
    private String name;
    /**
     *
     */
    private int pk = -1;

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Model o)
    {
        if (o == null)
        {
            return -1;
        }

        if (this == o)
        {
            return 0;
        }

        String value1 = Objects.toString(getName()).strip();
        String value2 = Objects.toString(o.getName()).strip();

        return value1.compareTo(value2);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        return (obj != null) && obj instanceof AbstractModel other && Objects.equals(this.name, other.name) && (this.pk == other.pk);
    }

    /**
     * @see Model#getName()
     */
    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * @see Model#getPK()
     */
    @Override
    public int getPK()
    {
        return this.pk;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.name, this.pk);
    }

    /**
     * @see Model#setName(java.lang.String)
     */
    @Override
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @see Model#setPK(int)
     */
    @Override
    public void setPK(final int pk)
    {
        this.pk = pk;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [pk=");
        builder.append(this.pk).append(", name=");
        builder.append(this.name).append("]");

        return builder.toString();
    }
}
