package de.freese.mediathek.kodi.model;

/**
 * @author Thomas Freese
 * @since 16.09.2014
 */
public interface Model extends Comparable<Model> {
    String getName();

    int getPk();

    void setName(String name);

    void setPk(int pk);
}
