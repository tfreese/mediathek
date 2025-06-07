// Created: 16.09.2014
package de.freese.mediathek.kodi.model;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public abstract class AbstractModel implements Model {
    private String name;
    private int pk = -1;

    @Override
    public int compareTo(final Model o) {
        if (o == null) {
            return -1;
        }

        if (this == o) {
            return 0;
        }

        final String value1 = Objects.toString(getName()).strip();
        final String value2 = Objects.toString(o.getName()).strip();

        return value1.compareTo(value2);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AbstractModel model)) {
            return false;
        }

        return pk == model.pk && Objects.equals(getName(), model.getName());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPk() {
        return pk;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), pk);
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public void setPk(final int pk) {
        this.pk = pk;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [");
        builder.append("pk=").append(pk);
        builder.append(", name=").append(name);
        builder.append("]");

        return builder.toString();
    }
}
