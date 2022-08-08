// Created: 16.09.2014
package de.freese.mediathek.kodi.model;

/**
 * @author Thomas Freese
 */
public interface Model extends Comparable<Model>
{
    /**
     * @return String
     */
    String getName();

    /**
     * @return int
     */
    int getPK();

    /**
     * @param name String
     */
    void setName(final String name);

    /**
     * @param pk int
     */
    void setPK(final int pk);
}
