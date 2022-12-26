// Created: 16.09.2014
package de.freese.mediathek.kodi.model;

/**
 * @author Thomas Freese
 */
public interface Model extends Comparable<Model>
{
    String getName();

    int getPk();

    void setName(final String name);

    void setPk(final int pk);
}
