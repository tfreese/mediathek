/**
 * Created: 16.09.2014
 */
package de.freese.mediathek.kodi.model;

/**
 * @author Thomas Freese
 */
public interface IModel extends Comparable<IModel>
{
    /**
     * @return String
     */
    public String getName();

    /**
     * @return int
     */
    public int getPK();

    /**
     * @param name String
     */
    public void setName(final String name);

    /**
     * @param pk int
     */
    public void setPK(final int pk);
}
