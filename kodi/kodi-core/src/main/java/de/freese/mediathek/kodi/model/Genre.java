// Created: 13.09.2014
package de.freese.mediathek.kodi.model;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public final class Genre extends AbstractModel {
    private int anzahlFilme;
    private int anzahlSerien;

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Genre genre)) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        return anzahlFilme == genre.anzahlFilme && anzahlSerien == genre.anzahlSerien;
    }

    public int getAnzahlFilme() {
        return anzahlFilme;
    }

    public int getAnzahlSerien() {
        return anzahlSerien;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), anzahlFilme, anzahlSerien);
    }

    public void setAnzahlFilme(final int anzahlFilme) {
        this.anzahlFilme = anzahlFilme;
    }

    public void setAnzahlSerien(final int anzahlSerien) {
        this.anzahlSerien = anzahlSerien;
    }
}
