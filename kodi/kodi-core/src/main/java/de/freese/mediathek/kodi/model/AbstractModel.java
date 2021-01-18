/**
 * Created: 16.09.2014
 */
package de.freese.mediathek.kodi.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Thomas Freese
 */
public abstract class AbstractModel implements IModel
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
     * Erstellt ein neues {@link AbstractModel} Object.
     */
    protected AbstractModel()
    {
        super();
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final IModel o)
    {
        if (o == null)
        {
            return Integer.MIN_VALUE;
        }

        if (this == o)
        {
            return 0;
        }

        return StringUtils.defaultIfBlank(getName(), "").compareTo(StringUtils.defaultIfBlank(o.getName(), ""));
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

        if (obj == null)
        {
            return false;
        }

        if (!(obj instanceof AbstractModel))
        {
            return false;
        }

        AbstractModel other = (AbstractModel) obj;

        if (this.name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!this.name.equals(other.name))
        {
            return false;
        }

        if (this.pk != other.pk)
        {
            return false;
        }

        return true;
    }

    /**
     * @see de.freese.mediathek.kodi.model.IModel#getName()
     */
    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * @see de.freese.mediathek.kodi.model.IModel#getPK()
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
        final int prime = 31;
        int result = 1;

        result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
        result = (prime * result) + this.pk;

        return result;
    }

    /**
     * @see de.freese.mediathek.kodi.model.IModel#setName(java.lang.String)
     */
    @Override
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @see de.freese.mediathek.kodi.model.IModel#setPK(int)
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
